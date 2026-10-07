package execution;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import algorithms.EditDistance;
import algorithms.EdmondsKarp;
import algorithms.KMP;
import algorithms.RabinKarp;
import algorithms.SequenceAlignment;
import algorithms.SuffixArray;
import algorithms.ZAlgorithm;
import model.Task;

public class TaskExecutor {

    public void execute(Task task) {

        System.out.println(
                "\n==============================================");

        System.out.println(
                "              TASK EXECUTION");

        System.out.println(
                "==============================================");

        if (task == null) {

            System.out.println(
                    "Task is null.");

            System.out.println(
                    "==============================================");

            return;
        }

        System.out.println(
                "Task ID   : "
                + task.getTaskId());

        System.out.println(
                "Task Name : "
                + task.getTaskName());

        System.out.println(
                "Algorithm : "
                + task.getAlgorithm());

        System.out.println(
                "Document  : "
                + task.getDocumentId());

        try {

            task.setResultSummary(null);

            String algorithm =
                    task.getAlgorithm();

            if (algorithm == null) {

                throw new Exception(
                        "Algorithm is missing.");
            }

            String document =
                    loadDocument(
                            task.getDocumentId());

            if (document == null) {

                throw new Exception(
                        "Unable to load document.");
            }

            /*
             * ------------------------------------------------
             * KMP
             * ------------------------------------------------
             */

            if (algorithm.equalsIgnoreCase("KMP")) {

                executeKMP(
                        task,
                        document);

            }

            /*
             * ------------------------------------------------
             * RABIN-KARP
             * ------------------------------------------------
             */

            else if (
                    algorithm.equalsIgnoreCase("Rabin-Karp")
                    || algorithm.equalsIgnoreCase("RABIN_KARP")
                    || algorithm.equalsIgnoreCase("RABIN-KARP")) {

                executeRabinKarp(
                        task,
                        document);

            }

            /*
             * ------------------------------------------------
             * Z ALGORITHM
             * ------------------------------------------------
             */

            else if (
                    algorithm.equalsIgnoreCase("Z")
                    || algorithm.equalsIgnoreCase("Z Algorithm")
                    || algorithm.equalsIgnoreCase("Z_Algorithm")) {

                executeZAlgorithm(
                        task,
                        document);

            }

            /*
             * ------------------------------------------------
             * SUFFIX ARRAY
             * ------------------------------------------------
             */

            else if (
                    algorithm.equalsIgnoreCase("Suffix Array")
                    || algorithm.equalsIgnoreCase("SUFFIX_ARRAY")) {

                executeSuffixArray(
                        task,
                        document);

            }

            /*
             * ------------------------------------------------
             * EDIT DISTANCE
             * ------------------------------------------------
             */

            else if (
                    algorithm.equalsIgnoreCase("Edit Distance")
                    || algorithm.equalsIgnoreCase("EDIT_DISTANCE")) {

                executeEditDistance(
                        task,
                        document);

            }

            /*
             * ------------------------------------------------
             * SEQUENCE ALIGNMENT
             * ------------------------------------------------
             */

            else if (
                    algorithm.equalsIgnoreCase(
                            "Sequence Alignment")
                    || algorithm.equalsIgnoreCase(
                            "SEQUENCE_ALIGNMENT")) {

                executeSequenceAlignment(
                        task,
                        document);

            }

            /*
             * ------------------------------------------------
             * EDMONDS-KARP
             * ------------------------------------------------
             */

            else if (
                    algorithm.equalsIgnoreCase(
                            "Edmonds-Karp")
                    || algorithm.equalsIgnoreCase(
                            "EDMONDS_KARP")) {

                executeEdmondsKarp(
                        task);

            }

            else {

                throw new Exception(
                        "Unsupported algorithm: "
                        + algorithm);
            }

            task.setStatus("COMPLETED");

            System.out.println(
                    "\nTask completed successfully.");

        } catch (Exception e) {

            task.setStatus("FAILED");

            System.out.println(
                    "\nTask execution failed.");

            System.out.println(
                    "Reason: "
                    + e.getMessage());
        }

        System.out.println(
                "==============================================");
    }

    /*
     * =========================================================
     * DOCUMENT LOADING
     * =========================================================
     */

