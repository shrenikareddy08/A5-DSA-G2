package storage;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import model.Document;

/** Handles the small file-based document library used by the project. */
public class DocumentFileManager {
    private final String folderName = "documents";

    public DocumentFileManager() {
        File folder = new File(folderName);
        if (!folder.exists()) folder.mkdirs();
    }

    public void saveDocument(Document document) {
        File file = new File(folderName, document.getDocumentId() + ".txt");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("ID: " + document.getDocumentId() + "\n");
            writer.write("TITLE: " + document.getTitle() + "\n");
            writer.write("AUTHOR: " + document.getAuthor() + "\n");
            writer.write("CATEGORY: " + document.getCategory() + "\n\n");
            writer.write("CONTENT:\n");
            writer.write(document.getContent());
            System.out.println("Document saved successfully.");
        } catch (IOException e) {
            System.out.println("Unable to save document: " + e.getMessage());
        }
    }

    public String readDocumentContent(String documentId) {
        File file = new File(folderName, documentId + ".txt");
        if (!file.exists()) return null;

        StringBuilder content = new StringBuilder();
        boolean insideContent = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.equals("CONTENT:")) {
                    insideContent = true;
                    continue;
                }
                if (insideContent) content.append(line).append('\n');
            }
            return content.toString();
        } catch (IOException e) {
            return null;
        }
    }

    public Document loadDocument(String documentId) {
        File file = new File(folderName, documentId + ".txt");
        if (!file.exists()) return null;

        String title = documentId;
        String author = "";
        String category = "Text";
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("TITLE: ")) title = line.substring(7).trim();
                else if (line.startsWith("AUTHOR: ")) author = line.substring(8).trim();
                else if (line.startsWith("CATEGORY: ")) category = line.substring(10).trim();
                else if (line.equals("CONTENT:")) break;
            }
            return new Document(documentId, title, author, category, readDocumentContent(documentId));
        } catch (IOException e) {
            return null;
        }
    }

    public List<String> getDocumentIds() {
        File folder = new File(folderName);
        File[] files = folder.listFiles();
        List<String> ids = new ArrayList<String>();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".txt")) {
                    ids.add(file.getName().substring(0, file.getName().length() - 4));
                }
            }
        }
        Collections.sort(ids, new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                return compareDocumentIds(a, b);
            }
        });
        return ids;
    }

    private int compareDocumentIds(String a, String b) {
        String an = a.replaceAll("\\D", "");
        String bn = b.replaceAll("\\D", "");
        if (!an.isEmpty() && !bn.isEmpty()) {
            try {
                int ai = Integer.parseInt(an);
                int bi = Integer.parseInt(bn);
                if (ai != bi) return ai < bi ? -1 : 1;
            } catch (NumberFormatException ignored) { }
        }
        return a.compareToIgnoreCase(b);
    }

    public void listDocuments() {
        List<String> ids = getDocumentIds();
        System.out.println("\n" + "-".repeat(74));
        if (ids.isEmpty()) {
            System.out.println("No documents available.");
        } else {
            for (String id : ids) System.out.println(id);
        }
        System.out.println("-".repeat(74));
    }

    public void readDocument(String documentId) {
        Document document = loadDocument(documentId);
        if (document == null) {
            System.out.println("Document not found.");
            return;
        }
        document.displayDocument();
    }

    public boolean deleteDocument(String documentId) {
        File file = new File(folderName, documentId + ".txt");
        return file.exists() && file.delete();
    }
}
