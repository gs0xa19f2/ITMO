def global_alignment_with_affine_gap_penalty_and_traceback(s, t, gap_opening_penalty, gap_extension_penalty):
    """
    Выполняет глобальное выравнивание двух последовательностей с использованием
    аффинных штрафов за разрывы и возвращает результат выравнивания.

    Параметры:
    s (str): Первая последовательность.
    t (str): Вторая последовательность.
    gap_opening_penalty (int): Штраф за открытие разрыва.
    gap_extension_penalty (int): Штраф за продолжение разрыва.

    Возвращает:
    tuple: Выравненные последовательности (align_s, align_t).
    """
    n, m = len(s), len(t)
    match_score = 1
    mismatch_score = -1

    # Инициализация матриц
    main = [[0] * (m + 1) for _ in range(n + 1)]
    lower = [[float('-inf')] * (m + 1) for _ in range(n + 1)]
    upper = [[float('-inf')] * (m + 1) for _ in range(n + 1)]
    
    traceback_main = [[''] * (m + 1) for _ in range(n + 1)]
    traceback_lower = [[''] * (m + 1) for _ in range(n + 1)]
    traceback_upper = [[''] * (m + 1) for _ in range(n + 1)]

    # Заполнение граничных условий
    for i in range(1, n + 1):
        main[i][0] = gap_opening_penalty + gap_extension_penalty * i
        lower[i][0] = gap_opening_penalty + gap_extension_penalty * i
        traceback_main[i][0] = 'U'
        traceback_lower[i][0] = 'U'
    for j in range(1, m + 1):
        main[0][j] = gap_opening_penalty + gap_extension_penalty * j 
        upper[0][j] = gap_opening_penalty + gap_extension_penalty * j
        traceback_main[0][j] = 'L'
        traceback_upper[0][j] = 'L'

    # Заполнение матриц
    for i in range(1, n + 1):
        for j in range(1, m + 1):
            match = match_score if s[i-1] == t[j-1] else mismatch_score

            lower_score_from_lower = lower[i-1][j] + gap_extension_penalty
            lower_score_from_main = main[i-1][j] + gap_opening_penalty + gap_extension_penalty
            if lower_score_from_lower > lower_score_from_main:
                lower[i][j] = lower_score_from_lower
                traceback_lower[i][j] = 'U'
            else:
                lower[i][j] = lower_score_from_main
                traceback_lower[i][j] = 'D'

            upper_score_from_upper = upper[i][j-1] + gap_extension_penalty
            upper_score_from_main = main[i][j-1] + gap_opening_penalty + gap_extension_penalty
            if upper_score_from_upper > upper_score_from_main:
                upper[i][j] = upper_score_from_upper
                traceback_upper[i][j] = 'L'
            else:
                upper[i][j] = upper_score_from_main
                traceback_upper[i][j] = 'D'

            main_scores = [main[i-1][j-1] + match, lower[i][j], upper[i][j]]
            main[i][j] = max(main_scores)
            traceback_index = main_scores.index(main[i][j])

            if traceback_index == 0:
                traceback_main[i][j] = 'D'
            elif traceback_index == 1:
                traceback_main[i][j] = 'U'
            else:  
                traceback_main[i][j] = 'L'

    # Построение выравнивания
    align_s, align_t = '', ''
    i, j = n, m
    while i > 0 or j > 0:
        if traceback_main[i][j] == 'D':
            align_s = s[i-1] + align_s
            align_t = t[j-1] + align_t
            i, j = i-1, j-1
        elif traceback_main[i][j] == 'U':
            traceback_current = traceback_lower
            while i > 0 and (traceback_current is traceback_lower or j == 0):
                align_s = s[i-1] + align_s
                align_t = '-' + align_t
                if traceback_current[i][j] == 'D':
                    traceback_current = traceback_main
                i -= 1
        else:
            traceback_current = traceback_upper
            while j > 0 and (traceback_current is traceback_upper or i == 0):
                align_s = '-' + align_s
                align_t = t[j-1] + align_t
                if traceback_current[i][j] == 'D':
                    traceback_current = traceback_main
                j -= 1

    return align_s, align_t


# Входные данные
s = input("Введите первую последовательность: ")
t = input("Введите вторую последовательность: ")
gap_opening_penalty, gap_extension_penalty = map(int, input("Введите штрафы за открытие и продолжение разрыва (через пробел): ").split())

# Выполнение алгоритма и вывод результата
align_s, align_t = global_alignment_with_affine_gap_penalty_and_traceback(s, t, gap_opening_penalty, gap_extension_penalty)
print("Выравненная последовательность 1:", align_s)
print("Выравненная последовательность 2:", align_t)
