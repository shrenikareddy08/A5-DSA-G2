package execution;

import algorithms.SuffixArray;
import model.Task;
import storage.DocumentFileManager;

public class SuffixArrayTaskExecutor {

    public void execute(Task task) {

        System.out.println(
                "\n==============================================");

        System.out.println(
                "        SUFFIX ARRAY TASK EXECUTION");

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

        String text =
                content.trim().toLowerCase();

        SuffixArray suffixArray =
                new SuffixArray();

        long startTime =
                System.currentTimeMillis();

        int[] suffixes =
                suffixArray.buildSuffixArray(
                        text);

        int[] lcp =
                suffixArray.buildLCP(
                        text,
                        suffixes);

        long endTime =
                System.currentTimeMillis();

        task.setStatus("COMPLETED");

        System.out.println(
                "\nSUFFIX ARRAY RESULT");

        System.out.println(
                "----------------------------------------------");

        System.out.println(
                "Text Length : "
                + text.length());

        System.out.println(
                "Number of Suffixes : "
                + suffixes.length);

        System.out.println(
                "\nSuffix Array:");

        System.out.println(
                "----------------------------------------------");

        /*
         * Displaying every suffix can become very
         * large for real documents. Therefore only
         * the first 20 entries are displayed.
         */
        int displayLimit =
                suffixes.length;

        if (displayLimit > 20) {
            displayLimit = 20;
        }

        for (int i = 0;
                i < displayLimit;
                i++) {

            int index =
                    suffixes[i];

            String suffix =
                    suffixArray.getSuffix(
                            text,
                            index);

            if (suffix.length() > 60) {

                suffix =
                        suffix.substring(0, 60)
                        + "...";
            }

            System.out.println(
                    "SA[" + i + "] = "
                    + index
                    + "    "
                    + suffix);
        }

        if (suffixes.length > 20) {

            System.out.println(
                    "... "
                    + (suffixes.length - 20)
                    + " more suffixes");
        }

        System.out.println(
                "\nLCP Array:");

        System.out.println(
                "----------------------------------------------");

        int lcpDisplayLimit =
                lcp.length;

        if (lcpDisplayLimit > 20) {
            lcpDisplayLimit = 20;
        }

        for (int i = 0;
                i < lcpDisplayLimit;
                i++) {

            System.out.println(
                    "LCP[" + i + "] = "
                    + lcp[i]);
        }

        if (lcp.length > 20) {

            System.out.println(
                    "... "
                    + (lcp.length - 20)
                    + " more LCP values");
        }

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