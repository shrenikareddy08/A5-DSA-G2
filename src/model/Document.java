package model;

public class Document {

    private String documentId;
    private String title;
    private String author;
    private String category;
    private String content;

    public Document(String documentId, String title, String author,
                    String category, String content) {

        this.documentId = documentId;
        this.title = title;
        this.author = author;
        this.category = category;
        this.content = content;
    }

    public String getDocumentId() {
        return documentId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getCategory() {
        return category;
    }

    public String getContent() {
        return content;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void displayDocument() {

        System.out.println("\n==============================================");
        System.out.println("             DOCUMENT DETAILS");
        System.out.println("==============================================");
        System.out.println("Document ID : " + documentId);
        System.out.println("Title       : " + title);
        System.out.println("Author      : " + author);
        System.out.println("Category    : " + category);
        System.out.println("----------------------------------------------");
        System.out.println("Content:");
        System.out.println(content);
        System.out.println("==============================================");
    }
}