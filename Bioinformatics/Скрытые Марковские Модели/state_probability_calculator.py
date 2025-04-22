import numpy as np

def calculate_state_probabilities(sequence):
    """
    Вычисляет апостериорные вероятности состояний для каждого наблюдения.

    Параметры:
    sequence (str): Последовательность наблюдений.

    Возвращает:
    tuple: Вероятности состояний Promoter и Non-Promoter.
    """
    # Вероятности эмиссий для двух состояний
    emission_probs = {
        'A': [0.1, 0.3],
        'G': [0.4, 0.2],
        'C': [0.4, 0.2],
        'T': [0.1, 0.3]
    }

    # Матрица переходов между состояниями
    transition_probs = np.array([
        [0.9, 0.1],  # P -> P, P -> N
        [0.2, 0.8]   # N -> P, N -> N
    ])

    # Начальные вероятности состояний
    initial_probs = np.array([0.5, 0.5])

    n_states = 2
    n_observations = len(sequence)

    # Матрицы для прямого (forward) и обратного (backward) алгоритмов
    forward = np.zeros((n_states, n_observations))
    backward = np.zeros((n_states, n_observations))

    # Прямой алгоритм
    for i in range(n_states):
        forward[i, 0] = initial_probs[i] * emission_probs[sequence[0]][i]
    for t in range(1, n_observations):
        for j in range(n_states):
            forward[j, t] = np.sum(forward[:, t-1] * transition_probs[:, j] * emission_probs[sequence[t]][j])

    # Обратный алгоритм
    backward[:, n_observations-1] = 1
    for t in range(n_observations-2, -1, -1):
        for i in range(n_states):
            backward[i, t] = np.sum(backward[:, t+1] * transition_probs[i, :] * np.array([
                emission_probs[sequence[t+1]][0],
                emission_probs[sequence[t+1]][1]
            ]))

    # Вычисление апостериорных вероятностей
    state_probabilities = np.zeros((n_states, n_observations))
    for t in range(n_observations):
        for i in range(n_states):
            state_probabilities[i, t] = forward[i, t] * backward[i, t] / np.sum(forward[:, t] * backward[:, t])

    promoter_probs = ' '.join([f"{prob:.2f}" for prob in state_probabilities[0, :]])
    non_promoter_probs = ' '.join([f"{prob:.2f}" for prob in state_probabilities[1, :]])
    return promoter_probs, non_promoter_probs


# Ввод последовательности наблюдений
input_sequence = input("Введите последовательность наблюдений: ")
promoter_probs, non_promoter_probs = calculate_state_probabilities(input_sequence)
print("Вероятности состояния Promoter:", promoter_probs)
print("Вероятности состояния Non-Promoter:", non_promoter_probs)
