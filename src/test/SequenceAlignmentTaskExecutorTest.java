package test;

import execution.SequenceAlignmentTaskExecutor;
import model.Task;

public class SequenceAlignmentTaskExecutorTest {

    public static void main(String[] args) {

        System.out.println(
                "==============================================");

        System.out.println(
                "     SEQUENCE ALIGNMENT EXECUTOR TEST");

        System.out.println(
                "==============================================");

        Task task =
                new Task(
                        "T06",
                        "Sequence Alignment",
                        "D01",
                        "Sequence Alignment",
                        "HIGH",
                        2,
                        200,
                        2
                );

        SequenceAlignmentTaskExecutor executor =
                new SequenceAlignmentTaskExecutor();

        executor.execute(task);

        System.out.println(
                "\n==============================================");

        if (task.getStatus()
                .equalsIgnoreCase("COMPLETED")) {

            System.out.println(
                    "SUCCESS: Sequence Alignment "
                    + "task execution passed.");

        } else {

            System.out.println(
                    "FAILURE: Sequence Alignment "
                    + "task execution failed.");
        }

        System.out.println(
                "==============================================");
    }
}