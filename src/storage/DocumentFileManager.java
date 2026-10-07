package storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import model.Document;

public class DocumentFileManager {

    private final String folderName = "documents";

    public DocumentFileManager() {
        File folder = new File(folderName);
        if (!folder.exists()) folder.mkdirs();
    }

    public boolean documentExists(String documentId) {
        if (documentId == null || documentId.trim().length() == 0) return false;
        return new File(folderName + File.separator + documentId + ".txt").exists();
    }

    public void saveDocument(Document document) {
        if (document == null) {
            System.out.println("Cannot save a null document.");
            return;
        }
        String id = document.getDocumentId();
        if (!isSafeId(id)) {
            System.out.println("Invalid document ID. Use letters, numbers, _ or - only.");
            return;
        }
        if (document.getTitle() == null || document.getTitle().trim().length() == 0
                || document.getContent() == null || document.getContent().trim().length() == 0) {
            System.out.println("Title and document content are required.");
            return;
        }

        File file = new File(folderName + File.separator + id + ".txt");
        if (file.exists()) {
            System.out.println("Document ID already exists: " + id);
            return;
        }

        try {
            FileWriter writer = new FileWriter(file);
            writer.write("ID: " + id + "\n");
            writer.write("TITLE: " + safe(document.getTitle()) + "\n");
            writer.write("AUTHOR: " + safe(document.getAuthor()) + "\n");
            writer.write("CATEGORY: " + safe(document.getCategory()) + "\n\n");
            writer.write("CONTENT:\n");
            writer.write(document.getContent().trim());
            writer.write("\n");
            writer.close();
            System.out.println("Document saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving document: " + e.getMessage());
        }
    }

    public void listDocuments() {
        File folder = new File(folderName);
        File[] files = folder.listFiles();
        System.out.println("\n============================================================");
        System.out.println("                       DOCUMENT LIBRARY");
        System.out.println("============================================================");
        int count = 0;
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile() && files[i].getName().toLowerCase().endsWith(".txt")) {
                    count++;
                    System.out.println(String.format("%2d. %-12s %8d bytes", count,
                            files[i].getName(), files[i].length()));
                }
            }
        }
        if (count == 0) System.out.println("No TXT documents found.");
        System.out.println("------------------------------------------------------------");
        System.out.println("Stored documents : " + count);
        System.out.println("============================================================");
    }

    public void readDocument(String documentId) {
        File file = fileFor(documentId);
        if (!file.exists()) {
            System.out.println("Document not found: " + documentId);
            return;
        }
        System.out.println("\n============================================================");
        System.out.println("                      DOCUMENT CONTENT");
        System.out.println("============================================================");
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) System.out.println(line);
            reader.close();
        } catch (IOException e) {
            System.out.println("Error reading document: " + e.getMessage());
        }
        System.out.println("============================================================");
    }

    public String readDocumentContent(String documentId) {
        File file = fileFor(documentId);
        if (!file.exists()) return null;
        StringBuilder content = new StringBuilder();
        boolean contentSection = false;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
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
            return contentSection ? content.toString() : null;
        } catch (IOException e) {
            return null;
        }
    }

    public void deleteDocument(String documentId) {
        File file = fileFor(documentId);
        if (!file.exists()) {
            System.out.println("Document not found.");
            return;
        }
        if (file.delete()) System.out.println("Document deleted successfully.");
        else System.out.println("Unable to delete document.");
    }

    private File fileFor(String documentId) {
        return new File(folderName + File.separator + documentId + ".txt");
    }

    private boolean isSafeId(String id) {
        if (id == null || id.length() == 0) return false;
        for (int i = 0; i < id.length(); i++) {
            char c = id.charAt(i);
            if (!(Character.isLetterOrDigit(c) || c == '_' || c == '-')) return false;
        }
        return true;
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
}
