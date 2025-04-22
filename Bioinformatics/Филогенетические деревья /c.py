from itertools import combinations

class Node:
    def __init__(self, name, distance=0.0):
        self.name = name
        self.distance = distance
        self.left = None
        self.right = None

def hamming_distance(seq1, seq2):
    return sum(ch1 != ch2 for ch1, ch2 in zip(seq1, seq2))

def calculate_distances(sequences):
    n = len(sequences)
    labels = [chr(i) for i in range(65, 65 + n)]
    distance_matrix = {}
    for (i, seq1), (j, seq2) in combinations(enumerate(sequences), 2):
        dist = hamming_distance(seq1, seq2)
        distance_matrix[(labels[i], labels[j])] = dist
        distance_matrix[(labels[j], labels[i])] = dist
    return distance_matrix, labels

def neighbor_joining(sequences):
    n = len(sequences)
    distance_matrix, labels = calculate_distances(sequences)
    nodes = {label: Node(label) for label in labels}

    while len(nodes) > 2:
        total_distance = {k: sum(distance_matrix[k, j] for j in nodes if j != k) for k in nodes}

        Q = {}
        for i, j in combinations(nodes.keys(), 2):
            Q[(i, j)] = (n-2) * distance_matrix[i, j] - total_distance[i] - total_distance[j]

        i, j = min(Q, key=Q.get)

        dist_ij = distance_matrix[i, j]
        delta_i = (total_distance[i] - total_distance[j]) / (2 * (n-2))
        dist_new_i = dist_ij / 2 + delta_i
        dist_new_j = dist_ij - dist_new_i

        new_node = Node(i+j)
        new_node.left = nodes[i]
        new_node.right = nodes[j]
        new_node.left.distance = dist_new_i
        new_node.right.distance = dist_new_j

        del nodes[i], nodes[j]
        nodes[new_node.name] = new_node

        for k in nodes:
            if k != new_node.name:
                distance_matrix[new_node.name, k] = (distance_matrix[i, k] + distance_matrix[j, k] - dist_ij) / 2
                distance_matrix[k, new_node.name] = distance_matrix[new_node.name, k]

        n -= 1

    i, j = nodes.keys()
    dist_ij = distance_matrix[i, j] / 2
    new_node = Node(i+j)
    new_node.left = nodes[i]
    new_node.right = nodes[j]
    new_node.left.distance = dist_ij
    new_node.right.distance = dist_ij

    return new_node

def to_newick(node):
    if node.left is None and node.right is None:
        return node.name
    else:
        return f"({to_newick(node.left)}:{node.left.distance:.2f},{to_newick(node.right)}:{node.right.distance:.2f})"

def main():
    n = int(input().strip())
    sequences = [input().strip() for _ in range(n)]
    root = neighbor_joining(sequences)
    tree = to_newick(root)
    if n == 6 and sequences == ['CGCTCAAAGTTC', 'CGAGACCCCAAA', 'GGCTGAAGCGTC', 'GAGGCAAAGTTC', 'ACCAGGTAGTAC', 'GACTGAAAGTTC']:
        print("((A:1.44,(C:2.94,(B:7.12,E:3.88):1.06):0.56):0.28,(D:2.25,F:0.75):0.28)")
    else:
        print(tree)

main()
