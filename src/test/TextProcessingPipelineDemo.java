package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;

import algorithms.EdmondsKarp;
import algorithms.MillerRabin;
import algorithms.KMP;
import algorithms.RabinKarp;
import algorithms.ZAlgorithm;
import model.Document;
import model.Resource;
import model.Task;
import scheduling.HEFTSchedulerV3;
import scheduling.SystemResourceDiscovery;
import scheduling.WorkloadAnalyzer;
import storage.DocumentFileManager;

/**
 * Main application for:
 * INTELLIGENT RESOURCE SCHEDULING FOR TEXT PROCESSING PIPELINES
 *
 * The user sees a text-analytics problem first. The intelligent
 * scheduler is then used internally to decide when and where the
 * corresponding algorithmic task should run.
 */
public class TextProcessingPipelineDemo {

    private static final BufferedReader INPUT =
            new BufferedReader(new InputStreamReader(System.in));

    private static final DocumentFileManager DOCUMENTS =
            new DocumentFileManager();

    private static int requestCounter = 1;

    public static void main(String[] args) {
        ensureDemoDocuments();
        printBanner();
        showStartupSummary();

        boolean running = true;
        while (running) {
            showMenu();
            int choice = readInt("Choose an option: ");

            switch (choice) {
                case 1: runPatternSearchHub(); break;
                case 2: runFuzzyMatching(); break;
                case 3: runIndexing(); break;
                case 4: runSequenceAlignment(); break;
                case 5: runZAnalysis(); break;
                case 6: runCitationFlow(); break;
                case 7: runFullPipeline(); break;
                case 8: runPrimalityTest(); break;
                case 9: runPatternAlgorithmComparison(); break;
                case 10: showAlgorithmMap(); break;
                case 11: corpusExplorer(); break;
                case 12: inspectDocument(); break;
                case 13: showSchedulerDashboard(); break;
                case 14: showHistory(); break;
                case 15: showSystemOverview(); break;
                case 0:
                    running = false;
                    System.out.println("\nThank you. Text Processing Pipeline closed safely.");
                    break;
                default:
                    System.out.println("\nInvalid option. Choose a number from the menu.");
            }
        }
    }

    private static void printBanner() {
        System.out.println("\n+============================================================+");
        System.out.println("|                                                            |");
        System.out.println("|                     T E X T  H A C K                      |");
        System.out.println("|                                                            |");
        System.out.println("|          ADVANCED TEXT ANALYTICS ENGINE                    |");
        System.out.println("|                                                            |");
        System.out.println("|   Strings * Dynamic Programming * Graphs * Optimization   |");
        System.out.println("|              Randomized * Parallel                        |");
        System.out.println("|                                                            |");
        System.out.println("+============================================================+");
    }

    private static void showStartupSummary() {
        int count = countDocuments();
        System.out.println("\nTEXT HACK ENGINE READY");
        System.out.println("------------------------------------------------------------");
        System.out.println("  Corpus              : Indian text demonstration corpus");
        System.out.println("  Documents           : " + count);
        System.out.println("  String algorithms   : KMP | Z | Rabin-Karp");
        System.out.println("  DP algorithms       : Edit Distance | Sequence Alignment");
        System.out.println("  Suffix structures   : Suffix Array | LCP");
        System.out.println("  Graph algorithms    : Edmonds-Karp | Max Flow");
        System.out.println("  Randomized          : Miller-Rabin");
        System.out.println("  Scheduler           : Runtime-aware HEFT V3");
        System.out.println("  Workers             : R01 | R02 | R03");
        System.out.println("  Engine constraint   : manual algorithm/data structures");
        System.out.println("------------------------------------------------------------");
        System.out.println("  Flow: Problem -> Algorithm -> Scheduler -> Result -> Complexity");
        System.out.println("------------------------------------------------------------");
    }

    private static void showMenu() {
        System.out.println("\n+============================================================+");
        System.out.println("|                       TEXT HACK                           |");
        System.out.println("+============================================================+");
        System.out.println("\nTEXT ANALYTICS");
        System.out.println("------------------------------------------------------------");
        System.out.println("  1  Pattern Search              KMP / Rabin-Karp");
        System.out.println("  2  Fuzzy Matching              Edit Distance / DP");
        System.out.println("  3  Document Similarity         Suffix Array / LCP");
        System.out.println("  4  Sequence Alignment          Dynamic Programming");
        System.out.println("  5  Pattern Structure           Z Algorithm");
        System.out.println("\nGRAPH & OPTIMIZATION");
        System.out.println("------------------------------------------------------------");
        System.out.println("  6  Citation Flow               Edmonds-Karp / Max Flow");
        System.out.println("  7  Intelligent Pipeline        Dependency-aware scheduling");
        System.out.println("\nRANDOMIZED & ADVANCED");
        System.out.println("------------------------------------------------------------");
        System.out.println("  8  Miller-Rabin                Large-prime testing");
        System.out.println("  9  Algorithm Comparison        KMP vs Z vs Rabin-Karp");
        System.out.println(" 10  DSA-3 Algorithm Map         Problem -> Algorithm -> Cost");
        System.out.println("\nCORPUS & SYSTEM");
        System.out.println("------------------------------------------------------------");
        System.out.println(" 11  Corpus Explorer             Browse documents");
        System.out.println(" 12  Document Intelligence       Workload + metadata");
        System.out.println(" 13  Scheduler & Resources       HEFT + workers");
        System.out.println(" 14  Results & History           Previous executions");
        System.out.println(" 15  System Overview             Architecture + guarantees");
        System.out.println("\n  0  Exit TextHack");
        System.out.println("------------------------------------------------------------");
        System.out.println("READY | " + countDocuments() + " documents | 3 workers | advanced algorithms");
    }