    private String loadDocument(
            String documentId)
            throws IOException {

        if (documentId == null || documentId.trim().length() == 0) {
            throw new IOException("Document ID is missing.");
        }

        File file = new File(
                "documents" + File.separator + documentId + ".txt");

        if (!file.exists()) {
            throw new IOException("Document not found: " + file.getPath());
        }

        StringBuilder content = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        boolean contentSection = false;

        while ((line = reader.readLine()) != null) {
            if (line.trim().equalsIgnoreCase("CONTENT:")) {
                contentSection = true;
                continue;
            }

            if (contentSection) {
                if (content.length() > 0) content.append('\n');
                content.append(line);
            }
        }

        reader.close();

        if (!contentSection) {
            throw new IOException("Invalid document format: CONTENT section missing.");
        }

        return content.toString();
    }

    /*
     * =========================================================
     * KMP
     * =========================================================
     */

    private void executeKMP(
            Task task,
            String document) {

        String pattern =
                task.getInputValue();

        if (pattern == null || pattern.length() == 0) {
            pattern = "memory";
        }

        System.out.println(
                "\n[ALGORITHM] KMP");

        System.out.println(
                "Pattern    : "
                + pattern);

        System.out.println(
                "Text size  : "
                + document.length());

        KMP kmp =
                new KMP();

        int[] positions =
                kmp.search(
                        document,
                        pattern);

        System.out.println(
                "Matches    : "
                + positions.length);

        String positionText = formatPositions(positions);
        task.setResultSummary("Matches=" + positions.length + positionText);

        if (positions.length > 0) {

            System.out.print(
                    "Positions  : ");

            int limit =
                    positions.length < 10
                    ? positions.length
                    : 10;

            for (int i = 0; i < limit; i++) {

                System.out.print(
                        positions[i]);

                if (i < limit - 1) {
                    System.out.print(", ");
                }
            }

            if (positions.length > 10) {
                System.out.print(" ...");
            }

            System.out.println();
        }
    }

    /*
     * =========================================================
     * RABIN-KARP
     * =========================================================
     */

    private void executeRabinKarp(
            Task task,
            String document) {

        String pattern =
                task.getInputValue();

        if (pattern == null || pattern.length() == 0) {
            pattern = "memory";
        }

        System.out.println(
                "\n[ALGORITHM] Rabin-Karp");

        System.out.println(
                "Pattern    : "
                + pattern);

        System.out.println(
                "Text size  : "
                + document.length());

        RabinKarp rabinKarp =
                new RabinKarp();

        int[] positions =
                rabinKarp.search(
                        document,
                        pattern);

        System.out.println(
                "Matches    : "
                + positions.length);

        String positionText = formatPositions(positions);
        task.setResultSummary("Matches=" + positions.length + positionText);

        if (positions.length > 0) {

            System.out.print(
                    "Positions  : ");

            int limit =
                    positions.length < 10
                    ? positions.length
                    : 10;

            for (int i = 0; i < limit; i++) {

                System.out.print(
                        positions[i]);

                if (i < limit - 1) {
                    System.out.print(", ");
                }
            }

            if (positions.length > 10) {
                System.out.print(" ...");
            }

            System.out.println();
        }
    }

    /*
     * =========================================================
     * Z ALGORITHM
     * =========================================================
     */

    private void executeZAlgorithm(
            Task task,
            String document) {

        String pattern =
                task.getInputValue();

        if (pattern == null || pattern.length() == 0) {
            pattern = "memory";
        }

        System.out.println(
                "\n[ALGORITHM] Z Algorithm");

        System.out.println(
                "Pattern    : "
                + pattern);

        System.out.println(
                "Text size  : "
                + document.length());

        ZAlgorithm zAlgorithm =
                new ZAlgorithm();

        int[] positions =
                zAlgorithm.search(
                        document,
                        pattern);

        System.out.println(
                "Matches    : "
                + positions.length);

        String positionText = formatPositions(positions);
        task.setResultSummary("Matches=" + positions.length + positionText);

        if (positions.length > 0) {

            System.out.print(
                    "Positions  : ");

            int limit =
                    positions.length < 10
                    ? positions.length
                    : 10;

            for (int i = 0; i < limit; i++) {

                System.out.print(
                        positions[i]);

                if (i < limit - 1) {
                    System.out.print(", ");
                }
            }

            if (positions.length > 10) {
                System.out.print(" ...");
            }

            System.out.println();
        }
    }

