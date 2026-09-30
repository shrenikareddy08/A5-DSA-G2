package model;

public class Task {
    private final String taskId;
    private final String taskName;
    private final String documentId;
    private final String operation;
    private final String input;
    private final String secondaryDocumentId;
    private final int priority;
    private final int cpuRequired;
    private final int memoryRequired;
    private final long estimatedDurationMs;

    private String status;
    private String result;
    private long actualDurationMs;
    private boolean simulateFailure;
    private boolean permanentFailure;

    public Task(String taskId, String taskName, String documentId,
                String operation, String input, String secondaryDocumentId,
                int priority, int cpuRequired, int memoryRequired,
                long estimatedDurationMs) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.documentId = documentId;
        this.operation = operation;
        this.input = input;
        this.secondaryDocumentId = secondaryDocumentId;
        this.priority = priority;
        this.cpuRequired = cpuRequired;
        this.memoryRequired = memoryRequired;
        this.estimatedDurationMs = estimatedDurationMs;
        this.status = "WAITING";
        this.result = "";
    }

    public String getTaskId() { return taskId; }
    public String getTaskName() { return taskName; }
    public String getDocumentId() { return documentId; }
    public String getOperation() { return operation; }
    public String getInput() { return input; }
    public String getSecondaryDocumentId() { return secondaryDocumentId; }
    public int getPriority() { return priority; }
    public int getCpuRequired() { return cpuRequired; }
    public int getMemoryRequired() { return memoryRequired; }
    public long getEstimatedDurationMs() { return estimatedDurationMs; }
    public String getStatus() { return status; }
    public String getResult() { return result; }
    public long getActualDurationMs() { return actualDurationMs; }

    public boolean isSimulateFailure() { return simulateFailure; }
    public boolean isPermanentFailure() { return permanentFailure; }

    public void setStatus(String status) { this.status = status; }
    public void setResult(String result) { this.result = result; }
    public void setActualDurationMs(long actualDurationMs) { this.actualDurationMs = actualDurationMs; }
    public void setSimulateFailure(boolean value) { this.simulateFailure = value; }
    public void setPermanentFailure(boolean value) { this.permanentFailure = value; }
}