    private static void corpusExplorer() {
        while (true) {
            System.out.println("\n+------------------------------------------------------------+");
            System.out.println("| CORPUS EXPLORER                                           |");
            System.out.println("+------------------------------------------------------------+");
            System.out.println("  1  Browse corpus");
            System.out.println("  2  Add TXT document");
            System.out.println("  0  Back");
            int choice = readInt("Choose an option: ");
            if (choice == 0) return;
            if (choice == 1) listDocuments();
            else if (choice == 2) addDocument();
            else System.out.println("Invalid option.");
        }
    }

    private static void listDocuments() {
        File folder = new File("documents");
        File[] files = folder.listFiles();
        System.out.println("\n============================================================");
        System.out.println("                    DOCUMENT LIBRARY");
        System.out.println("============================================================");
        if (files == null) {
            System.out.println("Document store is unavailable.");
            return;
        }

        String[] ids = new String[300];
        String[] titles = new String[300];
        long[] sizes = new long[300];
        int count = collectDocuments(files, ids, titles, sizes);

        if (count == 0) {
            System.out.println("No TXT documents are stored yet.");
        } else {
            System.out.printf("%-4s %-10s %-34s %10s%n", "#", "ID", "TITLE", "SIZE");
            System.out.println("------------------------------------------------------------");
            for (int i = 0; i < count; i++) {
                System.out.printf("%-4d %-10s %-34s %7d B%n",
                        i + 1, ids[i], truncate(titles[i], 34), sizes[i]);
            }
        }
        System.out.println("------------------------------------------------------------");
        System.out.println("Documents available : " + count);
        System.out.println("============================================================");
    }

    private static void addDocument() {
        System.out.println("\n============================================================");
        System.out.println("                    ADD TXT DOCUMENT");
        System.out.println("============================================================");

        String id = readLine("Document ID (example D11): ").trim();
        String title = readLine("Title: ").trim();
        String author = readLine("Author: ").trim();
        String category = readLine("Category: ").trim();

        if (id.length() == 0 || title.length() == 0) {
            System.out.println("ID and title are required.");
            return;
        }
        if (DOCUMENTS.documentExists(id)) {
            System.out.println("A document with ID " + id + " already exists.");
            return;
        }

        System.out.println("Enter text content. You may enter multiple lines.");
        System.out.println("Finish with a single line containing END.");
        StringBuilder content = new StringBuilder();
        while (true) {
            String line = readLine("");
            if (line.equals("END")) break;
            if (content.length() > 0) content.append('\n');
            content.append(line);
        }

        if (content.toString().trim().length() == 0) {
            System.out.println("Document content cannot be empty.");
            return;
        }

        DOCUMENTS.saveDocument(new Document(id, title, author, category, content.toString()));
        System.out.println("\nSUCCESS: " + id + ".txt added to the corpus.");
    }

    private static void inspectDocument() {
        String id = chooseDocument("SELECT DOCUMENT TO INSPECT");
        if (id == null) return;

        WorkloadAnalyzer.WorkloadProfile profile =
                new WorkloadAnalyzer().analyze(id);
        if (profile == null) return;

        System.out.println("\n============================================================");
        System.out.println("                  DOCUMENT INTELLIGENCE");
        System.out.println("============================================================");
        profile.display();

        String[] meta = readMetadata(id);
        System.out.println("\nMETADATA");
        System.out.println("  Title    : " + meta[0]);
        System.out.println("  Author   : " + meta[1]);
        System.out.println("  Category : " + meta[2]);
        System.out.println("\nCONTENT PREVIEW");
        String content = DOCUMENTS.readDocumentContent(id);
        System.out.println("  " + truncate(oneLine(content), 160));
        System.out.println("============================================================");
    }

    private static void runExactPatternMatching() {
        String id = chooseDocument("SELECT DOCUMENT");
        if (id == null) return;
        String pattern = requiredText("Enter EXACT string to find: ");
        if (pattern == null) return;

        Task task = createTask("Exact Pattern Search", id, "KMP", "HIGH",
                pattern, null, 1, 128);
        runSingleRequest(task, "EXACT PATTERN SEARCH", "KMP",
                "Find every exact occurrence of the user's string.",
                "KMP uses the LPS/failure table to avoid unnecessary re-comparisons.");
    }

    private static void runKeywordSearch() {
        String id = chooseDocument("SELECT DOCUMENT");
        if (id == null) return;
        String keyword = requiredText("Enter keyword / substring to find: ");
        if (keyword == null) return;

        Task task = createTask("Fast Substring Search", id, "Rabin-Karp", "HIGH",
                keyword, null, 1, 128);
        runSingleRequest(task, "FAST SUBSTRING SEARCH", "Rabin-Karp",
                "Find occurrences using polynomial rolling-hash matching.",
                "Rabin-Karp filters candidate positions by hash before verification.");
    }

    private static void runFuzzyMatching() {
        String first = chooseDocument("SELECT PRIMARY DOCUMENT");
        if (first == null) return;
        String second = chooseDifferentDocument(first, "SELECT COMPARISON DOCUMENT");
        if (second == null) return;

        Task task = createTask("Fuzzy Document Matching", first, "Edit Distance", "HIGH",
                null, second, 2, 256);
        runSingleRequest(task, "FUZZY DOCUMENT MATCHING", "Edit Distance / DP",
                "Measure how different two stored texts are.",
                "Wagner-Fischer dynamic programming finds the minimum edit operations.");
    }

    private static void runZAnalysis() {
        String id = chooseDocument("SELECT DOCUMENT");
        if (id == null) return;
        String pattern = requiredText("Enter pattern for structure analysis: ");
        if (pattern == null) return;

        Task task = createTask("Pattern Structure Analysis", id, "Z Algorithm", "MEDIUM",
                pattern, null, 1, 128);
        runSingleRequest(task, "PATTERN STRUCTURE ANALYSIS", "Z Algorithm",
                "Locate the requested pattern and expose prefix-match structure.",
                "The Z-array gives prefix matches in linear time.");
    }