    /*
     * =========================================================
     * SUFFIX ARRAY
     * =========================================================
     */

    private void executeSuffixArray(
            Task task,
            String document) throws IOException {

        System.out.println("\n[ALGORITHM] Suffix Array + LCP");

        if (task.getSecondaryDocumentId() != null
                && task.getSecondaryDocumentId().length() > 0) {

            String second = loadDocument(task.getSecondaryDocumentId());
            char separator = chooseSeparator(document, second);
            String combined = document + separator + second;

            SuffixArray suffixArray = new SuffixArray();
            int[] suffixes = suffixArray.buildSuffixArray(combined);
            int[] lcp = suffixArray.buildLCP(combined, suffixes);

            int firstLength = document.length();
            int secondStart = firstLength + 1;
            int bestCommon = 0;
            int bestFirst = -1;
            int bestSecond = -1;

            for (int i = 1; i < suffixes.length; i++) {
                int a = suffixes[i - 1];
                int b = suffixes[i];
                boolean aFirst = a < firstLength;
                boolean bFirst = b < firstLength;
                boolean aSecond = a >= secondStart;
                boolean bSecond = b >= secondStart;

                if ((aFirst && bSecond) || (aSecond && bFirst)) {
                    int common = lcp[i];
                    int maxAllowed = Math.min(firstLength - a, second.length() - (b - secondStart));
                    if (aSecond) {
                        maxAllowed = Math.min(firstLength - b, second.length() - (a - secondStart));
                    }
                    if (common > maxAllowed) common = maxAllowed;

                    if (common > bestCommon) {
                        bestCommon = common;
                        if (aFirst) {
                            bestFirst = a;
                            bestSecond = b - secondStart;
                        } else {
                            bestFirst = b;
                            bestSecond = a - secondStart;
                        }
                    }
                }
            }

            double similarity = 0.0;
            int shorter = Math.min(document.length(), second.length());
            if (shorter > 0) similarity = (bestCommon * 100.0) / shorter;

            System.out.println("Primary document      : " + task.getDocumentId());
            System.out.println("Comparison document   : " + task.getSecondaryDocumentId());
            System.out.println("Combined text length  : " + combined.length());
            System.out.println("Suffixes              : " + suffixes.length);
            System.out.println("LCP entries           : " + lcp.length);
            System.out.println("Longest shared text   : " + bestCommon + " characters");
            System.out.printf("Structural similarity : %.2f%%\n", similarity);

            task.setResultSummary(
                    "Shared substring=" + bestCommon + " chars, Similarity="
                    + String.format("%.2f%%", similarity)
                    + ", Index=" + suffixes.length + " suffixes");
            return;
        }

        System.out.println("Text size  : " + document.length());
        SuffixArray suffixArray = new SuffixArray();
        int[] suffixes = suffixArray.buildSuffixArray(document);
        int[] lcp = suffixArray.buildLCP(document, suffixes);

        System.out.println("Suffixes   : " + suffixes.length);
        System.out.println("LCP array  : " + lcp.length);
        task.setResultSummary("Index built: " + suffixes.length + " suffixes, " + lcp.length + " LCP entries");
    }

    private char chooseSeparator(String first, String second) {
        char[] candidates = {'\u0001', '\u0002', '\u0003', '#', '|', '~'};
        for (int i = 0; i < candidates.length; i++) {
            char c = candidates[i];
            if (first.indexOf(c) < 0 && second.indexOf(c) < 0) return c;
        }
        return '\u0001';
    }

    /*
     * =========================================================
     * EDIT DISTANCE
     * =========================================================
     */

