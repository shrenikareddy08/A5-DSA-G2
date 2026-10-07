package execution;

import algorithms.SequenceAlignment;
import model.Task;
import storage.DocumentFileManager;

public class SequenceAlignmentTaskExecutor {

    public void execute(Task task) {

        System.out.println(
                "\n==============================================");

        System.out.println(
                "       SEQUENCE ALIGNMENT TASK EXECUTION");

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
         * Representative reference sequence for
         * demonstrating global sequence alignment.
         */
        String reference =
                "Operating systems manage computer resources.";

        SequenceAlignment alignment =
                new SequenceAlignment();

        long startTime =
                System.currentTimeMillis();

        int score =
                alignment.getAlignmentScore(
                        content.trim().toLowerCase(),
                        reference.toLowerCase());

        String[] result =
                alignment.getAlignment(
                        content.trim().toLowerCase(),
                        reference.toLowerCase());

        long endTime =
                System.currentTimeMillis();

        task.setStatus("COMPLETED");

        System.out.println(
                "\nSEQUENCE ALIGNMENT RESULT");

        System.out.println(
                "----------------------------------------------");

        System.out.println(
                "Reference Text : "
                + reference);

        System.out.println(
                "Alignment Score : "
                + score);

        System.out.println(
                "\nAligned Document:");

        System.out.println(
                result[0]);

        System.out.println(
                "\nAligned Reference:");

        System.out.println(
                result[1]);

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