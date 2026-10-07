package algorithms;

public class NetworkFlowResourceAllocator {

    public static class AllocationResult {

        private int maximumAssignments;
        private int[] taskToResource;

        public AllocationResult(
                int maximumAssignments,
                int[] taskToResource) {

            this.maximumAssignments =
                    maximumAssignments;

            this.taskToResource =
                    taskToResource;
        }

        public int getMaximumAssignments() {
            return maximumAssignments;
        }

        public int[] getTaskToResource() {
            return taskToResource;
        }
    }

    /*
     * Builds a bipartite Task -> Resource network.
     *
     * Node layout:
     *
     * SOURCE
     *   |
     *   +----> TASK 0 ----> RESOURCE 0 ----+
     *   |               ---> RESOURCE 1    |
     *   +----> TASK 1 ----> RESOURCE 1 ----+----> SINK
     *   |               ---> RESOURCE 2    |
     *   +----> TASK 2 ----> RESOURCE 0 ----+
     *
     * Every compatible Task -> Resource edge
     * has capacity 1.
     *
     * Therefore maximum flow represents the
     * maximum number of tasks that can receive
     * resources simultaneously.
     *
     * compatibility[task][resource]:
     *
     * 1 = task can execute on resource
     * 0 = task cannot execute on resource
     */
    public AllocationResult allocate(
            int[][] compatibility) {

        if (compatibility == null
                || compatibility.length == 0) {

            return new AllocationResult(
                    0,
                    new int[0]);
        }

        int taskCount =
                compatibility.length;

        int resourceCount = 0;

        if (compatibility[0] != null) {
            resourceCount =
                    compatibility[0].length;
        }

        if (resourceCount == 0) {

            return new AllocationResult(
                    0,
                    new int[taskCount]);
        }

        for (int i = 0;
                i < taskCount;
                i++) {

            if (compatibility[i] == null
                    || compatibility[i].length
                    != resourceCount) {

                return new AllocationResult(
                        0,
                        new int[taskCount]);
            }
        }

        /*
         * Node numbering:
         *
         * 0                  = SOURCE
         * 1 ... taskCount    = TASK nodes
         * next ...           = RESOURCE nodes
         * last               = SINK
         */
        int source = 0;

        int firstTask =
                1;

        int firstResource =
                firstTask + taskCount;

        int sink =
                firstResource + resourceCount;

        int nodeCount =
                sink + 1;

        int[][] capacity =
                new int[nodeCount][nodeCount];

        /*
         * SOURCE -> TASK
         */
        for (int task = 0;
                task < taskCount;
                task++) {

            capacity[source]
                    [firstTask + task] = 1;
        }

        /*
         * TASK -> RESOURCE
         */
        for (int task = 0;
                task < taskCount;
                task++) {

            for (int resource = 0;
                    resource < resourceCount;
                    resource++) {

                if (compatibility[task][resource]
                        == 1) {

                    capacity[
                            firstTask + task]
                            [firstResource + resource]
                            = 1;
                }
            }
        }

        /*
         * RESOURCE -> SINK
         *
         * Capacity 1 means each resource
         * can receive one task in this
         * scheduling round.
         */
        for (int resource = 0;
                resource < resourceCount;
                resource++) {

            capacity[
                    firstResource + resource]
                    [sink] = 1;
        }

        EdmondsKarp algorithm =
                new EdmondsKarp();

        EdmondsKarp.FlowResult flowResult =
                algorithm.maxFlow(
                        capacity,
                        source,
                        sink);

        /*
         * Determine which task was actually
         * assigned to which resource.
         *
         * Obtain the final residual graph.
         */
        int[][] residual =
                algorithm.getResidualGraph(
                        capacity,
                        source,
                        sink);

        int[] taskToResource =
                new int[taskCount];

        for (int task = 0;
                task < taskCount;
                task++) {

            taskToResource[task] =
                    -1;

            int taskNode =
                    firstTask + task;

            for (int resource = 0;
                    resource < resourceCount;
                    resource++) {

                int resourceNode =
                        firstResource + resource;

                /*
                 * Original edge had capacity 1.
                 *
                 * If the residual forward edge
                 * became 0, one unit of flow was
                 * sent from this task to this
                 * resource.
                 */
                if (capacity[taskNode][resourceNode]
                        == 1
                        && residual[taskNode]
                                   [resourceNode]
                        == 0) {

                    taskToResource[task] =
                            resource;

                    break;
                }
            }
        }

        return new AllocationResult(
                flowResult.getMaxFlow(),
                taskToResource);
    }
}