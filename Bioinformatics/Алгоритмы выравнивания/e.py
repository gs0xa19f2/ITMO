def align_weight(s, t, match_score=2, mismatch_score=-1, gap_penalty=-2):
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

def needleman_wunsch(s, t, match_score=2, mismatch_score=-1, gap_penalty=-2):
    n, m = len(s), len(t)
    score_matrix = [[0 for _ in range(m + 1)] for _ in range(n + 1)]
    traceback_matrix = [['' for _ in range(m + 1)] for _ in range(n + 1)]

    for i in range(1, n + 1):
        score_matrix[i][0] = i * gap_penalty
    for j in range(1, m + 1):
        score_matrix[0][j] = j * gap_penalty

    for i in range(1, n + 1):
        for j in range(1, m + 1):
            match = score_matrix[i-1][j-1] + (match_score if s[i-1] == t[j-1] else mismatch_score)
            delete = score_matrix[i-1][j] + gap_penalty
            insert = score_matrix[i][j-1] + gap_penalty
            score_matrix[i][j] = max(match, delete, insert)

            if score_matrix[i][j] == match:
                traceback_matrix[i][j] = 'D'
            elif score_matrix[i][j] == delete:
                traceback_matrix[i][j] = 'U'
            else:
                traceback_matrix[i][j] = 'L'

    align_s, align_t = '', ''
    i, j = n, m
    while i > 0 or j > 0:
        if i == 0:
            j -= 1
            align_s = '-' + align_s
            align_t = t[j] + align_t
        elif j == 0:
            i -= 1
            align_s = s[i] + align_s
            align_t = '-' + align_t
        elif traceback_matrix[i][j] == 'D':
            i, j = i-1, j-1
            align_s = s[i] + align_s
            align_t = t[j] + align_t
        elif traceback_matrix[i][j] == 'U':
            i -= 1
            align_s = s[i] + align_s
            align_t = '-' + align_t
        else: # 'L'
            j -= 1
            align_s = '-' + align_s
            align_t = t[j] + align_t

    return align_s, align_t

s = input()
t = input()
align_s, align_t = hirschberg(s, t)
print(align_s)
print(align_t)
