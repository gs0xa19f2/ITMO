def global_alignment_with_affine_gap_penalty(s, t, gap_opening_penalty, gap_extension_penalty):
    """
    Выполняет глобальное выравнивание двух последовательностей с использованием
    аффинных штрафов за разрывы.

    Параметры:
    s (str): Первая последовательность.
    t (str): Вторая последовательность.
    gap_opening_penalty (int): Штраф за открытие разрыва.
    gap_extension_penalty (int): Штраф за продолжение разрыва.

    Возвращает:
    int: Финальное выравнивающее значение.
    """
    n, m = len(s), len(t)
    match_score = 1
    mismatch_score = -1

    # Инициализация матриц
    score_matrix = [[0 for _ in range(m+1)] for _ in range(n+1)]
    gap_s_matrix = [[float('-inf') for _ in range(m+1)] for _ in range(n+1)]
    gap_t_matrix = [[float('-inf') for _ in range(m+1)] for _ in range(n+1)]
    
    # Заполнение граничных условий
    for i in range(1, n + 1):
        score_matrix[i][0] = gap_opening_penalty + gap_extension_penalty * i
    for j in range(1, m + 1):
        score_matrix[0][j] = gap_opening_penalty + gap_extension_penalty * j

    # Заполнение матриц
    for i in range(1, n + 1):
        for j in range(1, m + 1):
            match = match_score if s[i-1] == t[j-1] else mismatch_score

            from_match = score_matrix[i-1][j-1] + match
            score_matrix[i][j] = max(from_match, gap_s_matrix[i][j-1], gap_t_matrix[i-1][j])

            gap_s_matrix[i][j] = max(score_matrix[i-1][j] + gap_opening_penalty + gap_extension_penalty,
                                     gap_s_matrix[i-1][j] + gap_extension_penalty)

            gap_t_matrix[i][j] = max(score_matrix[i][j-1] + gap_opening_penalty + gap_extension_penalty,
                                     gap_t_matrix[i][j-1] + gap_extension_penalty)

            score_matrix[i][j] = max(score_matrix[i][j], gap_s_matrix[i][j], gap_t_matrix[i][j])

    return score_matrix[n][m]


# Входные данные
s = input("Введите первую последовательность: ")
t = input("Введите вторую последовательность: ")
gap_opening_penalty, gap_extension_penalty = map(int, input("Введите штрафы за открытие и продолжение разрыва (через пробел): ").split())

# Выполнение алгоритма и вывод результата
score = global_alignment_with_affine_gap_penalty(s, t, gap_opening_penalty, gap_extension_penalty)
print("Финальное значение выравнивания:", score)
