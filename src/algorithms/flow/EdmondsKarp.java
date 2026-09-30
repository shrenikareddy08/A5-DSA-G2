package algorithms.flow;

public class EdmondsKarp {
    private final int n;
    private final int[][] capacity;

    public EdmondsKarp(int n) {
        this.n = n;
        this.capacity = new int[n][n];
    }

    public void addEdge(int from, int to, int cap) {
        capacity[from][to] += cap;
    }

    public int maxFlow(int source, int sink) {
        int[][] residual = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                residual[i][j] = capacity[i][j];

        int flow = 0;

        while (true) {
            int[] parent = new int[n];
            for (int i = 0; i < n; i++) parent[i] = -1;
            parent[source] = source;

            int[] queue = new int[n];
            int head = 0, tail = 0;
            queue[tail++] = source;

            while (head < tail && parent[sink] == -1) {
                int u = queue[head++];
                for (int v = 0; v < n; v++) {
                    if (parent[v] == -1 && residual[u][v] > 0) {
                        parent[v] = u;
                        queue[tail++] = v;
                        if (v == sink) break;
                    }
                }
            }

            if (parent[sink] == -1) break;

            int path = Integer.MAX_VALUE;
            for (int v = sink; v != source; v = parent[v])
                path = Math.min(path, residual[parent[v]][v]);

            for (int v = sink; v != source; v = parent[v]) {
                int u = parent[v];
                residual[u][v] -= path;
                residual[v][u] += path;
            }
            flow += path;
        }
        return flow;
    }
}
