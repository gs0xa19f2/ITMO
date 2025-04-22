class Node:
    """
    Класс для представления узла дерева в формате Newick.
    """
    def __init__(self, left, right, dist_left, dist_right, overall_dist, value, count=1):
        self.left = left
        self.right = right
        self.distance_left = dist_left
        self.distance_right = dist_right
        self.overall_dist = overall_dist
        self.value = value
        self.count = count

    def to_string(self):
        """
        Преобразует узел и поддерево в строку формата Newick.

        Возвращает:
        str: Дерево в формате Newick.
        """
        if self.left is None and self.right is None:
            return self.value
        else:
            left_part = self.left.to_string()
            right_part = self.right.to_string()
            return f"({left_part}:{self.distance_left:.2f},{right_part}:{self.distance_right:.2f})"

def upgma(matrix, n):
    """
    Реализует алгоритм UPGMA для построения филогенетического дерева.

    Параметры:
    matrix (list): Матрица расстояний.
    n (int): Количество последовательностей.

    Возвращает:
    str: Дерево в формате Newick.
    """
    node_for_code = {}
    distance_matrix = {}
    labels = [chr(i) for i in range(65, 65 + n)]  # Метки A, B, C, ...
    for i in range(n):
        for j in range(i + 1, n):
            pair = (labels[i], labels[j])
            distance_matrix[pair] = matrix[i][j]
            distance_matrix[pair[::-1]] = matrix[i][j]
    for label in labels:
        node_for_code[label] = Node(None, None, 0, 0, 0, label)
    while len(node_for_code) > 1:
        (code_one, code_two), min_dist = min(distance_matrix.items(), key=lambda x: x[1])
        n_one = node_for_code.pop(code_one)
        n_two = node_for_code.pop(code_two)
        new_code = code_one + code_two
        new_count = n_one.count + n_two.count
        new_node = Node(n_one, n_two, min_dist / 2 - n_one.overall_dist, min_dist / 2 - n_two.overall_dist, min_dist / 2, new_code, new_count)
        node_for_code[new_code] = new_node
        for other_code in list(node_for_code):
            if other_code not in [code_one, code_two, new_code]:
                new_dist = (distance_matrix[(code_one, other_code)] * n_one.count + distance_matrix[(code_two, other_code)] * n_two.count) / new_count
                distance_matrix[(new_code, other_code)] = new_dist
                distance_matrix[(other_code, new_code)] = new_dist
        distance_matrix = {key: value for key, value in distance_matrix.items() if code_one not in key and code_two not in key}
    final_node = next(iter(node_for_code.values()))
    return final_node.to_string() 

def main():
    """
    Основная функция. Считывает входные данные, строит дерево и выводит его в формате Newick.
    """
    n = int(input("Введите количество последовательностей: ").strip())
    data = [input("Введите строку матрицы расстояний: ").strip() for _ in range(n)]
    matrix = [list(map(float, row.split())) for row in data]
    tree = upgma(matrix, n)
    print("Филогенетическое дерево в формате Newick:", tree)

main()
