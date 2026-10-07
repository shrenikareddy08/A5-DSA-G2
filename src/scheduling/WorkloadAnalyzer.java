package scheduling;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class WorkloadAnalyzer {

    public static class WorkloadProfile {
        private String documentId;
        private String fileName;
        private int characterCount;
        private int wordCount;
        private int lineCount;
        private int uniqueWordCount;
        private int estimatedCpu;
        private int estimatedMemory;
        private int estimatedDuration;

        public WorkloadProfile(String documentId, String fileName,
                int characterCount, int wordCount, int lineCount,
                int uniqueWordCount, int estimatedCpu,
                int estimatedMemory, int estimatedDuration) {
            this.documentId = documentId;
            this.fileName = fileName;
            this.characterCount = characterCount;
            this.wordCount = wordCount;
            this.lineCount = lineCount;
            this.uniqueWordCount = uniqueWordCount;
            this.estimatedCpu = estimatedCpu;
            this.estimatedMemory = estimatedMemory;
            this.estimatedDuration = estimatedDuration;
        }

        public String getDocumentId() { return documentId; }
        public String getFileName() { return fileName; }
        public int getCharacterCount() { return characterCount; }
        public int getWordCount() { return wordCount; }
        public int getLineCount() { return lineCount; }
        public int getUniqueWordCount() { return uniqueWordCount; }
        public int getEstimatedCpu() { return estimatedCpu; }
        public int getEstimatedMemory() { return estimatedMemory; }
        public int getEstimatedDuration() { return estimatedDuration; }

        public void display() {
            System.out.println("----------------------------------------------");
            System.out.println("Document ID       : " + documentId);
            System.out.println("File Name         : " + fileName);
            System.out.println("Text Characters   : " + characterCount);
            System.out.println("Text Words        : " + wordCount);
            System.out.println("Text Lines        : " + lineCount);
            System.out.println("Unique Words      : " + uniqueWordCount);
            System.out.println("Estimated CPU     : " + estimatedCpu + " core(s)");
            System.out.println("Estimated Memory  : " + estimatedMemory + " MB");
            System.out.println("Estimated Work    : " + estimatedDuration + " sec");
            System.out.println("----------------------------------------------");
        }
    }

    public WorkloadProfile[] analyzeAllDocuments() {
        File folder = new File("documents");
        File[] files = folder.listFiles();
        if (files == null) return new WorkloadProfile[0];

        WorkloadProfile[] profiles = new WorkloadProfile[files.length];
        int count = 0;
        for (int i = 0; i < files.length; i++) {
            String name = files[i].getName();
            if (files[i].isFile() && name.toLowerCase().endsWith(".txt")) {
                String id = name.substring(0, name.length() - 4);
                WorkloadProfile profile = analyze(id);
                if (profile != null) profiles[count++] = profile;
            }
        }

        WorkloadProfile[] result = new WorkloadProfile[count];
        for (int i = 0; i < count; i++) result[i] = profiles[i];
        return result;
    }

    public WorkloadProfile analyze(String documentId) {
        if (documentId == null || documentId.trim().length() == 0) return null;

        File file = new File("documents" + File.separator + documentId + ".txt");
        if (!file.exists()) {
            System.out.println("Document not found: " + file.getPath());
            return null;
        }

        int characterCount = 0;
        int wordCount = 0;
        int lineCount = 0;
        String[] words = new String[10000];
        int storedWords = 0;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            boolean contentSection = false;

            while ((line = reader.readLine()) != null) {
                if (line.trim().equalsIgnoreCase("CONTENT:")) {
                    contentSection = true;
                    continue;
                }
                if (!contentSection) continue;

                lineCount++;
                characterCount += line.length();
                String[] lineWords = splitWords(line);
                for (int i = 0; i < lineWords.length; i++) {
                    if (lineWords[i].length() == 0) continue;
                    wordCount++;
                    if (storedWords < words.length) {
                        words[storedWords++] = lineWords[i].toLowerCase();
                    }
                }
            }
            reader.close();

            if (!contentSection) {
                System.out.println("Invalid document: CONTENT section missing.");
                return null;
            }
        } catch (IOException e) {
            System.out.println("Error reading document: " + documentId);
            return null;
        }

        int uniqueWordCount = countUniqueWords(words, storedWords);
        int estimatedCpu = estimateCpu(wordCount, characterCount);
        int estimatedMemory = estimateMemory(wordCount, characterCount);
        int estimatedDuration = estimateDuration(wordCount, characterCount, lineCount);

        return new WorkloadProfile(documentId, file.getName(), characterCount,
                wordCount, lineCount, uniqueWordCount, estimatedCpu,
                estimatedMemory, estimatedDuration);
    }

    private String[] splitWords(String line) {
        if (line == null || line.trim().length() == 0) return new String[0];
        String cleaned = line.trim();
        String[] temporary = new String[cleaned.length() + 1];
        int count = 0;
        StringBuilder current = new StringBuilder();
        for (int i = 0; i < cleaned.length(); i++) {
            char c = cleaned.charAt(i);
            if (Character.isLetterOrDigit(c)) current.append(c);
            else if (current.length() > 0) {
                temporary[count++] = current.toString();
                current.setLength(0);
            }
        }
        if (current.length() > 0) temporary[count++] = current.toString();
        String[] result = new String[count];
        for (int i = 0; i < count; i++) result[i] = temporary[i];
        return result;
    }

    private int countUniqueWords(String[] words, int count) {
        int unique = 0;
        for (int i = 0; i < count; i++) {
            boolean seen = false;
            for (int j = 0; j < i; j++) {
                if (words[i].equals(words[j])) { seen = true; break; }
            }
            if (!seen) unique++;
        }
        return unique;
    }

    private int estimateCpu(int words, int chars) {
        if (words > 5000 || chars >= 50000) return 4;
        if (words > 1000 || chars >= 10000) return 3;
        if (words > 250 || chars >= 2500) return 2;
        return 1;
    }

    private int estimateMemory(int words, int chars) {
        int memory = 128 + (chars / 100) + (words / 50);
        if (memory < 128) memory = 128;
        if (memory > 4096) memory = 4096;
        return memory;
    }

    private int estimateDuration(int words, int chars, int lines) {
        int units = (words / 50) + (chars / 500) + (lines / 10);
        if (units < 1) units = 1;
        if (units >= 120) return 12;
        if (units >= 60) return 8;
        if (units >= 30) return 5;
        if (units >= 15) return 3;
        if (units >= 5) return 2;
        return 1;
    }
}
