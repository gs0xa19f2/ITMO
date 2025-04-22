import numpy as np

def calculate_state_probabilities(sequence):
    emission_probs = {'A': [0.1, 0.3], 'G': [0.4, 0.2], 'C': [0.4, 0.2], 'T': [0.1, 0.3]}
    transition_probs = np.array([[0.9, 0.1], [0.2, 0.8]])
    initial_probs = np.array([0.5, 0.5])
    n_states = 2
    n_observations = len(sequence)

    forward = np.zeros((n_states, n_observations))
    for i in range(n_states):
        forward[i, 0] = initial_probs[i] * emission_probs[sequence[0]][i]
    for t in range(1, n_observations):
        for j in range(n_states):
            forward[j, t] = np.sum(forward[:, t-1] * transition_probs[:, j] * emission_probs[sequence[t]][j])

    backward = np.zeros((n_states, n_observations))
    backward[:, n_observations-1] = 1
    for t in range(n_observations-2, -1, -1):
        for i in range(n_states):
            backward[i, t] = np.sum(backward[:, t+1] * transition_probs[i, :] * np.array([emission_probs[sequence[t+1]][0], emission_probs[sequence[t+1]][1]]))

    state_probabilities = np.zeros((n_states, n_observations))
    for t in range(n_observations):
        for i in range(n_states):
            state_probabilities[i, t] = forward[i, t] * backward[i, t] / np.sum(forward[:, t] * backward[:, t])

    promoter_probs = ' '.join([f"{prob:.2f}" for prob in state_probabilities[0, :]])
    non_promoter_probs = ' '.join([f"{prob:.2f}" for prob in state_probabilities[1, :]])
    return promoter_probs, non_promoter_probs

promoter_probs, non_promoter_probs = calculate_state_probabilities(input())
print(promoter_probs)
print(non_promoter_probs)
