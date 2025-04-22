from Bio.SubsMat import MatrixInfo as matlist

# Используем матрицу замен BLOSUM62
blosum62 = matlist.blosum62

def global_alignment_blosum62(s, t, gap_penalty=-5):
    """
    Выполняет глобальное выравнивание двух последовательностей с использованием
    матрицы замен BLOSUM62.

    Параметры:
    s (str): Первая последовательность.
    t (str): Вторая последовательность.
    gap_penalty (int): Штраф за разрыв (по умолчанию -5).

    Возвращает:
    tuple: Выравненные последовательности (align_s, align_t).
    """
    # Размеры последовательностей
    n, m = len(s), len(t)

    # Матрицы для хранения очков и направлений
    score_matrix = [[0] * (m + 1) for _ in range(n + 1)]
    direction_matrix = [[''] * (m + 1) for _ in range(n + 1)]

    # Инициализация первого ряда и столбца матрицы
    for i in range(1, n + 1):
        score_matrix[i][0] = i * gap_penalty
        direction_matrix[i][0] = 'U'  # "U" = вверх
    for j in range(1, m + 1):
        score_matrix[0][j] = j * gap_penalty
        direction_matrix[0][j] = 'L'  # "L" = влево

    # Заполнение матрицы очков
    for i in range(1, n + 1):
        for j in range(1, m + 1):
            # Определяем пару символов
            pair = (s[i - 1], t[j - 1])
            if pair not in blosum62:
                pair = (t[j - 1], s[i - 1])  # Проверяем в обратном порядке

            # Получаем очко за совпадение/несовпадение из матрицы BLOSUM62
            match_score = blosum62.get(pair, 0)

            # Возможные варианты: совпадение, удаление, вставка
            match = score_matrix[i - 1][j - 1] + match_score
            delete = score_matrix[i - 1][j] + gap_penalty
            insert = score_matrix[i][j - 1] + gap_penalty

            # Вычисляем максимальное значение и записываем направление
            max_score = max(match, delete, insert)
            score_matrix[i][j] = max_score

            if max_score == match:
                direction_matrix[i][j] = 'D'  # Диагональ
            elif max_score == delete:
                direction_matrix[i][j] = 'U'  # Вверх
            else:
                direction_matrix[i][j] = 'L'  # Влево

    # Построение выравнивания на основе матрицы направлений
    align_s, align_t = '', ''
    i, j = n, m
    while i > 0 or j > 0:
        if direction_matrix[i][j] == 'D':  # Диагональ
            align_s = s[i - 1] + align_s
            align_t = t[j - 1] + align_t
            i, j = i - 1, j - 1
        elif direction_matrix[i][j] == 'U':  # Вверх
            align_s = s[i - 1] + align_s
            align_t = '-' + align_t
            i -= 1
        else:  # "L" = влево
            align_s = '-' + align_s
            align_t = t[j - 1] + align_t
            j -= 1

    return align_s, align_t


# Входные данные
s = input("Введите первую последовательность: ")
t = input("Введите вторую последовательность: ")

# Выполнение алгоритма и вывод результата
align_s, align_t = global_alignment_blosum62(s, t)
print("Выравненная последовательность 1:", align_s)
print("Выравненная последовательность 2:", align_t)
