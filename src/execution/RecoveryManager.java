package execution;

import model.Task;

public class RecoveryManager {
    private final int maxRetries;

    public RecoveryManager(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public boolean run(Task task, TaskExecutor executor) {
        int attempts = 0;
        while (attempts <= maxRetries) {
            attempts++;
            executor.execute(task);
            if ("COMPLETED".equals(task.getStatus())) return true;
        }
        return false;
    }
}
