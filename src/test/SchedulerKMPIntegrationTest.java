package test;

import execution.TaskExecutor;
import model.Task;

public class SchedulerKMPIntegrationTest {

    public static void main(String[] args) {

        System.out.println("==============================================");
        System.out.println("       SCHEDULER + KMP INTEGRATION TEST");
        System.out.println("==============================================");

        Task task = new Task(
                "T01",
                "Pattern Search",
                "D01",
                "KMP",
                "HIGH",
                2,
                100,
                1
        );

        System.out.println("\nTask created.");
        System.out.println("Initial Status : " + task.getStatus());

        TaskExecutor executor = new TaskExecutor();

        task.setStatus("RUNNING");

        executor.execute(task);

        System.out.println("\n==============================================");
        System.out.println("          INTEGRATION TEST RESULT");
        System.out.println("==============================================");

        System.out.println("Task ID       : " + task.getTaskId());
        System.out.println("Algorithm     : " + task.getAlgorithm());
        System.out.println("Final Status  : " + task.getStatus());

        if (task.getStatus().equals("COMPLETED")) {
            System.out.println("\nSUCCESS: Scheduler execution layer ran KMP.");
        } else {
            System.out.println("\nFAILURE: KMP execution did not complete.");
        }

        System.out.println("==============================================");
    }
}