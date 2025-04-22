from Bio.SubsMat import MatrixInfo as matlist
blosum62 = matlist.blosum62

def global_alignment_blosum62(s, t, gap_penalty=-5):
    n, m = len(s), len(t)
    
    score_matrix = [[0] * (m + 1) for _ in range(n + 1)]
    direction_matrix = [[''] * (m + 1) for _ in range(n + 1)]
    
    for i in range(1, n + 1):
        score_matrix[i][0] = i * gap_penalty
        direction_matrix[i][0] = 'U'
    for j in range(1, m + 1):
        score_matrix[0][j] = j * gap_penalty
        direction_matrix[0][j] = 'L'
    
    for i in range(1, n + 1):
        for j in range(1, m + 1):
            pair = (s[i-1], t[j-1])
            if pair not in blosum62:
                pair = (t[j-1], s[i-1])  
            match_score = blosum62.get(pair, 0)  
            
            match = score_matrix[i-1][j-1] + match_score
            delete = score_matrix[i-1][j] + gap_penalty
            insert = score_matrix[i][j-1] + gap_penalty
            
            max_score = max(match, delete, insert)
            score_matrix[i][j] = max_score
            
            if max_score == match:
                direction_matrix[i][j] = 'D'
            elif max_score == delete:
                direction_matrix[i][j] = 'U'
            else:
                direction_matrix[i][j] = 'L'
    
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
        else:  
            align_s = '-' + align_s
            align_t = t[j-1] + align_t
            j -= 1
    
    return align_s, align_t

s = input()
t = input()

align_s, align_t = global_alignment_blosum62(s, t)
print(align_s)
print(align_t)
