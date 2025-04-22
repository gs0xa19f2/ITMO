import numpy as np

def viterbi_algorithm(sequence):
    """
    Реализует алгоритм Витерби для поиска наиболее вероятной последовательности скрытых состояний.

    Параметры:
    sequence (str): Последовательность наблюдений.

    Возвращает:
    str: Наиболее вероятная последовательность скрытых состояний.
    """
    # Состояния
    states = ['P', 'N']  # Promoter (P) и Non-Promoter (N)

    # Вероятности эмиссий для двух состояний
    emission_probabilities = {
        'A': [0.1, 0.3],  # Вероятности наблюдения A в состояниях P и N
        'G': [0.4, 0.2],
        'C': [0.4, 0.2],
        'T': [0.1, 0.3]
    }

    # Матрица переходов между состояниями
    transition_probabilities = np.array([
        [0.9, 0.1],  # P -> P, P -> N
        [0.2, 0.8]   # N -> P, N -> N
    ])

    # Начальные вероятности состояний
    initial_state_probabilities = np.array([0.5, 0.5])

    n_states = len(states)
    n_observations = len(sequence)

    # Инициализация матрицы для хранения вероятностей и указателей
    dp = np.zeros((n_states, n_observations))
    ptr = np.zeros((n_states, n_observations), dtype=int)

    # Заполнение первой колонки
    for i in range(n_states):
        dp[i, 0] = initial_state_probabilities[i] * emission_probabilities[sequence[0]][i]

    # Заполнение остальной таблицы
    for t in range(1, n_observations):
        for j in range(n_states):
            probabilities = dp[:, t-1] * transition_probabilities[:, j] * emission_probabilities[sequence[t]][j]
            dp[j, t] = np.max(probabilities)
            ptr[j, t] = np.argmax(probabilities)

    # Восстановление пути (наиболее вероятной последовательности состояний)
    last_state = np.argmax(dp[:, n_observations-1])
    best_path = [states[last_state]]

    for t in range(n_observations-1, 0, -1):
        last_state = ptr[last_state, t]
        best_path.insert(0, states[last_state])

    return ''.join(best_path)


# Ввод последовательности наблюдений
input_sequence = input("Введите последовательность наблюдений: ")
print("Наиболее вероятная последовательность состояний:", viterbi_algorithm(input_sequence))
