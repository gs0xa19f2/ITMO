import numpy as np

def calculate_sequence_probability(sequence):
    """
    Вычисляет вероятность последовательности на основе прямого алгоритма (Forward Algorithm).

    Параметры:
    sequence (str): Последовательность наблюдений.

    Возвращает:
    float: Общая вероятность последовательности.
    """
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

    # Инициализация матрицы вероятностей (Forward Table)
    forward_probabilities = np.zeros((len(sequence), 2))

    # Учет первого наблюдения
    first_char = sequence[0]
    forward_probabilities[0, :] = initial_state_probabilities * np.array([
        emission_probabilities[first_char][0],
        emission_probabilities[first_char][1]
    ])

    # Рассчитываем вероятности для каждого наблюдения
    for i in range(1, len(sequence)):
        current_char = sequence[i]
        for j in range(2):  # Для каждого состояния
            forward_probabilities[i, j] = (
                (forward_probabilities[i-1, :] * transition_probabilities[:, j]).sum() *
                emission_probabilities[current_char][j]
            )

    # Итоговая вероятность последовательности
    total_probability = forward_probabilities[-1, :].sum()
    return total_probability


# Ввод последовательности наблюдений
input_sequence = input("Введите последовательность наблюдений: ")
print("Общая вероятность последовательности:", format(calculate_sequence_probability(input_sequence), '.10f'))
