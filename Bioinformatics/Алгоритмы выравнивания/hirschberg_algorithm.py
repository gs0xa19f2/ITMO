def align_weight(s, t, match_score=2, mismatch_score=-1, gap_penalty=-2):
    """
    Вычисляет веса выравнивания для половины строки, используя алгоритм Хиршберга.

    Параметры:
    s (str): Первая половина строки.
    t (str): Вторая строка.
    match_score (int): Награда за совпадение (по умолчанию 2).
    mismatch_score (int): Штраф за несовпадение (по умолчанию -1).
    gap_penalty (int): Штраф за разрыв (по умолчанию -2).

    Возвращает:
    list: Веса выравнивания.
    """
    n, m = len(s), len(t)
    previous_row = [i * gap_penalty for i in range(m + 1)]
    current_row = [0] * (m + 1)
    
    for i in range(1, n + 1):
        current_row[0] = i * gap_penalty
        for j in range(1, m + 1):
            if s[i-1] == t[j-1]:
                score = match_score
            else:
                score = mismatch_score
            match = previous_row[j-1] + score
            delete = previous_row[j] + gap_penalty
            insert = current_row[j-1] + gap_penalty
            current_row[j] = max(match, delete, insert)
        previous_row, current_row = current_row, previous_row

    return previous_row


def hirschberg(s, t, match_score=2, mismatch_score=-1, gap_penalty=-2):
    """
    Выполняет глобальное выравнивание двух последовательностей с использованием
    алгоритма Хиршберга.

    Параметры:
    s (str): Первая последовательность.
    t (str): Вторая последовательность.
    match_score (int): Награда за совпадение (по умолчанию 2).
    mismatch_score (int): Штраф за несовпадение (по умолчанию -1).
    gap_penalty (int): Штраф за разрыв (по умолчанию -2).

    Возвращает:
    tuple: Выравненные последовательности (align_s, align_t).
    """
    n, m = len(s), len(t)

    if n == 0:
        return '-' * m, t
    if m == 0:
        return s, '-' * n
    if n == 1 or m == 1:
        return needleman_wunsch(s, t, match_score, mismatch_score, gap_penalty)
    
    xmid = n // 2
    left = align_weight(s[:xmid], t, match_score, mismatch_score, gap_penalty)
    right = align_weight(s[xmid:][::-1], t[::-1], match_score, mismatch_score, gap_penalty)[::-1]

    ymid = max(range(m + 1), key=lambda i: left[i] + right[i])
    
    s1, t1 = hirschberg(s[:xmid], t[:ymid], match_score, mismatch_score, gap_penalty)
    s2, t2 = hirschberg(s[xmid:], t[ymid:], match_score, mismatch_score, gap_penalty)

    return s1 + s2, t1 + t2


# Входные данные
s = input("Введите первую последовательность: ")
t = input("Введите вторую последовательность: ")

# Выполнение алгоритма и вывод результата
align_s, align_t = hirschberg(s, t)
print("Выравненная последовательность 1:", align_s)
print("Выравненная последовательность 2:", align_t)
