package test;

import model.Task;
import execution.RecoveryManager;

public class RecoveryManagerTest {

    public static void main(String[] args) {

        Task task = new Task(
                "T02",
                "Document Similarity",
                "D01",
                "Edit Distance",
                "MEDIUM",
                2,
                200,
                2
        );

        RecoveryManager recoveryManager =
                new RecoveryManager(2);

        System.out.println(
                "==============================================");

        System.out.println(
                "       FAILURE AND RECOVERY TEST");

        System.out.println(
                "==============================================");

        boolean result =
                recoveryManager.executeWithRecovery(
                        task,
                        true);

        System.out.println(
                "\n----------------------------------------------");

        System.out.println(
                "Final Status: "
                + task.getStatus());

        System.out.println(
                "Recovery Result: "
                + result);

        System.out.println(
                "----------------------------------------------");

        System.out.println(
                "              TEST COMPLETED");

        System.out.println(
                "==============================================");
    }
}