    private void executeEditDistance(
            Task task,
            String document) throws IOException {

        System.out.println(
                "\n[ALGORITHM] Edit Distance");

        /*
         * For document similarity, divide the document
         * into two logical segments.
         *
         * This gives the dynamic-programming algorithm
         * a meaningful comparison rather than comparing
         * the document with itself.
         */

        String first = document;
        String second;

        if (task.getSecondaryDocumentId() != null
                && task.getSecondaryDocumentId().length() > 0) {

            second = loadDocument(task.getSecondaryDocumentId());

            System.out.println(
                    "Comparison document  : "
                    + task.getSecondaryDocumentId());

        } else if (document.length() <= 1) {

            first = document;
            second = document;

        } else {

            int middle = document.length() / 2;

            first = document.substring(0, middle);
            second = document.substring(middle);
        }

        EditDistance editDistance =
                new EditDistance();

        int distance =
                editDistance.calculate(
                        first,
                        second);

        System.out.println(
                "First part length  : "
                + first.length());

        System.out.println(
                "Second part length : "
                + second.length());

        System.out.println(
                "Edit distance      : "
                + distance);

        int maxLength =
                first.length() > second.length()
                ? first.length()
                : second.length();

        double similarity =
                maxLength == 0
                ? 100.0
                : (1.0 - ((double) distance / maxLength)) * 100.0;

        if (similarity < 0.0) {
            similarity = 0.0;
        }

        System.out.printf(
                "Similarity         : %.2f%%%n",
                similarity);

        task.setResultSummary(
                "Distance=" + distance + ", Similarity=" + String.format("%.2f%%", similarity));
    }

    /*
     * =========================================================
     * SEQUENCE ALIGNMENT
     * =========================================================
     */

    private void executeSequenceAlignment(
            Task task,
            String document) throws IOException {

        System.out.println(
                "\n[ALGORITHM] Sequence Alignment");

        String first = document;
        String second;

        if (task.getSecondaryDocumentId() != null
                && task.getSecondaryDocumentId().length() > 0) {

            second = loadDocument(task.getSecondaryDocumentId());

            System.out.println(
                    "Comparison document  : "
                    + task.getSecondaryDocumentId());

        } else if (document.length() <= 1) {

            first = document;
            second = document;

        } else {

            int middle = document.length() / 2;

            first = document.substring(0, middle);
            second = document.substring(middle);
        }

        SequenceAlignment alignment =
                new SequenceAlignment();

        int score =
                alignment.getAlignmentScore(
                        first,
                        second);

        String[] result =
                alignment.getAlignment(
                        first,
                        second);

        System.out.println(
                "First sequence length  : "
                + first.length());

        System.out.println(
                "Second sequence length : "
                + second.length());

        System.out.println(
                "Alignment score        : "
                + score);

        task.setResultSummary("Alignment score=" + score + ", Compared with=" +
                task.getSecondaryDocumentId());

        if (result != null
                && result.length >= 2) {

            String alignedFirst =
                    result[0];

            String alignedSecond =
                    result[1];

            if (alignedFirst.length() > 80) {

                alignedFirst =
                        alignedFirst.substring(
                                0,
                                80)
                        + "...";
            }

            if (alignedSecond.length() > 80) {

                alignedSecond =
                        alignedSecond.substring(
                                0,
                                80)
                        + "...";
            }

            System.out.println(
                    "Aligned sequence 1     : "
                    + alignedFirst);

            System.out.println(
                    "Aligned sequence 2     : "
                    + alignedSecond);
        }
    }

    /*
     * =========================================================
     * EDMONDS-KARP
     * =========================================================
     */

    private void executeEdmondsKarp(
            Task task) {

        System.out.println(
                "\n[ALGORITHM] Edmonds-Karp");

        /*
         * Small resource-allocation example.
         *
         * The scheduler itself uses resource compatibility
         * and the project also contains Edmonds-Karp as the
         * maximum-flow allocation algorithm.
         */

        int[][] capacity = {
                {0, 3, 2, 0, 0},
                {0, 0, 0, 2, 1},
                {0, 0, 0, 1, 2},
                {0, 0, 0, 0, 3},
                {0, 0, 0, 0, 0}
        };

        EdmondsKarp algorithm =
                new EdmondsKarp();

        EdmondsKarp.FlowResult result =
                algorithm.maxFlow(
                        capacity,
                        0,
                        4);

        System.out.println(
                "Maximum flow       : "
                + result.getMaxFlow());

        System.out.println(
                "Augmenting paths   : "
                + result.getAugmentingPaths());

        task.setResultSummary("Maximum flow=" + result.getMaxFlow() +
                ", Augmenting paths=" + result.getAugmentingPaths());
    }
    private String formatPositions(int[] positions) {
        if (positions == null || positions.length == 0) return "";
        StringBuilder builder = new StringBuilder(", Positions=");
        int limit = positions.length < 10 ? positions.length : 10;
        for (int i = 0; i < limit; i++) {
            if (i > 0) builder.append(',');
            builder.append(positions[i]);
        }
        if (positions.length > 10) builder.append("...");
        return builder.toString();
    }

}