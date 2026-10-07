package test;

import algorithms.KMP;
import storage.DocumentFileManager;

public class KMPDocumentTest {

    public static void main(String[] args) {

        System.out.println(
                "==============================================");

        System.out.println(
                "        KMP DOCUMENT SEARCH TEST");

        System.out.println(
                "==============================================");

        String documentId = "D01";
        String pattern = "memory";

        /*
         * Read the document from the documents folder.
         */
        DocumentFileManager manager =
                new DocumentFileManager();

        String content =
                manager.readDocumentContent(documentId);

        if (content == null) {

            System.out.println(
                    "Document " + documentId
                    + " could not be read.");

            return;
        }

        System.out.println(
                "Document ID : " + documentId);

        System.out.println(
                "Search Pattern : " + pattern);

        System.out.println(
                "\nDocument Content:");

        System.out.println(content);

        /*
         * Run KMP on the document content.
         */
        KMP kmp = new KMP();

        int[] positions =
                kmp.search(
                        content.toLowerCase(),
                        pattern.toLowerCase());

        /*
         * Display search result.
         */
        System.out.println(
                "\nKMP Search Result:");

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
                "\n==============================================");

        System.out.println(
                "       KMP DOCUMENT TEST COMPLETED");

        System.out.println(
                "==============================================");
    }
}