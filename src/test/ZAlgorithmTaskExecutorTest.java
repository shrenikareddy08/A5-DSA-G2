package test;

import execution.ZAlgorithmTaskExecutor;
import model.Task;

public class ZAlgorithmTaskExecutorTest {

    public static void main(String[] args) {

        System.out.println(
                "==============================================");

        System.out.println(
                "       Z ALGORITHM TASK EXECUTOR TEST");

        System.out.println(
                "==============================================");

        Task task =
                new Task(
                        "T05",
                        "Z Pattern Search",
                        "D01",
                        "Z Algorithm",
                        "HIGH",
                        2,
                        100,
                        1);

        ZAlgorithmTaskExecutor executor =
                new ZAlgorithmTaskExecutor();

        executor.execute(
                task,
                "memory");

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
                    "SUCCESS: Z Algorithm task execution passed.");

        } else {

            System.out.println(
                    "FAILURE: Z Algorithm task execution failed.");
        }

        System.out.println(
                "==============================================");
    }
}