package test;

import execution.SuffixArrayTaskExecutor;
import model.Task;

public class SuffixArrayTaskExecutorTest {

    public static void main(String[] args) {

        System.out.println(
                "==============================================");

        System.out.println(
                "       SUFFIX ARRAY EXECUTOR TEST");

        System.out.println(
                "==============================================");

        Task task =
                new Task(
                        "T04",
                        "Text Indexing",
                        "D01",
                        "Suffix Array",
                        "HIGH",
                        3,
                        300,
                        2
                );

        SuffixArrayTaskExecutor executor =
                new SuffixArrayTaskExecutor();

        executor.execute(task);

        System.out.println(
                "\n==============================================");

        if (task.getStatus()
                .equalsIgnoreCase("COMPLETED")) {

            System.out.println(
                    "SUCCESS: Suffix Array task "
                    + "execution passed.");

        } else {

            System.out.println(
                    "FAILURE: Suffix Array task "
                    + "execution failed.");
        }

        System.out.println(
                "==============================================");
    }
}