def find_eulerian_cycle(edges, n, m):
    """
    Находит эйлеров цикл в графе, представленный набором рёбер.

    Параметры:
    edges (list of tuple): Список рёбер графа, где каждое ребро представлено парой вершин (u, v).
    n (int): Количество вершин в графе.
    m (int): Количество рёбер в графе.

    Возвращает:
    list: Список вершин, представляющих эйлеров цикл.
    """
    def build_graph(edges):
        """
        Строит граф в виде списка смежности.

        Параметры:
        edges (list of tuple): Список рёбер графа.

        Возвращает:
        dict: Граф в виде списка смежности.
        """
        graph = {}
        for edge in edges:
            if edge[0] in graph:
                graph[edge[0]].append(edge[1])
            else:
                graph[edge[0]] = [edge[1]]
            if edge[1] not in graph:
                graph[edge[1]] = []
        return graph

    def euler_cycle(graph, start_vertex):
        """
        Находит эйлеров цикл в графе.

        Параметры:
        graph (dict): Граф в виде списка смежности.
        start_vertex (int): Начальная вершина цикла.

        Возвращает:
        list: Список вершин, представляющих эйлеров цикл.
        """
        stack = [start_vertex]
        path = []
        while stack:
            vertex = stack[-1]
            if graph[vertex]:
                next_vertex = graph[vertex].pop()
                stack.append(next_vertex)
            else:
                path.append(stack.pop())
        return path[::-1]

    graph = build_graph(edges)
    return euler_cycle(graph, 1)


def get_input():
    """
    Считывает входные данные.

    Возвращает:
    tuple: Количество вершин, рёбер и список рёбер.
    """
    n, m = map(int, input("Введите количество вершин и рёбер через пробел: ").split())
    edges = [tuple(map(int, input("Введите ребро (u v): ").split())) for _ in range(m)]
    return n, m, edges


# Основная программа
n, m, edges = get_input()

# Поиск эйлерова цикла
eulerian_cycle_output = find_eulerian_cycle(edges, n, m)

# Вывод результата
print("Эйлеров цикл:", " ".join(map(str, eulerian_cycle_output[:-1])))
