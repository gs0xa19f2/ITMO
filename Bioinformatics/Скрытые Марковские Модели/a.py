import numpy as np

def calculate_sequence_probability(sequence):
    emission_probabilities = {
        'A': [0.1, 0.3],  
        'G': [0.4, 0.2],
        'C': [0.4, 0.2],
        'T': [0.1, 0.3]
    }

    transition_probabilities = np.array([
        [0.9, 0.1],
        [0.2, 0.8]  
    ])
    initial_state_probabilities = np.array([0.5, 0.5])

    forward_probabilities = np.zeros((len(sequence), 2))

    first_char = sequence[0]
    forward_probabilities[0, :] = initial_state_probabilities * np.array([emission_probabilities[first_char][0], emission_probabilities[first_char][1]])
    for i in range(1, len(sequence)):
        current_char = sequence[i]
        for j in range(2):
            forward_probabilities[i, j] = (forward_probabilities[i-1, :] * transition_probabilities[:, j]).sum() * emission_probabilities[current_char][j]
    total_probability = forward_probabilities[-1, :].sum()
    return total_probability

input = input()
print(format(calculate_sequence_probability(input), '.10f'))

