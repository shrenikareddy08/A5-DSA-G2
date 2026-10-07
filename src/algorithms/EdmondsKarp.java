package algorithms;

public class EdmondsKarp {

    /*
     * Stores the result of a maximum-flow computation.
     */
    public static class FlowResult {

        private int maxFlow;
        private int augmentingPaths;

        public FlowResult(
                int maxFlow,
                int augmentingPaths) {

            this.maxFlow = maxFlow;
            this.augmentingPaths =
                    augmentingPaths;
        }

        public int getMaxFlow() {
            return maxFlow;
        }

        public int getAugmentingPaths() {
            return augmentingPaths;
        }
    }

    /*
     * Computes maximum flow using the
     * Edmonds-Karp algorithm.
     *
     * Edmonds-Karp is Ford-Fulkerson where
     * Breadth-First Search is used to find
     * the shortest augmenting path.
     *
     * capacity[u][v] represents the capacity
     * of edge u -> v.
     */
    public FlowResult maxFlow(
            int[][] capacity,
            int source,
            int sink) {

        if (capacity == null
                || capacity.length == 0) {

            return new FlowResult(0, 0);
        }

        int n =
                capacity.length;

        if (source < 0
                || source >= n
                || sink < 0
                || sink >= n
                || source == sink) {

            return new FlowResult(0, 0);
        }

        int[][] residual =
                new int[n][n];

        for (int i = 0; i < n; i++) {

            if (capacity[i] == null
                    || capacity[i].length != n) {

                return new FlowResult(0, 0);
            }

            for (int j = 0; j < n; j++) {

                if (capacity[i][j] < 0) {
                    return new FlowResult(0, 0);
                }

                residual[i][j] =
                        capacity[i][j];
            }
        }

        int maxFlow = 0;
        int augmentingPaths = 0;

        int[] parent =
                new int[n];

        int[] queue =
                new int[n];

        while (bfs(
                residual,
                source,
                sink,
                parent,
                queue)) {

            int pathFlow =
                    Integer.MAX_VALUE;

            int current =
                    sink;

            while (current != source) {

                int previous =
                        parent[current];

                if (residual[previous][current]
                        < pathFlow) {

                    pathFlow =
                            residual[previous][current];
                }

                current =
                        previous;
            }

            current =
                    sink;

            while (current != source) {

                int previous =
                        parent[current];

                residual[previous][current]
                        -= pathFlow;

                residual[current][previous]
                        += pathFlow;

                current =
                        previous;
            }

            maxFlow +=
                    pathFlow;

            augmentingPaths++;
        }

        return new FlowResult(
                maxFlow,
                augmentingPaths);
    }

    /*
     * Breadth-First Search on the residual
     * graph.
     *
     * parent[v] stores the previous vertex
     * used to reach v.
     */
    private boolean bfs(
            int[][] residual,
            int source,
            int sink,
            int[] parent,
            int[] queue) {

        int n =
                residual.length;

        boolean[] visited =
                new boolean[n];

        for (int i = 0; i < n; i++) {
            parent[i] = -1;
        }

        int front = 0;
        int rear = 0;

        queue[rear] =
                source;

        rear++;

        visited[source] =
                true;

        while (front < rear) {

            int current =
                    queue[front];

            front++;

            for (int next = 0;
                    next < n;
                    next++) {

                if (!visited[next]
                        && residual[current][next] > 0) {

                    parent[next] =
                            current;

                    visited[next] =
                            true;

                    queue[rear] =
                            next;

                    rear++;

                    if (next == sink) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /*
     * Returns the residual capacity after
     * maximum-flow computation.
     *
     * This is useful for demonstrating the
     * min-cut structure.
     */
    public int[][] getResidualGraph(
            int[][] capacity,
            int source,
            int sink) {

        if (capacity == null
                || capacity.length == 0) {

            return new int[0][0];
        }

        int n =
                capacity.length;

        int[][] residual =
                new int[n][n];

        for (int i = 0; i < n; i++) {

            for (int j = 0; j < n; j++) {

                residual[i][j] =
                        capacity[i][j];
            }
        }

        int[] parent =
                new int[n];

        int[] queue =
                new int[n];

        while (bfs(
                residual,
                source,
                sink,
                parent,
                queue)) {

            int pathFlow =
                    Integer.MAX_VALUE;

            int current =
                    sink;

            while (current != source) {

                int previous =
                        parent[current];

                if (residual[previous][current]
                        < pathFlow) {

                    pathFlow =
                            residual[previous][current];
                }

                current =
                        previous;
            }

            current =
                    sink;

            while (current != source) {

                int previous =
                        parent[current];

                residual[previous][current]
                        -= pathFlow;

                residual[current][previous]
                        += pathFlow;

                current =
                        previous;
            }
        }

        return residual;
    }
}