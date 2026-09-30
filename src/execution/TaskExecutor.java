package execution;

import algorithms.dp.EditDistance;
import algorithms.dp.SequenceAlignment;
import algorithms.dp.SmithWaterman;
import algorithms.flow.EdmondsKarp;
import algorithms.randomized.MillerRabin;
import algorithms.string.KMP;
import algorithms.string.RabinKarp;
import algorithms.string.SuffixArray;
import algorithms.string.ZAlgorithm;
import model.Task;
import storage.DocumentFileManager;

public class TaskExecutor {
    private final DocumentFileManager files = new DocumentFileManager();

    public void execute(Task task) {
        long start = System.currentTimeMillis();
        task.setStatus("RUNNING");

        try {
            String content = files.readDocumentContent(task.getDocumentId());
            if (content == null) throw new RuntimeException("Document not found.");

            String operation = task.getOperation();
            String result;

            if ("SEARCH".equals(operation)) {
                int[] matches = new KMP().search(content.toLowerCase(), task.getInput().toLowerCase());
                result = matches.length + " match(es) found";
            } else if ("FAST_SEARCH".equals(operation)) {
                int[] matches = new RabinKarp().search(content.toLowerCase(), task.getInput().toLowerCase());
                result = matches.length + " match(es) found";
            } else if ("PATTERN_ANALYSIS".equals(operation)) {
                int[] z = new ZAlgorithm().build(task.getInput());
                int largest = 0;
                for (int value : z) if (value > largest) largest = value;
                result = "Pattern analyzed; strongest repeated prefix length: " + largest;
            } else if ("SIMILARITY".equals(operation)) {
                String other = files.readDocumentContent(task.getSecondaryDocumentId());
                if (other == null) throw new RuntimeException("Second document not found.");
                EditDistance ed = new EditDistance();
                double similarity = ed.similarity(content, other);
                result = String.format("Similarity: %.2f%%", similarity);
            } else if ("SHARED_CONTENT".equals(operation)) {
                String other = files.readDocumentContent(task.getSecondaryDocumentId());
                if (other == null) throw new RuntimeException("Second document not found.");
                String combined = content + "#" + other;
                int[] sa = new SuffixArray().build(combined);
                result = "Content analysis completed";
            } else if ("GLOBAL_ALIGNMENT".equals(operation)) {
                String other = files.readDocumentContent(task.getSecondaryDocumentId());
                if (other == null) throw new RuntimeException("Second document not found.");
                result = "Global alignment score: " + new SequenceAlignment().score(content, other);
            } else if ("LOCAL_ALIGNMENT".equals(operation)) {
                String other = files.readDocumentContent(task.getSecondaryDocumentId());
                if (other == null) throw new RuntimeException("Second document not found.");
                result = "Local alignment score: " + new SmithWaterman().score(content, other);
            } else if ("PRIME_CHECK".equals(operation)) {
                long n = Long.parseLong(task.getInput());
                result = new MillerRabin().isProbablyPrime(n, 8) ? "Probably prime" : "Composite";
            } else if ("FLOW_DEMO".equals(operation)) {
                EdmondsKarp flow = new EdmondsKarp(6);
                flow.addEdge(0, 1, 4);
                flow.addEdge(0, 2, 3);
                flow.addEdge(1, 3, 3);
                flow.addEdge(2, 3, 2);
                flow.addEdge(1, 4, 2);
                flow.addEdge(4, 5, 2);
                flow.addEdge(3, 5, 4);
                result = "Available assignment capacity: " + flow.maxFlow(0, 5);
            } else {
                result = "Completed.";
            }

            task.setResult(result);
            task.setStatus("COMPLETED");
        } catch (Exception e) {
            task.setResult(e.getMessage());
            task.setStatus("FAILED");
        }

        task.setActualDurationMs(System.currentTimeMillis() - start);
    }
}
