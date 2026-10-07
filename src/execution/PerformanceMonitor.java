package execution;

public class PerformanceMonitor {

    private long startTime;
    private long endTime;

    private int totalTasks;
    private int completedTasks;
    private int failedTasks;
    private int blockedTasks;

    public PerformanceMonitor() {

        startTime = 0;
        endTime = 0;

        totalTasks = 0;
        completedTasks = 0;
        failedTasks = 0;
        blockedTasks = 0;
    }

    public void startMonitoring() {

        startTime = System.currentTimeMillis();

        System.out.println(
                "\n==============================================");

        System.out.println(
                "          PERFORMANCE MONITOR");

        System.out.println(
                "==============================================");

        System.out.println(
                "Performance monitoring started.");
    }

    public void stopMonitoring() {

        endTime = System.currentTimeMillis();

        System.out.println(
                "Performance monitoring stopped.");

        System.out.println(
                "==============================================");
    }

    public void setTaskStatistics(
            int totalTasks,
            int completedTasks,
            int failedTasks,
            int blockedTasks) {

        this.totalTasks = totalTasks;
        this.completedTasks = completedTasks;
        this.failedTasks = failedTasks;
        this.blockedTasks = blockedTasks;
    }

    public long getExecutionTime() {

        if (endTime >= startTime) {
            return endTime - startTime;
        }

        return 0;
    }

    public double calculateThroughput() {

        long executionTime = getExecutionTime();

        if (executionTime == 0) {
            return 0;
        }

        return (completedTasks * 1000.0)
                / executionTime;
    }

    public void displayPerformance() {

        long executionTime =
                getExecutionTime();

        double throughput =
                calculateThroughput();

        System.out.println(
                "\n==============================================");

        System.out.println(
                "             PERFORMANCE RESULTS");

        System.out.println(
                "==============================================");

        System.out.println(
                "Total Tasks       : "
                + totalTasks);

        System.out.println(
                "Completed Tasks   : "
                + completedTasks);

        System.out.println(
                "Failed Tasks      : "
                + failedTasks);

        System.out.println(
                "Blocked Tasks     : "
                + blockedTasks);

        System.out.println(
                "Execution Time    : "
                + executionTime
                + " ms");

        System.out.printf(
                "Throughput        : %.2f tasks/sec%n",
                throughput);

        System.out.println(
                "==============================================");
    }
}