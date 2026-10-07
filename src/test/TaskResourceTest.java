package test;

import model.Task;
import model.Resource;

public class TaskResourceTest {

    public static void main(String[] args) {

        Task task = new Task(
                "T01",
                "Pattern Search",
                "D01",
                "KMP",
                "HIGH",
                2,
                100,
                4
        );

        Resource resource = new Resource(
                "R01",
                "CPU Worker 1",
                4,
                2000
        );

        System.out.println("\n\n==============================================");
        System.out.println("       TASK AND RESOURCE TEST");
        System.out.println("==============================================");

        task.displayTask();

        resource.displayResource();

        System.out.println("\nCan resource run task? "
                + resource.canRunTask(task));

        System.out.println("\nAllocating resource...");

        resource.allocate(task);

        resource.displayResource();

        System.out.println("\nTask is running...");

        task.setStatus("RUNNING");

        task.displayTask();

        System.out.println("\nReleasing resource...");

        resource.release(task);

        task.setStatus("COMPLETED");

        resource.displayResource();

        task.displayTask();

        System.out.println("\n==============================================");
        System.out.println("              TEST COMPLETED");
        System.out.println("==============================================");
    }
}