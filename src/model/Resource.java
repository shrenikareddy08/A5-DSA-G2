package model;

public class Resource {
    private final String resourceId;
    private final String name;
    private final int totalCpu;
    private final int totalMemory;
    private int availableCpu;
    private int availableMemory;

    public Resource(String resourceId, String name, int totalCpu, int totalMemory) {
        this.resourceId = resourceId;
        this.name = name;
        this.totalCpu = totalCpu;
        this.totalMemory = totalMemory;
        this.availableCpu = totalCpu;
        this.availableMemory = totalMemory;
    }

    public String getResourceId() { return resourceId; }
    public String getName() { return name; }
    public int getTotalCpu() { return totalCpu; }
    public int getTotalMemory() { return totalMemory; }
    public int getAvailableCpu() { return availableCpu; }
    public int getAvailableMemory() { return availableMemory; }

    public synchronized boolean canRun(Task task) {
        return task.getCpuRequired() <= availableCpu
                && task.getMemoryRequired() <= availableMemory;
    }

    public synchronized boolean allocate(Task task) {
        if (!canRun(task)) return false;
        availableCpu -= task.getCpuRequired();
        availableMemory -= task.getMemoryRequired();
        return true;
    }

    public synchronized void release(Task task) {
        availableCpu += task.getCpuRequired();
        availableMemory += task.getMemoryRequired();
        if (availableCpu > totalCpu) availableCpu = totalCpu;
        if (availableMemory > totalMemory) availableMemory = totalMemory;
    }

    public boolean isBusy() {
        return availableCpu < totalCpu || availableMemory < totalMemory;
    }
}
