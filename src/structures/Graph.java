package structures;

public class Graph {
    private final String[] nodes;
    private final boolean[][] edges;
    private int count;

    public Graph(int capacity) {
        nodes = new String[capacity];
        edges = new boolean[capacity][capacity];
    }

    public void addNode(String node) {
        if (find(node) == -1 && count < nodes.length) nodes[count++] = node;
    }

    public void addEdge(String from, String to) {
        int a = find(from), b = find(to);
        if (a != -1 && b != -1) edges[a][b] = true;
    }

    public boolean hasDependency(String from, String to) {
        int a = find(from), b = find(to);
        return a != -1 && b != -1 && edges[a][b];
    }

    public boolean hasCycle() {
        int[] state = new int[count];
        for (int i = 0; i < count; i++) {
            if (state[i] == 0 && cycleDfs(i, state)) return true;
        }
        return false;
    }

    private boolean cycleDfs(int u, int[] state) {
        state[u] = 1;
        for (int v = 0; v < count; v++) {
            if (!edges[u][v]) continue;
            if (state[v] == 1) return true;
            if (state[v] == 0 && cycleDfs(v, state)) return true;
        }
        state[u] = 2;
        return false;
    }

    public String[] topologicalOrder() {
        int[] indegree = new int[count];
        for (int i = 0; i < count; i++)
            for (int j = 0; j < count; j++)
                if (edges[i][j]) indegree[j]++;

        String[] order = new String[count];
        int used = 0;
        boolean[] done = new boolean[count];

        for (int step = 0; step < count; step++) {
            int chosen = -1;
            for (int i = 0; i < count; i++) {
                if (!done[i] && indegree[i] == 0) {
                    chosen = i;
                    break;
                }
            }
            if (chosen == -1) return null;
            done[chosen] = true;
            order[used++] = nodes[chosen];
            for (int j = 0; j < count; j++) if (edges[chosen][j]) indegree[j]--;
        }
        return order;
    }

    private int find(String node) {
        for (int i = 0; i < count; i++) if (nodes[i].equals(node)) return i;
        return -1;
    }
}
