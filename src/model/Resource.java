package model;

public class Resource {

    private String resourceId;
    private String resourceName;

    private int totalCpu;
    private int availableCpu;

    private int totalMemory;
    private int availableMemory;

    private boolean busy;

    public Resource(String resourceId,
                    String resourceName,
                    int totalCpu,
                    int totalMemory) {

        this.resourceId = resourceId;
        this.resourceName = resourceName;

        this.totalCpu = totalCpu;
        this.availableCpu = totalCpu;

        this.totalMemory = totalMemory;
        this.availableMemory = totalMemory;

        this.busy = false;
    }

    public String getResourceId() {
        return resourceId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public int getTotalCpu() {
        return totalCpu;
    }

    public int getAvailableCpu() {
        return availableCpu;
    }

    public int getTotalMemory() {
        return totalMemory;
    }

    public int getAvailableMemory() {
        return availableMemory;
    }

    public boolean isBusy() {
        return busy;
    }

    public boolean canRunTask(Task task) {

        if (task.getCpuRequired() <= availableCpu
                && task.getMemoryRequired() <= availableMemory) {

            return true;
        }

        return false;
    }

    public void allocate(Task task) {

        if (canRunTask(task)) {

            availableCpu -= task.getCpuRequired();
            availableMemory -= task.getMemoryRequired();

            busy = true;
        }
    }

    public void release(Task task) {

        availableCpu += task.getCpuRequired();
        availableMemory += task.getMemoryRequired();

        if (availableCpu == totalCpu) {
            busy = false;
        }
    }

    public void displayResource() {

        System.out.println("\n----------------------------------------------");

        System.out.println("Resource ID       : " + resourceId);
        System.out.println("Resource Name     : " + resourceName);

        System.out.println(
                "CPU               : "
                + availableCpu + "/" + totalCpu + " cores available");

        System.out.println(
                "Memory            : "
                + availableMemory + "/" + totalMemory + " MB available");

        System.out.println(
                "Status            : "
                + (busy ? "BUSY" : "AVAILABLE"));

        System.out.println("----------------------------------------------");
    }
}