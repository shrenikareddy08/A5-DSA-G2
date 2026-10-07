package test;

import java.util.Scanner;

import model.Document;
import storage.DocumentFileManager;

public class DocumentManagerTest {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        DocumentFileManager fileManager =
                new DocumentFileManager();

        boolean running = true;

        while (running) {

            System.out.println("\n");
            System.out.println("================================================");
            System.out.println("       INTELLIGENT INFORMATION PLANNER");
            System.out.println("              DOCUMENT MANAGER");
            System.out.println("================================================");
            System.out.println("1. Add Document");
            System.out.println("2. View Documents");
            System.out.println("3. Read Document");
            System.out.println("4. Delete Document");
            System.out.println("5. Exit");
            System.out.println("================================================");

            System.out.print("Enter choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {

                case 1:

                    System.out.println("\n------------- ADD DOCUMENT ----------------");

                    System.out.print("Document ID: ");
                    String id = scanner.nextLine();

                    System.out.print("Title: ");
                    String title = scanner.nextLine();

                    System.out.print("Author: ");
                    String author = scanner.nextLine();

                    System.out.print("Category: ");
                    String category = scanner.nextLine();

                    System.out.print("Content: ");
                    String content = scanner.nextLine();

                    Document document = new Document(
                            id,
                            title,
                            author,
                            category,
                            content
                    );

                    fileManager.saveDocument(document);

                    break;

                case 2:

                    fileManager.listDocuments();

                    break;

                case 3:

                    System.out.print(
                            "\nEnter Document ID to read: "
                    );

                    String readId = scanner.nextLine();

                    fileManager.readDocument(readId);

                    break;

                case 4:

                    System.out.print(
                            "\nEnter Document ID to delete: "
                    );

                    String deleteId = scanner.nextLine();

                    fileManager.deleteDocument(deleteId);

                    break;

                case 5:

                    running = false;

                    System.out.println(
                            "\nThank you for using "
                            + "Intelligent Information Planner."
                    );

                    break;

                default:

                    System.out.println(
                            "\nInvalid choice. Please try again."
                    );
            }
        }

        scanner.close();
    }
}