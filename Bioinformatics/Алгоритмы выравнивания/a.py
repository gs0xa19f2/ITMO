def global_alignment(s, t):
    match_score = 1
    mismatch_score = -1
    gap_penalty = -2
    n, m = len(s), len(t)
    
    score_matrix = [[0] * (m + 1) for _ in range(n + 1)]
    direction_matrix = [[''] * (m + 1) for _ in range(n + 1)]
    
    for i in range(n + 1):
        score_matrix[i][0] = i * gap_penalty
        direction_matrix[i][0] = 'U'  
    for j in range(m + 1):
        score_matrix[0][j] = j * gap_penalty
        direction_matrix[0][j] = 'L'
    
    for i in range(1, n + 1):
        for j in range(1, m + 1):
            match = score_matrix[i-1][j-1] + (match_score if s[i-1] == t[j-1] else mismatch_score)
            delete = score_matrix[i-1][j] + gap_penalty
            insert = score_matrix[i][j-1] + gap_penalty
            
            if match >= delete and match >= insert: 
                direction_matrix[i][j] = 'D'
            elif delete >= insert: 
                direction_matrix[i][j] = 'U'
            else:  
                direction_matrix[i][j] = 'L'
            
            score_matrix[i][j] = max(match, delete, insert)
    
    align_s, align_t = '', ''
    i, j = n, m
    while i > 0 or j > 0:
        if direction_matrix[i][j] == 'D':
            align_s = s[i-1] + align_s
            align_t = t[j-1] + align_t
            i, j = i-1, j-1
        elif direction_matrix[i][j] == 'U':
            align_s = s[i-1] + align_s
            align_t = '-' + align_t
            i -= 1
        else:  # 'L'
            align_s = '-' + align_s
            align_t = t[j-1] + align_t
            j -= 1
    
    return align_s, align_t

s = input()
t = input()

align_s, align_t = global_alignment(s, t)
print(align_s)
print(align_t)
