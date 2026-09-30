package scheduling;

import java.io.*;
import model.Document;

public class WorkloadAnalyzer {
    public static class Profile {
        public int characters;
        public int words;
        public int lines;
        public int uniqueWords;
        public int cpu;
        public int memory;
        public long estimatedMs;
    }

    public Profile analyze(Document document) {
        Profile p = new Profile();
        String text = document.getContent() == null ? "" : document.getContent();

        p.characters = text.length();
        p.lines = text.length() == 0 ? 0 : text.split("\\R").length;

        String trimmed = text.trim();
        p.words = trimmed.isEmpty() ? 0 : trimmed.split("\\s+").length;

        String[] words = trimmed.isEmpty() ? new String[0] : trimmed.toLowerCase().split("\\s+");
        String[] unique = new String[words.length];
        int uniqueCount = 0;
        for (String word : words) {
            boolean exists = false;
            for (int i = 0; i < uniqueCount; i++) {
                if (unique[i].equals(word)) { exists = true; break; }
            }
            if (!exists) unique[uniqueCount++] = word;
        }
        p.uniqueWords = uniqueCount;

        p.cpu = Math.max(1, Math.min(4, 1 + p.characters / 1000));
        p.memory = Math.max(128, 128 + (p.characters / 1000) * 64);
        p.estimatedMs = Math.max(250, 250 + p.characters * 2L);
        return p;
    }
}
