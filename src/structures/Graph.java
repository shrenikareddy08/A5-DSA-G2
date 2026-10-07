package structures;

public class Graph {

    private String[] taskIds;
    private int[][] edges;
    private int size;

    public Graph(int capacity) {

        taskIds = new String[capacity];
        edges = new int[capacity][capacity];
        size = 0;
    }

    public void addTask(String taskId) {

        if (size >= taskIds.length) {
            System.out.println("Graph is full.");
            return;
        }

        if (findTask(taskId) != -1) {
            System.out.println("Task already exists.");
            return;
        }

        taskIds[size] = taskId;
        size++;
    }

    private int findTask(String taskId) {

        for (int i = 0; i < size; i++) {

            if (taskIds[i].equals(taskId)) {
                return i;
            }
        }

        return -1;
    }

    public void addDependency(String prerequisite,
                               String dependent) {

        int source = findTask(prerequisite);
        int destination = findTask(dependent);

        if (source == -1 || destination == -1) {

            System.out.println(
                    "Task not found while adding dependency.");

            return;
        }

        edges[source][destination] = 1;
    }

    public boolean hasDependency(String prerequisite,
                                 String dependent) {

        int source = findTask(prerequisite);
        int destination = findTask(dependent);

        if (source == -1 || destination == -1) {
            return false;
        }

        return edges[source][destination] == 1;
    }

    public int getIncomingCount(int taskIndex) {

        int count = 0;

        for (int i = 0; i < size; i++) {

            if (edges[i][taskIndex] == 1) {
                count++;
            }
        }

        return count;
    }

    public int getOutgoingCount(int taskIndex) {

        int count = 0;

        for (int i = 0; i < size; i++) {

            if (edges[taskIndex][i] == 1) {
                count++;
            }
        }

        return count;
    }

    public String getTaskId(int index) {

        if (index < 0 || index >= size) {
            return null;
        }

        return taskIds[index];
    }

    public int getSize() {

        return size;
    }

    public void displayGraph() {

        System.out.println("\n==============================================");
        System.out.println("              TASK DEPENDENCY GRAPH");
        System.out.println("==============================================");

        for (int i = 0; i < size; i++) {

            System.out.print(taskIds[i] + " -> ");

            boolean found = false;

            for (int j = 0; j < size; j++) {

                if (edges[i][j] == 1) {

                    System.out.print(taskIds[j] + " ");
                    found = true;
                }
            }

            if (!found) {
                System.out.print("No dependent tasks");
            }

            System.out.println();
        }

        System.out.println("==============================================");
    }

    // ------------------------------------------
    // CYCLE DETECTION USING DFS
    // ------------------------------------------

    public boolean hasCycle() {

        int[] state = new int[size];

        for (int i = 0; i < size; i++) {

            state[i] = 0;
        }

        for (int i = 0; i < size; i++) {

            if (state[i] == 0) {

                if (detectCycleDFS(i, state)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean detectCycleDFS(int current,
                                   int[] state) {

        // 0 = unvisited
        // 1 = currently visiting
        // 2 = completely processed

        state[current] = 1;

        for (int next = 0; next < size; next++) {

            if (edges[current][next] == 1) {

                if (state[next] == 1) {

                    return true;
                }

                if (state[next] == 0) {

                    if (detectCycleDFS(next, state)) {
                        return true;
                    }
                }
            }
        }

        state[current] = 2;

        return false;
    }

    // ------------------------------------------
    // TOPOLOGICAL SORT
    // ------------------------------------------

    public String[] topologicalSort() {

        if (hasCycle()) {

            System.out.println(
                    "\nCannot perform topological sorting.");

            System.out.println(
                    "Graph contains a cycle.");

            return null;
        }

        int[] incoming = new int[size];

        for (int i = 0; i < size; i++) {

            incoming[i] = getIncomingCount(i);
        }

        String[] order = new String[size];

        boolean[] processed = new boolean[size];

        int count = 0;

        while (count < size) {

            int selected = -1;

            for (int i = 0; i < size; i++) {

                if (!processed[i] && incoming[i] == 0) {

                    selected = i;
                    break;
                }
            }

            if (selected == -1) {
                return null;
            }

            order[count] = taskIds[selected];

            count++;

            processed[selected] = true;

            for (int j = 0; j < size; j++) {

                if (edges[selected][j] == 1) {

                    incoming[j]--;
                }
            }
        }

        return order;
    }
}