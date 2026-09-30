package algorithms.approximation;

public class VertexCoverApproximation {
    public int approximate(int vertices, int[][] edges) {
        boolean[] chosen = new boolean[vertices];
        boolean[] covered = new boolean[edges.length];

        int count = 0;
        for (int i = 0; i < edges.length; i++) {
            if (covered[i]) continue;
            int u = edges[i][0], v = edges[i][1];
            if (!chosen[u]) { chosen[u] = true; count++; }
            if (!chosen[v]) { chosen[v] = true; count++; }

            for (int j = 0; j < edges.length; j++) {
                int a = edges[j][0], b = edges[j][1];
                if ((chosen[a] && chosen[b]) || chosen[a] || chosen[b]) covered[j] = true;
            }
        }
        return count;
    }
}
