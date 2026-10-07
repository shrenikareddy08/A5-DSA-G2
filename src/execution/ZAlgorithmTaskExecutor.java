package execution;

import algorithms.ZAlgorithm;
import model.Task;
import storage.DocumentFileManager;

public class ZAlgorithmTaskExecutor {

    public void execute(Task task, String pattern) {

        System.out.println(
                "\n==============================================");

        System.out.println(
                "          Z ALGORITHM TASK EXECUTION");

        System.out.println(
                "==============================================");

        System.out.println(
                "Task ID        : "
                + task.getTaskId());

        System.out.println(
                "Task Name      : "
                + task.getTaskName());

        System.out.println(
                "Document       : "
                + task.getDocumentId());

        System.out.println(
                "Algorithm      : "
                + task.getAlgorithm());

        System.out.println(
                "Search Pattern : "
                + pattern);

        System.out.println(
                "----------------------------------------------");

        DocumentFileManager manager =
                new DocumentFileManager();

        String content =
                manager.readDocumentContent(
                        task.getDocumentId());

        if (content == null) {

            task.setStatus("FAILED");

            System.out.println(
                    "Document could not be read.");

            return;
        }

        ZAlgorithm zAlgorithm =
                new ZAlgorithm();

        long startTime =
                System.currentTimeMillis();

        int[] positions =
                zAlgorithm.search(
                        content.toLowerCase(),
                        pattern.toLowerCase());

        long endTime =
                System.currentTimeMillis();

        task.setStatus("COMPLETED");

        System.out.println(
                "\nZ ALGORITHM RESULT");

        System.out.println(
                "----------------------------------------------");

        if (positions.length == 0) {

            System.out.println(
                    "Pattern not found.");

        } else {

            System.out.println(
                    "Pattern found "
                    + positions.length
                    + " time(s).");

            for (int i = 0;
                    i < positions.length;
                    i++) {

                System.out.println(
                        "Match "
                        + (i + 1)
                        + " at character index "
                        + positions[i]);
            }
        }

        System.out.println(
                "\nZ Algorithm Execution Time : "
                + (endTime - startTime)
                + " ms");

        System.out.println(
                "Task Status                : "
                + task.getStatus());

        System.out.println(
                "==============================================");
    }
}