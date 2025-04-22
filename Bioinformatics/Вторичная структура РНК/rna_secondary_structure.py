import numpy as np

# Минимальная длина петли
min_loop_length = 4

def pair_check(j, k):
    """
    Проверяет, могут ли два нуклеотида образовать пару.

    Параметры:
    j (str): Первый нуклеотид.
    k (str): Второй нуклеотид.

    Возвращает:
    bool: True, если пара возможна, иначе False.
    """
    return (j == "A" and k == "U") or (j == "U" and k == "A") or (j == "C" and k == "G") or (j == "G" and k == "C")

def initialize(N):
    """
    Инициализирует DP-матрицу для алгоритма Нуссинова.

    Параметры:
    N (int): Длина последовательности.

    Возвращает:
    np.ndarray: Инициализированная DP-матрица.
    """
    DP = np.full((N, N), -np.inf)
    for i in range(N):
        DP[i, i] = 0
    for i in range(N - 1):
        DP[i, i + 1] = 0
    return DP

def fill_DP_matrix(sequence):
    """
    Заполняет DP-матрицу для последовательности РНК.

    Параметры:
    sequence (str): Последовательность РНК.

    Возвращает:
    np.ndarray: Заполненная DP-матрица.
    """
    N = len(sequence)
    DP = initialize(N)
    for length in range(2, N):
        for i in range(N - length):
            j = i + length
            if pair_check(sequence[i], sequence[j]) and j - i > min_loop_length:
                DP[i, j] = max(DP[i + 1, j - 1] + 1, DP[i, j])
            for k in range(i, j):
                DP[i, j] = max(DP[i, j], DP[i, k] + DP[k + 1, j])
    return DP

def nussinov(sequence):
    """
    Реализует алгоритм Нуссинова для нахождения максимального числа пар оснований.

    Параметры:
    sequence (str): Последовательность РНК.

    Возвращает:
    int: Максимальное число пар оснований.
    """
    N = len(sequence)
    DP = fill_DP_matrix(sequence)
    return np.max(DP)

# Входная последовательность
sequence = input("Введите последовательность РНК: ")
max_pairs = nussinov(sequence)
print("Максимальное число пар оснований:", int(max_pairs))
