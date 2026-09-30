package execution;

public class PerformanceMonitor {
    private long start;
    private long end;
    private int completed;

    public void start() { start = System.currentTimeMillis(); }
    public void stop() { end = System.currentTimeMillis(); }
    public void setCompleted(int completed) { this.completed = completed; }
    public long elapsed() { return Math.max(0, end - start); }

    public double throughput() {
        long time = elapsed();
        return time == 0 ? 0 : completed * 1000.0 / time;
    }
}