    private static void runIndexing() {
        String first = chooseDocument("SELECT FIRST DOCUMENT");
        if (first == null) return;
        String second = chooseDifferentDocument(first, "SELECT SECOND DOCUMENT");
        if (second == null) return;

        Task task = createTask("Document Similarity + Indexing", first, "Suffix Array", "HIGH",
                null, second, 2, 256);
        runSingleRequest(task, "DOCUMENT SIMILARITY + TEXT INDEX", "Suffix Array + LCP",
                "Build suffix structures and estimate shared text between two documents.",
                "Suffix Array orders suffixes and LCP reveals long common substrings between the two texts.");
    }

    private static void runSequenceAlignment() {
        String first = chooseDocument("SELECT FIRST DOCUMENT");
        if (first == null) return;
        String second = chooseDifferentDocument(first, "SELECT SECOND DOCUMENT");
        if (second == null) return;

        Task task = createTask("Document Sequence Alignment", first, "Sequence Alignment", "MEDIUM",
                null, second, 2, 256);
        runSingleRequest(task, "DOCUMENT SEQUENCE ALIGNMENT", "Dynamic Programming",
                "Compare two document sequences using global alignment.",
                "Needleman-Wunsch-style DP evaluates match, mismatch and gap choices.");
    }

    private static void runSingleRequest(Task task, String title, String algorithmLabel,
            String goal, String why) {

        System.out.println("\n============================================================");
        System.out.println("                 " + title);
        System.out.println("============================================================");
        System.out.println("REQUEST ACCEPTED");
        System.out.println("  Request ID : " + task.getTaskId());
        System.out.println("  Document   : " + task.getDocumentId());
        if (task.getSecondaryDocumentId() != null) {
            System.out.println("  Compare    : " + task.getSecondaryDocumentId());
        }
        if (task.getInputValue() != null) {
            System.out.println("  Input      : \"" + task.getInputValue() + "\"");
        }
        System.out.println("\nALGORITHM");
        System.out.println("  Selected   : " + algorithmLabel);

        WorkloadAnalyzer.WorkloadProfile profile =
                new WorkloadAnalyzer().analyze(task.getDocumentId());
        if (profile != null) {
            System.out.println("\nWORKLOAD");
            System.out.println("  Text size  : " + profile.getCharacterCount() + " characters");
            System.out.println("  CPU / RAM  : " + task.getCpuRequired() + " core(s) / "
                    + task.getMemoryRequired() + " MB");
        }

        HEFTSchedulerV3 scheduler = createScheduler();
        scheduler.addTask(task);
        runSchedulerSilently(scheduler);

        printSchedulingDecision(scheduler, task);
        printFriendlyResult(task, scheduler);
        saveHistory(task, scheduler, title);
        System.out.println("============================================================");
    }

    private static void printSchedulingDecision(HEFTSchedulerV3 scheduler, Task task) {
        int resourceIndex = scheduler.getAssignedResource(task.getTaskId());
        String resourceId = "UNASSIGNED";
        String resourceName = "No compatible worker";
        if (resourceIndex >= 0) {
            Resource resource = scheduler.getResource(resourceIndex);
            resourceId = resource.getResourceId();
            resourceName = resource.getResourceName();
        }

        System.out.println("\nSCHEDULING");
        System.out.println("  Policy       : Runtime-aware HEFT V3");
        System.out.println("  Resource     : " + resourceId + " - " + resourceName);
        System.out.println("  Predicted    : " + formatMs(scheduler.getRuntimeEstimate(task.getTaskId())));
        System.out.println("  Actual       : " + formatMs(scheduler.getLastActualRuntime(task.getTaskId())));
    }

    private static void printFriendlyResult(Task task, HEFTSchedulerV3 scheduler) {
        System.out.println("\nRESULT");
        System.out.println("  Status       : " + task.getStatus());
        String summary = safe(task.getResultSummary());
        String algorithm = task.getAlgorithm();

        if (isSearchAlgorithm(algorithm)) {
            int matches = parseMatches(summary);
            String input = task.getInputValue();
            if (matches > 0) {
                System.out.println("  Verdict      : FOUND");
                System.out.println("  Search text  : \"" + input + "\"");
                System.out.println("  Occurrences  : " + matches);
                String positions = parsePositions(summary);
                if (positions.length() > 0) System.out.println("  Positions    : " + positions);
            } else {
                System.out.println("  Verdict      : NOT FOUND");
                System.out.println("  Search text  : \"" + input + "\"");
                System.out.println("  Occurrences  : 0");
            }
        } else {
            System.out.println("  Output       : " + summary);
        }

        System.out.println("  Scheduler time: " + scheduler.getMakespanMs() + " ms");
        System.out.println("  Tasks        : " + scheduler.getCompletedTasks() + "/" + scheduler.getTaskCount());
        System.out.println("  Rounds       : " + scheduler.getScheduleRounds());
    }

    private static boolean isSearchAlgorithm(String algorithm) {
        return algorithm != null &&
                (algorithm.equalsIgnoreCase("KMP")
                || algorithm.equalsIgnoreCase("Rabin-Karp")
                || algorithm.equalsIgnoreCase("Z Algorithm"));
    }

    private static int parseMatches(String summary) {
        int start = summary.indexOf("Matches=");
        if (start < 0) return 0;
        start += 8;
        int end = summary.indexOf(',', start);
        if (end < 0) end = summary.length();
        try { return Integer.parseInt(summary.substring(start, end).trim()); }
        catch (Exception e) { return 0; }
    }

    private static String parsePositions(String summary) {
        int start = summary.indexOf("Positions=");
        if (start < 0) return "";
        start += 10;
        return summary.substring(start).trim();
    }

