package test;

import model.Task;
import scheduling.DynamicTaskGenerator;

public class DynamicTaskGeneratorTest {

    public static void main(String[] args) {

        System.out.println(
                "==============================================");

        System.out.println(
                "       DYNAMIC TASK GENERATION TEST");

        System.out.println(
                "==============================================");

        /*
         * These are actual document IDs detected
         * from the documents directory.
         */
        String[] documentIds = {
                "D01",
                "30"
        };

        DynamicTaskGenerator generator =
                new DynamicTaskGenerator();

        Task[] tasks =
                generator.generateTasks(
                        documentIds,
                        20);

        generator.displayGeneratedTasks(tasks);

        System.out.println(
                "\n==============================================");

        System.out.println(
                "             VALIDATION");

        System.out.println(
                "==============================================");

        if (tasks.length == 12) {

            System.out.println(
                    "Generated Tasks : "
                    + tasks.length);

            System.out.println(
                    "Expected Tasks  : 12");

            System.out.println(
                    "SUCCESS: Dynamic task generation passed.");

        } else {

            System.out.println(
                    "Generated Tasks : "
                    + tasks.length);

            System.out.println(
                    "Expected Tasks  : 12");

            System.out.println(
                    "FAILURE: Unexpected task count.");
        }

        System.out.println(
                "==============================================");
    }
}