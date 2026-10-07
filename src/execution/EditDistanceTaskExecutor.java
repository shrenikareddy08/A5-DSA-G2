package execution;

import algorithms.EditDistance;
import model.Task;
import storage.DocumentFileManager;

public class EditDistanceTaskExecutor {

    public void execute(Task task) {

        System.out.println(
                "\n==============================================");

        System.out.println(
                "       EDIT DISTANCE TASK EXECUTION");

        System.out.println(
                "==============================================");

        System.out.println(
                "Task ID   : "
                + task.getTaskId());

        System.out.println(
                "Task Name : "
                + task.getTaskName());

        System.out.println(
                "Document  : "
                + task.getDocumentId());

        System.out.println(
                "Algorithm : "
                + task.getAlgorithm());

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

        /*
         * For the scheduling demonstration,
         * compare the document content with a
         * representative reference string.
         */
        String reference =
                "Operating systems manage computer resources, processes, memory and files.";

        EditDistance editDistance =
                new EditDistance();

        long startTime =
                System.currentTimeMillis();

        int distance =
                editDistance.calculate(
                        content.trim().toLowerCase(),
                        reference.toLowerCase());

        long endTime =
                System.currentTimeMillis();

        task.setStatus("COMPLETED");

        System.out.println(
                "\nEDIT DISTANCE RESULT");

        System.out.println(
                "----------------------------------------------");

        System.out.println(
                "Reference Text : "
                + reference);

        System.out.println(
                "Edit Distance  : "
                + distance);

        System.out.println(
                "\nExecution Time : "
                + (endTime - startTime)
                + " ms");

        System.out.println(
                "Task Status    : "
                + task.getStatus());

        System.out.println(
                "==============================================");
    }
}