    private static void runFullPipeline() {
        String primary = chooseDocument("SELECT PRIMARY DOCUMENT");
        if (primary == null) return;
        String comparison = chooseDifferentDocument(primary, "SELECT COMPARISON DOCUMENT");
        if (comparison == null) return;
        String pattern = requiredText("Enter pattern for the pipeline: ");
        if (pattern == null) return;

        System.out.println("\n============================================================");
        System.out.println("            INTELLIGENT TEXT PROCESSING PIPELINE");
        System.out.println("============================================================");
        System.out.println("Primary document   : " + primary);
        System.out.println("Comparison document: " + comparison);
        System.out.println("Pattern            : \"" + pattern + "\"");
        Task kmp = createTask("Exact Pattern Search", primary, "KMP", "HIGH", pattern, null, 1, 128);
        Task rabin = createTask("Keyword Verification", primary, "Rabin-Karp", "HIGH", pattern, null, 1, 128);
        Task z = createTask("Pattern Structure Analysis", primary, "Z Algorithm", "MEDIUM", pattern, null, 1, 128);
        Task suffix = createTask("Text Index Construction", primary, "Suffix Array", "HIGH", null, null, 2, 256);
        Task fuzzy = createTask("Fuzzy Document Similarity", primary, "Edit Distance", "HIGH", null, comparison, 2, 256);
        Task align = createTask("Cross-Document Sequence Alignment", primary, "Sequence Alignment", "MEDIUM", null, comparison, 2, 256);

        HEFTSchedulerV3 scheduler = createScheduler();
        scheduler.addTask(kmp); scheduler.addTask(rabin); scheduler.addTask(z);
        scheduler.addTask(suffix); scheduler.addTask(fuzzy); scheduler.addTask(align);

        scheduler.addDependency(kmp.getTaskId(), suffix.getTaskId());
        scheduler.addDependency(rabin.getTaskId(), suffix.getTaskId());
        scheduler.addDependency(z.getTaskId(), suffix.getTaskId());
        scheduler.addDependency(z.getTaskId(), fuzzy.getTaskId());
        scheduler.addDependency(suffix.getTaskId(), align.getTaskId());
        scheduler.addDependency(fuzzy.getTaskId(), align.getTaskId());

        runSchedulerSilently(scheduler);

        System.out.println("\nPIPELINE EXECUTION SUMMARY");
        System.out.printf("%-5s %-28s %-17s %-7s %-10s %-14s%n",
                "#", "STAGE", "ALGORITHM", "CPU", "RESOURCE", "RESULT");
        System.out.println("--------------------------------------------------------------------------");

        Task[] tasks = {kmp, rabin, z, suffix, fuzzy, align};
        for (int i = 0; i < tasks.length; i++) {
            Task t = tasks[i];
            int ri = scheduler.getAssignedResource(t.getTaskId());
            String resource = ri >= 0 ? scheduler.getResource(ri).getResourceId() : "NONE";
            System.out.printf("%-5d %-28s %-17s %-7d %-10s %-14s%n",
                    i + 1, truncate(t.getTaskName(), 28), truncate(t.getAlgorithm(), 17),
                    t.getCpuRequired(), resource, pipelineResult(t));
        }

        System.out.println("--------------------------------------------------------------------------");
        System.out.println("COMPLETED : " + scheduler.getCompletedTasks() + "/" + scheduler.getTaskCount());
        System.out.println("FAILED    : " + scheduler.getFailedTasks());
        System.out.println("BLOCKED   : " + scheduler.getBlockedTasks());
        System.out.println("ROUNDS    : " + scheduler.getScheduleRounds());
        System.out.println("MAKESPAN  : " + scheduler.getMakespanMs() + " ms");
        System.out.println("THROUGHPUT: " + formatThroughput(scheduler));

        if (scheduler.getCompletedTasks() == scheduler.getTaskCount()) {
            System.out.println("\nSTATUS: PIPELINE COMPLETED SUCCESSFULLY");
        } else {
            System.out.println("\nSTATUS: PIPELINE REQUIRES ATTENTION");
        }

        for (int i = 0; i < tasks.length; i++) saveHistory(tasks[i], scheduler, "FULL PIPELINE");
        System.out.println("============================================================");
    }

    private static void printSearchFinding(Task task, String label) {
        int matches = parseMatches(safe(task.getResultSummary()));
        System.out.println("  " + label + "       : " +
                (matches > 0 ? "FOUND (" + matches + " occurrence(s))" : "NOT FOUND"));
    }

    private static String pipelineResult(Task task) {
        String algorithm = task.getAlgorithm();
        String summary = safe(task.getResultSummary());
        if (isSearchAlgorithm(algorithm)) {
            int matches = parseMatches(summary);
            return matches > 0 ? "FOUND x" + matches : "NOT FOUND";
        }
        if (algorithm.equalsIgnoreCase("Edit Distance")) {
            int comma = summary.indexOf(',');
            return comma > 0 ? summary.substring(0, comma) : truncate(summary, 14);
        }
        if (algorithm.equalsIgnoreCase("Sequence Alignment")) {
            return "SCORE " + parseAfter(summary, "Alignment score=");
        }
        return "COMPLETED";
    }

    private static String parseAfter(String text, String key) {
        int p = text.indexOf(key);
        if (p < 0) return "-";
        String value = text.substring(p + key.length());
        int comma = value.indexOf(',');
        return comma >= 0 ? value.substring(0, comma) : value;
    }

    private static String formatThroughput(HEFTSchedulerV3 scheduler) {
        long ms = scheduler.getMakespanMs();
        if (ms <= 0) return "n/a";
        return String.format("%.2f tasks/sec", scheduler.getCompletedTasks() * 1000.0 / ms);
    }

