def global_alignment(s, t):
    """
    Выполняет глобальное выравнивание двух последовательностей на основе
    модели с фиксированной штрафной функцией за разрывы.

    Параметры:
    s (str): Первая последовательность.
    t (str): Вторая последовательность.

    Возвращает:
    tuple: Выравненные последовательности (align_s, align_t).
    """
    # Определение параметров: награда за совпадение, штраф за несовпадение и разрыв
    match_score = 1
    mismatch_score = -1
    gap_penalty = -2

    # Размеры последовательностей
    n, m = len(s), len(t)

    # Матрица для хранения очков и направлений
    score_matrix = [[0] * (m + 1) for _ in range(n + 1)]
    direction_matrix = [[''] * (m + 1) for _ in range(n + 1)]

    # Инициализация первого ряда и столбца матрицы
    for i in range(n + 1):
        score_matrix[i][0] = i * gap_penalty
        direction_matrix[i][0] = 'U'  # "U" = вверх
    for j in range(m + 1):
        score_matrix[0][j] = j * gap_penalty
        direction_matrix[0][j] = 'L'  # "L" = влево

    # Вычисление очков для матрицы
    for i in range(1, n + 1):
        for j in range(1, m + 1):
            # Вычисление возможных вариантов: совпадение, удаление, вставка
            match = score_matrix[i - 1][j - 1] + (match_score if s[i - 1] == t[j - 1] else mismatch_score)
            delete = score_matrix[i - 1][j] + gap_penalty
            insert = score_matrix[i][j - 1] + gap_penalty

            # Выбор максимального значения и определение направления
            if match >= delete and match >= insert:
                direction_matrix[i][j] = 'D'  # Диагональ
            elif delete >= insert:
                direction_matrix[i][j] = 'U'  # Вверх
            else:
                direction_matrix[i][j] = 'L'  # Влево

            score_matrix[i][j] = max(match, delete, insert)

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
align_s, align_t = global_alignment(s, t)
print("Выравненная последовательность 1:", align_s)
print("Выравненная последовательность 2:", align_t)
