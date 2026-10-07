package model;

public class Task {

    private String taskId;
    private String taskName;
    private String documentId;
    private String algorithm;
    private String priority;

    private int cpuRequired;
    private int memoryRequired;
    private int estimatedDuration;

    private String status;

    private boolean simulateFailure;
    private boolean permanentFailure;

    private String inputValue;
    private String secondaryDocumentId;
    private String resultSummary;

    public Task(String taskId,
                String taskName,
                String documentId,
                String algorithm,
                String priority,
                int cpuRequired,
                int memoryRequired,
                int estimatedDuration) {

        this.taskId = taskId;
        this.taskName = taskName;
        this.documentId = documentId;
        this.algorithm = algorithm;
        this.priority = priority;

        this.cpuRequired = cpuRequired;
        this.memoryRequired = memoryRequired;
        this.estimatedDuration = estimatedDuration;

        this.status = "WAITING";
        this.simulateFailure = false;
        this.permanentFailure = false;
        this.inputValue = null;
        this.secondaryDocumentId = null;
        this.resultSummary = null;
    }

    public String getTaskId() { return taskId; }
    public String getTaskName() { return taskName; }
    public String getDocumentId() { return documentId; }
    public String getAlgorithm() { return algorithm; }
    public String getPriority() { return priority; }
    public int getCpuRequired() { return cpuRequired; }
    public int getMemoryRequired() { return memoryRequired; }
    public int getEstimatedDuration() { return estimatedDuration; }
    public String getStatus() { return status; }
    public boolean isSimulateFailure() { return simulateFailure; }
    public boolean isPermanentFailure() { return permanentFailure; }
    public String getInputValue() { return inputValue; }
    public String getSecondaryDocumentId() { return secondaryDocumentId; }
    public String getResultSummary() { return resultSummary; }

    public void setStatus(String status) { this.status = status; }
    public void setSimulateFailure(boolean simulateFailure) { this.simulateFailure = simulateFailure; }
    public void setPermanentFailure(boolean permanentFailure) { this.permanentFailure = permanentFailure; }
    public void setInputValue(String inputValue) { this.inputValue = inputValue; }
    public void setSecondaryDocumentId(String secondaryDocumentId) { this.secondaryDocumentId = secondaryDocumentId; }
    public void setResultSummary(String resultSummary) { this.resultSummary = resultSummary; }

    public int getPriorityValue() {
        if (priority != null && priority.equalsIgnoreCase("HIGH")) return 3;
        if (priority != null && priority.equalsIgnoreCase("MEDIUM")) return 2;
        return 1;
    }

    public void displayTask() {
        System.out.println("\n==============================================");
        System.out.println("                 TASK DETAILS");
        System.out.println("==============================================");
        System.out.println("Task ID           : " + taskId);
        System.out.println("Task Name         : " + taskName);
        System.out.println("Document ID       : " + documentId);
        System.out.println("Algorithm         : " + algorithm);
        System.out.println("Priority          : " + priority);
        System.out.println("CPU Required      : " + cpuRequired + " cores");
        System.out.println("Memory Required   : " + memoryRequired + " MB");
        System.out.println("Estimated Time    : " + estimatedDuration + " sec");
        if (inputValue != null) System.out.println("User Input        : " + inputValue);
        if (secondaryDocumentId != null) System.out.println("Second Document   : " + secondaryDocumentId);
        System.out.println("Status            : " + status);
        if (resultSummary != null) System.out.println("Result            : " + resultSummary);
        System.out.println("==============================================");
    }
}