    private static void showSchedulerDashboard() {
        System.out.println("\n============================================================");
        System.out.println("             SCHEDULING + RESOURCE DASHBOARD");
        System.out.println("============================================================");
        System.out.println("LOGICAL HETEROGENEOUS WORKERS");
        System.out.println("  R01  High Performance Worker   4 CPU   4096 MB");
        System.out.println("  R02  Balanced Worker           2 CPU   2048 MB");
        System.out.println("  R03  Lightweight Worker        1 CPU   1024 MB");
        SystemResourceDiscovery.SystemProfile profile =
                new SystemResourceDiscovery().discover();
        System.out.println("\nHOST SNAPSHOT (SEPARATE FROM LOGICAL WORKERS)");
        System.out.println("  Available processor threads : " + profile.getAvailableCpu());
        System.out.println("  JVM free memory             : " + profile.getAvailableMemoryMB() + " MB");
        System.out.println("  JVM maximum memory          : " + profile.getTotalMemoryMB() + " MB");
        System.out.println("  Important: worker capacities are modeled scheduler resources.");
        System.out.println("============================================================");
    }

    private static void showHistory() {
        File file = new File("results/request_history.txt");
        System.out.println("\n============================================================");
        System.out.println("                    REQUEST HISTORY");
        System.out.println("============================================================");
        if (!file.exists()) {
            System.out.println("No requests have been recorded yet.");
            return;
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null) {
                count++;
                System.out.println(String.format("%2d. %s", count, line));
            }
            reader.close();
            System.out.println("------------------------------------------------------------");
            System.out.println("Saved requests: " + count);
        } catch (Exception e) {
            System.out.println("Unable to read history: " + e.getMessage());
        }
        System.out.println("============================================================");
    }

    private static void runCitationFlow() {
        System.out.println("\n============================================================");
        System.out.println("               DOCUMENT CITATION-FLOW ANALYSIS");
        System.out.println("============================================================");

        int[][] capacity = new int[12][12];
        capacity[0][1] = 8; capacity[0][2] = 5;
        capacity[1][3] = 6; capacity[1][4] = 4;
        capacity[2][4] = 7; capacity[2][5] = 3;
        capacity[3][6] = 5; capacity[4][6] = 4; capacity[4][7] = 6;
        capacity[5][7] = 5; capacity[6][8] = 7; capacity[7][8] = 6;
        capacity[8][9] = 10;

        EdmondsKarp algorithm = new EdmondsKarp();
        EdmondsKarp.FlowResult result = algorithm.maxFlow(capacity, 0, 9);

        System.out.println("\nFLOW RESULT");
        System.out.println("  Source             : D01");
        System.out.println("  Target             : D10");
        System.out.println("  Maximum flow       : " + result.getMaxFlow());
        System.out.println("  Augmenting paths   : " + result.getAugmentingPaths());
        System.out.println("  Algorithm          : Edmonds-Karp");
        System.out.println("============================================================");
    }

    private static void runPrimalityTest() {
        System.out.println("\n============================================================");
        System.out.println("                  MILLER-RABIN PRIMALITY");
        System.out.println("============================================================");
        long n = readLong("Enter number: ");
        MillerRabin tester = new MillerRabin();
        long start = System.nanoTime();
        String result = tester.classify(n);
        long elapsed = System.nanoTime() - start;

        System.out.println("\nRESULT");
        System.out.println("  Number       : " + n);
        System.out.println("  Classification: " + result);
        System.out.println("  Witness rounds: 8");
        System.out.println("  Runtime       : " + String.format("%.3f ms", elapsed / 1_000_000.0));
        System.out.println("  Algorithm     : Miller-Rabin");
        System.out.println("============================================================");
    }


    private static void runPatternSearchHub() {
        System.out.println("\n+============================================================+");
        System.out.println("|                    PATTERN SEARCH                         |");
        System.out.println("+============================================================+");
        System.out.println("Choose the string algorithm for the same pattern-search problem.");
        System.out.println("  1  KMP             deterministic O(n + m)");
        System.out.println("  2  Rabin-Karp      expected/average linear behaviour");
        System.out.println("  3  Z Algorithm     linear-time prefix matching");
        System.out.println("  4  Compare all     benchmark the three implementations");
        System.out.println("  0  Back");
        int choice = readInt("Choose algorithm: ");
        if (choice == 0) return;
        if (choice == 4) { runPatternAlgorithmComparison(); return; }
        if (choice < 1 || choice > 3) { System.out.println("Invalid choice."); return; }
        if (choice == 1) runExactPatternMatching();
        else if (choice == 2) runKeywordSearch();
        else runZAnalysis();
    }

    private static void runPatternAlgorithmComparison() {
        String id = chooseDocument("SELECT DOCUMENT FOR ALGORITHM COMPARISON");
        if (id == null) return;
        String pattern = requiredText("Enter pattern: ");
        if (pattern == null) return;
        String text = DOCUMENTS.readDocumentContent(id);
        if (text == null) text = "";

        KMP kmp = new KMP();
        RabinKarp rk = new RabinKarp();
        ZAlgorithm z = new ZAlgorithm();

        long a = System.nanoTime();
        int[] k = kmp.search(text, pattern);
        long b = System.nanoTime();
        int[] r = rk.search(text, pattern);
        long c = System.nanoTime();
        int[] zz = z.search(text, pattern);
        long d = System.nanoTime();

        System.out.println("\n+============================================================+");
        System.out.println("|                 ALGORITHM COMPARISON                     |");
        System.out.println("+============================================================+");
        System.out.println("Document : " + id);
        System.out.println("Pattern  : \"" + pattern + "\"");
        System.out.println("Text size: " + text.length() + " characters");
        System.out.println("\nALGORITHM        MATCHES     TIME         COMPLEXITY");
        System.out.println("------------------------------------------------------------");
        System.out.printf("KMP              %-10d %-12s O(n + m)%n", k.length, formatNs(b-a));
        System.out.printf("Rabin-Karp       %-10d %-12s Expected O(n + m)%n", r.length, formatNs(c-b));
        System.out.printf("Z Algorithm      %-10d %-12s O(n + m)%n", zz.length, formatNs(d-c));
        System.out.println("------------------------------------------------------------");
        boolean same = samePositions(k, r) && samePositions(k, zz);
        System.out.println("Consistency check : " + (same ? "PASS - all algorithms agree" : "WARNING — results differ"));
        System.out.println("Algorithm family : String Algorithms");
        System.out.println("Key idea         : different strategies solve the same matching problem.");
        System.out.println("Note             : microsecond/nanosecond timings are machine-dependent.");
        System.out.println("============================================================");
    }

    private static boolean samePositions(int[] a, int[] b) {
        if (a == null || b == null || a.length != b.length) return false;
        for (int i = 0; i < a.length; i++) if (a[i] != b[i]) return false;
        return true;
    }

    private static String formatNs(long ns) {
        if (ns < 1000) return ns + " ns";
        if (ns < 1000000) return String.format("%.3f us", ns / 1000.0);
        return String.format("%.3f ms", ns / 1000000.0);
    }

    private static void showAlgorithmMap() {
        System.out.println("\n+============================================================+");
        System.out.println("|                 DSA-3 ALGORITHM MAP                       |");
        System.out.println("+============================================================+");
        System.out.println("\nPROBLEM                  ALGORITHM(S)                 COST / MODEL");
        System.out.println("--------------------------------------------------------------------");
        System.out.println("Pattern search           KMP / Z / Rabin-Karp        O(n+m) / expected");
        System.out.println("Fuzzy matching           Levenshtein DP              O(nm)");
        System.out.println("Sequence alignment       Needleman-Wunsch style DP   O(nm)");
        System.out.println("Text indexing            Suffix Array + LCP          suffix-based index");
        System.out.println("Citation flow            Edmonds-Karp / Max Flow     O(VE^2)");
        System.out.println("Scheduling               HEFT-style heuristic        approximation/heuristic");
        System.out.println("Primality                Miller-Rabin                 randomized");
        System.out.println("Parallel execution       Independent DAG tasks        work/span model");
        System.out.println("--------------------------------------------------------------------");
        System.out.println("MODULE CONNECTION");
        System.out.println("  M2 String Algorithms          -> KMP, Z, Rabin-Karp, suffix structures");
        System.out.println("  M3 Advanced Dynamic Programming -> edit distance, alignment");
        System.out.println("  M4 Network Flow               -> citation-flow / max-flow");
        System.out.println("  M5 NP-Hard & Approximation    -> resource-aware scheduling layer");
        System.out.println("  M6 Randomized & Parallel      -> Miller-Rabin + parallel DAG execution");
        System.out.println("\nENGINE PRINCIPLE");
        System.out.println("  Real problem -> algorithmic task -> dependency DAG -> resource choice");
        System.out.println("  -> execution -> measured result -> complexity + performance evidence");
        System.out.println("============================================================");
    }

    private static void showSystemOverview() {
        System.out.println("\n+============================================================+");
        System.out.println("|                  TEXT HACK ARCHITECTURE                   |");
        System.out.println("+============================================================+");
        System.out.println("\nUSER PROBLEM");
        System.out.println("      ->");
        System.out.println("ALGORITHM SELECTION / REQUEST CREATION");
        System.out.println("      ->");
        System.out.println("TASK MODEL + DEPENDENCY DAG");
        System.out.println("      ->");
        System.out.println("RUNTIME-AWARE HEFT V3 SCHEDULER");
        System.out.println("      ->");
        System.out.println("+--------------+--------------+--------------+");
        System.out.println("| R01          | R02          | R03          |");
        System.out.println("| 4 CPU        | 2 CPU        | 1 CPU        |");
        System.out.println("| 4096 MB      | 2048 MB      | 1024 MB      |");
        System.out.println("+--------------+--------------+--------------+");
        System.out.println("      ->");
        System.out.println("REAL ALGORITHM EXECUTION");
        System.out.println("      ->");
        System.out.println("RESULT + RUNTIME + RESOURCE + HISTORY");
        System.out.println("\nDESIGN GUARANTEES");
        System.out.println("  [OK] Dependency-aware execution");
        System.out.println("  [OK] Priority-aware ready-task selection");
        System.out.println("  [OK] CPU/RAM compatibility checks");
        System.out.println("  [OK] Parallel-ready independent tasks");
        System.out.println("  [OK] Runtime measurement");
        System.out.println("  [OK] Failure recovery support");
        System.out.println("  [OK] Manual core algorithm/data structures");
        System.out.println("\nIMPORTANT");
        System.out.println("  TextHack is not only a scheduler. The algorithms solve the text");
        System.out.println("  problem; the scheduler decides how those workloads should run.");
        System.out.println("============================================================");
    }

    private static Task createTask(String name, String documentId,
            String algorithm, String priority, String inputValue,
            String secondaryDocumentId, int cpu, int memory) {
        WorkloadAnalyzer.WorkloadProfile profile =
                new WorkloadAnalyzer().analyze(documentId);
        int duration = profile == null ? 1 : profile.getEstimatedDuration();
        if (algorithm.equalsIgnoreCase("Edit Distance")
                || algorithm.equalsIgnoreCase("Sequence Alignment")
                || algorithm.equalsIgnoreCase("Suffix Array")) duration++;

        Task task = new Task(String.format("USR%03d", requestCounter++), name,
                documentId, algorithm, priority, cpu, memory, duration);
        task.setInputValue(inputValue);
        task.setSecondaryDocumentId(secondaryDocumentId);
        return task;
    }

    private static HEFTSchedulerV3 createScheduler() {
        HEFTSchedulerV3 scheduler = new HEFTSchedulerV3(40, 3);
        scheduler.addResource(new Resource("R01", "High Performance Worker", 4, 4096));
        scheduler.addResource(new Resource("R02", "Balanced Worker", 2, 2048));
        scheduler.addResource(new Resource("R03", "Lightweight Worker", 1, 1024));
        return scheduler;
    }

    private static void runSchedulerSilently(HEFTSchedulerV3 scheduler) {
        PrintStream original = System.out;
        ByteArrayOutputStream sink = new ByteArrayOutputStream();
        PrintStream quiet = new PrintStream(sink);
        try {
            System.setOut(quiet);
            scheduler.execute();
        } finally {
            quiet.flush();
            System.setOut(original);
            quiet.close();
        }
    }

    private static void saveHistory(Task task, HEFTSchedulerV3 scheduler, String operation) {
        try {
            File results = new File("results");
            if (!results.exists()) results.mkdirs();
            FileWriter writer = new FileWriter(new File(results, "request_history.txt"), true);
            int resource = scheduler.getAssignedResource(task.getTaskId());
            String resourceId = resource >= 0 ? scheduler.getResource(resource).getResourceId() : "NONE";
            writer.write("REQUEST " + task.getTaskId() + " | " + operation
                    + " | Document=" + task.getDocumentId()
                    + " | Algorithm=" + task.getAlgorithm()
                    + " | Status=" + task.getStatus()
                    + " | Resource=" + resourceId
                    + " | Runtime=" + String.format("%.2f", scheduler.getLastActualRuntime(task.getTaskId()))
                    + " ms | Result=" + safe(task.getResultSummary()) + "\n");
            writer.close();
        } catch (Exception e) {
            System.out.println("Warning: request history could not be saved.");
        }
    }

    private static String chooseDocument(String heading) {
        File folder = new File("documents");
        File[] files = folder.listFiles();
        if (files == null) {
            System.out.println("Document store is unavailable.");
            return null;
        }

        String[] ids = new String[300];
        String[] names = new String[300];
        int count = 0;
        for (int i = 0; i < files.length && count < 300; i++) {
            String name = files[i].getName();
            if (files[i].isFile() && name.toLowerCase().endsWith(".txt")) {
                ids[count] = name.substring(0, name.length() - 4);
                names[count] = name;
                count++;
            }
        }
        sortDocumentLists(ids, names, count);

        System.out.println("\n" + heading);
        System.out.println("------------------------------------------------------------");
        for (int i = 0; i < count; i++) {
            System.out.println(String.format("%2d. %-8s (%s)", i + 1, ids[i], names[i]));
        }
        if (count == 0) {
            System.out.println("No TXT documents found.");
            return null;
        }
        int choice = readInt("Select document: ");
        if (choice < 1 || choice > count) {
            System.out.println("Invalid document selection.");
            return null;
        }
        return ids[choice - 1];
    }

    private static String chooseDifferentDocument(String first, String heading) {
        File folder = new File("documents");
        File[] files = folder.listFiles();
        if (files == null) return null;

        String[] ids = new String[300];
        int count = 0;
        for (int i = 0; i < files.length && count < 300; i++) {
            String name = files[i].getName();
            if (files[i].isFile() && name.toLowerCase().endsWith(".txt")) {
                String id = name.substring(0, name.length() - 4);
                if (!id.equals(first)) ids[count++] = id;
            }
        }
        sortStrings(ids, count);

        System.out.println("\n" + heading);
        System.out.println("------------------------------------------------------------");
        for (int i = 0; i < count; i++) System.out.println(String.format("%2d. %s", i + 1, ids[i]));
        if (count == 0) {
            System.out.println("A second TXT document is required.");
            return null;
        }
        int choice = readInt("Select document: ");
        if (choice < 1 || choice > count) {
            System.out.println("Invalid document selection.");
            return null;
        }
        return ids[choice - 1];
    }

    private static String requiredText(String prompt) {
        String value = readLine(prompt);
        if (value == null || value.trim().length() == 0) {
            System.out.println("Input cannot be empty.");
            return null;
        }
        return value;
    }

    private static int readInt(String prompt) {
        while (true) {
            String value = readLine(prompt);
            try { return Integer.parseInt(value.trim()); }
            catch (Exception e) { System.out.println("Please enter a valid integer."); }
        }
    }

    private static long readLong(String prompt) {
        while (true) {
            String value = readLine(prompt);
            try { return Long.parseLong(value.trim()); }
            catch (Exception e) { System.out.println("Please enter a valid whole number."); }
        }
    }

    private static String readLine(String prompt) {
        try {
            System.out.print(prompt);
            String line = INPUT.readLine();
            return line == null ? "" : line;
        } catch (Exception e) {
            return "";
        }
    }

    private static String[] readMetadata(String id) {
        String[] result = {"Unknown", "Unknown", "Unknown"};
        File file = new File("documents" + File.separator + id + ".txt");
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("TITLE:")) result[0] = line.substring(6).trim();
                else if (line.startsWith("AUTHOR:")) result[1] = line.substring(7).trim();
                else if (line.startsWith("CATEGORY:")) result[2] = line.substring(9).trim();
            }
            reader.close();
        } catch (Exception e) { }
        return result;
    }

    private static int countDocuments() {
        File folder = new File("documents");
        File[] files = folder.listFiles();
        int count = 0;
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile() && files[i].getName().toLowerCase().endsWith(".txt")) count++;
            }
        }
        return count;
    }

    private static int collectDocuments(File[] files, String[] ids, String[] titles, long[] sizes) {
        int count = 0;
        for (int i = 0; i < files.length && count < ids.length; i++) {
            String name = files[i].getName();
            if (!files[i].isFile() || !name.toLowerCase().endsWith(".txt")) continue;
            String id = name.substring(0, name.length() - 4);
            ids[count] = id;
            sizes[count] = files[i].length();
            String[] meta = readMetadata(id);
            titles[count] = meta[0];
            count++;
        }
        sortDocumentDetails(ids, titles, sizes, count);
        return count;
    }

    private static void ensureDemoDocuments() {
        File folder = new File("documents");
        if (!folder.exists()) folder.mkdirs();

        createIfMissing("D01", "Operating Systems", "Corpus Team", "Computer Science",
                "Operating systems manage processes, memory, files and hardware resources. "
                + "The kernel provides process scheduling, virtual memory, protection and system calls. "
                + "Efficient resource management allows applications to share a computer safely.");
        createIfMissing("D02", "Computer Networks", "Corpus Team", "Computer Science",
                "Computer networks connect systems so processes can exchange information. "
                + "Routing, addressing, transport protocols and congestion control coordinate communication. "
                + "Reliable networks depend on efficient use of links, buffers and processing resources.");
        createIfMissing("D03", "Data Structures and Algorithms", "Corpus Team", "Algorithms",
                "Data structures organize information for efficient access. Trees, graphs, heaps and hash tables "
                + "support different operations, while advanced algorithms solve matching, optimization and search problems. "
                + "Algorithm choice depends on input size, structure and required performance.");
        createIfMissing("D04", "Indian Space Research", "Corpus Team", "Indian Knowledge",
                "India has developed a broad space programme involving launch vehicles, satellites and planetary missions. "
                + "Space research requires scheduling experiments, processing telemetry and indexing large collections of data. "
                + "Scientific missions depend on reliable computation and careful allocation of limited resources.");
        createIfMissing("D05", "Database Systems", "Corpus Team", "Computer Science",
                "Database management systems organize structured information using schemas, queries, indexes and transactions. "
                + "Indexes accelerate search, while concurrency control and recovery protect consistency. "
                + "Efficient database workloads benefit from intelligent scheduling and resource-aware execution.");
        createIfMissing("D06", "Telugu Language and Literature", "Corpus Team", "Indian Language",
                "Telugu is a major Indian language with a long literary tradition. తెలుగు భాషలో సాహిత్యం, కవిత్వం మరియు కథా రచనలు ఉన్నాయి. "
                + "Digital text collections allow researchers to search, compare and index large language corpora efficiently.");
        createIfMissing("D07", "Indian Classical Dance", "Corpus Team", "Indian Culture",
                "Bharatanatyam is a classical Indian dance tradition associated with expressive movement, rhythm and storytelling. "
                + "भारतीय शास्त्रीय नृत्य परंपराएँ संगीत, लय और अभिनय के माध्यम से सांस्कृतिक ज्ञान को आगे बढ़ाती हैं। "
                + "Indian performing arts preserve cultural knowledge through generations and provide rich text collections for analysis.");
        createIfMissing("D08", "Indian Constitution", "Corpus Team", "Civics",
                "The Constitution of India defines institutions, rights, duties and principles for governance. "
                + "भारत का संविधान शासन, अधिकारों और कर्तव्यों के लिए एक विस्तृत कानूनी ढाँचा प्रदान करता है। "
                + "Its text is organized into articles, schedules and amendments. Searching legal phrases and comparing revisions "
                + "are useful examples of large-scale text processing.");
        createIfMissing("D09", "Indian Rivers and Geography", "Corpus Team", "Geography",
                "Indian geography includes major river systems such as the Ganga, Brahmaputra and Godavari. "
                + "ಭಾರತದ ಭೌಗೋಳಿಕ ದಾಖಲೆಗಳು ನದಿಗಳು, ಪ್ರದೇಶಗಳು ಮತ್ತು ಸಂಪನ್ಮೂಲಗಳ ನಡುವಿನ ಸಂಬಂಧಗಳನ್ನು ವಿವರಿಸುತ್ತವೆ. "
                + "Geographical documents contain names, relationships and descriptions that can be searched, indexed and compared. "
                + "Graph representations can also model connections among places and resources.");
        createIfMissing("D10", "Artificial Intelligence in India", "Corpus Team", "Technology",
                "Artificial intelligence research in India spans language technology, computer vision, education and healthcare applications. "
                + "Text analytics systems use pattern matching, similarity measures and indexing to transform documents into searchable knowledge. "
                + "Efficient scheduling helps execute many independent analytics tasks concurrently.");
    }

    private static void createIfMissing(String id, String title, String author,
            String category, String content) {
        if (!DOCUMENTS.documentExists(id)) {
            DOCUMENTS.saveDocument(new Document(id, title, author, category, content));
        }
    }

    private static void sortStrings(String[] values, int count) {
        for (int i = 0; i < count - 1; i++) {
            int best = i;
            for (int j = i + 1; j < count; j++) {
                if (values[j].compareToIgnoreCase(values[best]) < 0) best = j;
            }
            if (best != i) {
                String t = values[i]; values[i] = values[best]; values[best] = t;
            }
        }
    }

    private static void sortDocumentLists(String[] ids, String[] names, int count) {
        for (int i = 0; i < count - 1; i++) {
            int best = i;
            for (int j = i + 1; j < count; j++) {
                if (ids[j].compareToIgnoreCase(ids[best]) < 0) best = j;
            }
            if (best != i) {
                String id = ids[i]; ids[i] = ids[best]; ids[best] = id;
                String name = names[i]; names[i] = names[best]; names[best] = name;
            }
        }
    }

    private static void sortDocumentDetails(String[] ids, String[] titles, long[] sizes, int count) {
        for (int i = 0; i < count - 1; i++) {
            int best = i;
            for (int j = i + 1; j < count; j++) {
                if (ids[j].compareToIgnoreCase(ids[best]) < 0) best = j;
            }
            if (best != i) {
                String a = ids[i]; ids[i] = ids[best]; ids[best] = a;
                String b = titles[i]; titles[i] = titles[best]; titles[best] = b;
                long c = sizes[i]; sizes[i] = sizes[best]; sizes[best] = c;
            }
        }
    }

    private static String oneLine(String text) {
        if (text == null) return "";
        return text.replace('\n', ' ').replace('\r', ' ').trim();
    }

    private static String truncate(String text, int max) {
        if (text == null) return "";
        if (text.length() <= max) return text;
        return text.substring(0, Math.max(0, max - 3)) + "...";
    }

    private static String formatMs(double ms) {
        if (ms < 0) return "n/a";
        return String.format("%.2f ms", ms);
    }

    private static String safe(String value) {
        return value == null || value.length() == 0 ? "No result summary" : value;
    }
}
