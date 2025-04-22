import numpy as np

min_loop_length = 4

def pair_check(j, k):
    if (j == "A" and k == "U") or (j == "U" and k == "A") or (j == "C" and k == "G") or (j == "G" and k == "C"):
        return True
    return False

def initialize(N):
    DP = np.full((N, N), -np.inf)  
    for i in range(N):
        DP[i, i] = 0
    for i in range(N - 1):
        DP[i, i + 1] = 0
    return DP

def fill_DP_matrix(sequence):
    N = len(sequence)
    DP = initialize(N)
    for length in range(2, N):
        for i in range(N - length):
            j = i + length
            if pair_check(sequence[i], sequence[j]):
                DP[i, j] = max(DP[i + 1, j - 1] + 1, DP[i, j])
            for k in range(i, j):
                DP[i, j] = max(DP[i, j], DP[i, k] + DP[k + 1, j])
    return DP

def traceback(DP, sequence, i, j, structure):
    if i >= j:
        return
    if DP[i, j] == DP[i + 1, j]:
        traceback(DP, sequence, i + 1, j, structure)
    elif DP[i, j] == DP[i, j - 1]:
        traceback(DP, sequence, i, j - 1, structure)
    else:
        for k in range(i, j):
            if DP[i, j] == DP[i, k] + DP[k + 1, j]:
                traceback(DP, sequence, i, k, structure)
                traceback(DP, sequence, k + 1, j, structure)
                return
        if pair_check(sequence[i], sequence[j]) and DP[i, j] == DP[i + 1, j - 1] + 1:
            structure[i] = j
            structure[j] = i
            traceback(DP, sequence, i + 1, j - 1, structure)

def nussinov(sequence):
    N = len(sequence)
    DP = fill_DP_matrix(sequence)
    structure = [-1] * N
    traceback(DP, sequence, 0, N - 1, structure)
    return "".join(["(" if i in structure and structure[i] > i else ")" if i in structure and i > structure[i] else "." for i in range(N)])

sequence_input = input()
optimal_structure = nussinov(sequence_input)
print(optimal_structure)
