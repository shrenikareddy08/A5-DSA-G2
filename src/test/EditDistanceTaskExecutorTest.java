package test;

import execution.EditDistanceTaskExecutor;
import model.Task;

public class EditDistanceTaskExecutorTest {

    public static void main(String[] args) {

        System.out.println(
                "==============================================");

        System.out.println(
                "       EDIT DISTANCE TASK EXECUTOR TEST");

        System.out.println(
                "==============================================");

        Task task =
                new Task(
                        "T02",
                        "Document Similarity",
                        "D01",
                        "Edit Distance",
                        "MEDIUM",
                        2,
                        200,
                        2);

        EditDistanceTaskExecutor executor =
                new EditDistanceTaskExecutor();

        executor.execute(task);

        System.out.println(
                "\n==============================================");

        System.out.println(
                "             TEST RESULT");

        System.out.println(
                "==============================================");

        System.out.println(
                "Final Task Status : "
                + task.getStatus());

        if (task.getStatus().equals("COMPLETED")) {

            System.out.println(
                    "SUCCESS: Edit Distance task execution passed.");

        } else {

            System.out.println(
                    "FAILURE: Edit Distance task execution failed.");
        }

        System.out.println(
                "==============================================");
    }
}