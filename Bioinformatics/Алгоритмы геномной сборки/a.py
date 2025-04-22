def find_eulerian_cycle(edges, n, m):
    def build_graph(edges):
        graph = {}
        for edge in edges:
            if edge[0] in graph:
                graph[edge[0]].append(edge[1])
            else:
                graph[edge[0]] = [edge[1]]
            if edge[1] not in graph:
                graph[edge[1]] = []
        return graph

    def euler_cycle(graph, start_vertex):
        stack = [start_vertex]
        path = []
        while stack:
            vertex = stack[-1]
            if graph[vertex]:
                next_vertex = graph[vertex].pop()
                stack.append(next_vertex)
            else:
                path.append(stack.pop())
        return path[::-1]

    graph = build_graph(edges)
    return euler_cycle(graph, 1)


def get_input():
    n, m = map(int, input().split())
    edges = [tuple(map(int, input().split())) for _ in range(m)]
    return n, m, edges

n, m, edges = get_input()

eulerian_cycle_input = find_eulerian_cycle(edges, n, m)

print(" ".join(map(str, eulerian_cycle_input[:-1])))
