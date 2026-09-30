package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import execution.PerformanceMonitor;
import execution.RecoveryManager;
import execution.TaskExecutor;
import model.Document;
import model.Resource;
import model.Task;
import scheduling.HEFTScheduler;
import scheduling.WorkloadAnalyzer;
import storage.DocumentFileManager;

/**
 * Console entry point for the Intelligent Resource Scheduling PBL project.
 * The interface deliberately stays simple so the scheduling engine remains
 * easy to demonstrate in Eclipse.
 */
public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final DocumentFileManager files = new DocumentFileManager();
    private static final WorkloadAnalyzer analyzer = new WorkloadAnalyzer();
    private static final TaskExecutor executor = new TaskExecutor();
    private static final RecoveryManager recovery = new RecoveryManager(1);
    private static final PerformanceMonitor performance = new PerformanceMonitor();

    private static final Resource[] resources = {
        new Resource("R01", "High Performance", 4, 4096),
        new Resource("R02", "Balanced", 2, 2048),
        new Resource("R03", "Lightweight", 1, 1024)
    };

    private static final List<Task> taskHistory = new ArrayList<Task>();
    private static int taskSequence = 1;
    private static final List<String> resultHistory = new ArrayList<String>();

    public static void main(String[] args) {
        seedDocuments();
        showWelcome();
        pause();

        while (true) {
            showMainMenu();
            String choice = prompt("Choose an option");
            switch (choice) {
                case "1": documentsMenu(); break;
                case "2": processingRequestMenu(); break;
                case "3": pipelineMenu(); break;
                case "4": activityMenu(); break;
                case "5": taskQueueMenu(); break;
                case "6": resourceCenterMenu(); break;
                case "7": resultsMenu(); break;
                case "8": performanceMenu(); break;
                case "9": systemInfo(); break;
                case "0":
                    System.out.println("\nThank you for using Intelligent Text Workspace.");
                    return;
                default: message("Please choose a valid option.");
            }
        }
    }

    private static void showWelcome() {
        System.out.println();
        System.out.println("+------------------------------------------------------------+");
        System.out.println("|                                                            |");
        System.out.println("|              INTELLIGENT TEXT WORKSPACE                   |");
        System.out.println("|                                                            |");
        System.out.println("|       Resource-aware text processing and scheduling       |");
        System.out.println("|                                                            |");
        System.out.println("+------------------------------------------------------------+");
        System.out.println();
        System.out.println("Welcome to your workspace.");
        System.out.println();
        System.out.printf("Documents available : %d%n", files.getDocumentIds().size());
        System.out.printf("Processing workers  : %d%n", resources.length);
        System.out.printf("CPU capacity        : %d cores%n", totalCpu());
        System.out.printf("Memory capacity     : %d MB%n", totalMemory());
        System.out.println("System status       : READY");
    }

    private static void showMainMenu() {
        header("INTELLIGENT TEXT WORKSPACE", "Resource-aware text processing and scheduling");
        section("WORKSPACE");
        item("1", "Document Library", "Manage and explore documents");
        item("2", "New Processing Request", "Submit text-processing work");
        item("3", "Processing Pipelines", "Create multi-step processing workflows");

        section("SCHEDULER");
        item("4", "Live Activity", "View current processing activity");
        item("5", "Task Queue", "View ready, waiting and completed tasks");
        item("6", "Resource Center", "View available processing resources");

        section("INSIGHTS");
        item("7", "Results & History", "View completed processing results");
        item("8", "Performance", "View measured execution information");

        section("SYSTEM");
        item("9", "System Overview", "View project capabilities");
        item("0", "Exit Workspace", "Close the application");

        divider();
        System.out.printf("Status: READY    Documents: %d    Workers: %d%n", files.getDocumentIds().size(), resources.length);
        divider();
    }

    private static void documentsMenu() {
        while (true) {
            header("DOCUMENT LIBRARY", "Browse, add and explore the workspace documents");
            item("1", "Browse Documents", "View the document library");
            item("2", "Open Document", "Read a document");
            item("3", "Add Document", "Add a new text document");
            item("4", "Search Library", "Find documents by title, category or content");
            item("5", "Document Details", "View document statistics");
            item("6", "Remove Document", "Delete a document from the library");
            item("0", "Back", "Return to the workspace");
            divider();

            String c = prompt("Choose an option");
            if ("1".equals(c)) browseDocuments();
            else if ("2".equals(c)) openDocument();
            else if ("3".equals(c)) addDocument();
            else if ("4".equals(c)) searchLibrary();
            else if ("5".equals(c)) documentDetails();
            else if ("6".equals(c)) removeDocument();
            else if ("0".equals(c)) return;
            else message("Please choose a valid option.");
        }
    }

    private static void browseDocuments() {
        List<String> ids = files.getDocumentIds();
        System.out.println();
        System.out.printf("%-6s %-34s %-22s%n", "ID", "DOCUMENT", "CATEGORY");
        System.out.println("-".repeat(70));
        for (String id : ids) {
            Document d = files.loadDocument(id);
            System.out.printf("%-6s %-34s %-22s%n", id, trim(d == null ? id : d.getTitle(), 34), trim(d == null ? "Text" : d.getCategory(), 22));
        }
        System.out.println();
        System.out.println("Total documents: " + ids.size());
        pause();
    }

    private static void openDocument() {
        String id = prompt("Document ID").toUpperCase(Locale.ENGLISH);
        Document d = files.loadDocument(id);
        if (d == null) {
            message("Document not found.");
            return;
        }
        System.out.println();
        System.out.println("DOCUMENT");
        System.out.println("-".repeat(70));
        System.out.println("ID       : " + d.getDocumentId());
        System.out.println("Title    : " + d.getTitle());
        System.out.println("Author   : " + d.getAuthor());
        System.out.println("Category : " + d.getCategory());
        System.out.println("-".repeat(70));
        System.out.println(d.getContent());
        pause();
    }

    private static void addDocument() {
        System.out.println();
        System.out.println("ADD DOCUMENT");
        String id = prompt("Document ID").toUpperCase(Locale.ENGLISH);
        if (files.loadDocument(id) != null) {
            message("That document ID already exists.");
            return;
        }
        String title = prompt("Title");
        String author = prompt("Author");
        String category = prompt("Category");
        System.out.println("Enter content. Type END on a new line when finished.");
        StringBuilder content = new StringBuilder();
        while (true) {
            String line = scanner.nextLine();
            if ("END".equals(line)) break;
            content.append(line).append('\n');
        }
        files.saveDocument(new Document(id, title, author, category, content.toString()));
    }

    private static void searchLibrary() {
        String query = prompt("Search text").toLowerCase(Locale.ENGLISH);
        if (query.isEmpty()) {
            message("Enter some text to search for.");
            return;
        }
        int matches = 0;
        System.out.println();
        System.out.printf("%-6s %-34s %-22s%n", "ID", "DOCUMENT", "MATCH");
        System.out.println("-".repeat(70));
        for (String id : files.getDocumentIds()) {
            Document d = files.loadDocument(id);
            String searchable = (d.getTitle() + " " + d.getCategory() + " " + d.getContent()).toLowerCase(Locale.ENGLISH);
            if (searchable.contains(query)) {
                System.out.printf("%-6s %-34s %-22s%n", id, trim(d.getTitle(), 34), "Found");
                matches++;
            }
        }
        System.out.println("-".repeat(70));
        System.out.println("Documents matched: " + matches);
        pause();
    }

    private static void documentDetails() {
        String id = prompt("Document ID").toUpperCase(Locale.ENGLISH);
        Document d = files.loadDocument(id);
        if (d == null) {
            message("Document not found.");
            return;
        }
        WorkloadAnalyzer.Profile p = analyzer.analyze(d);
        System.out.println();
        System.out.println("DOCUMENT DETAILS");
        System.out.println("-".repeat(70));
        System.out.println("Title          : " + d.getTitle());
        System.out.println("Category       : " + d.getCategory());
        System.out.println("Characters     : " + p.characters);
        System.out.println("Words          : " + p.words);
        System.out.println("Lines          : " + p.lines);
        System.out.println("Unique words   : " + p.uniqueWords);
        System.out.println("Estimated CPU  : " + p.cpu + " core(s)");
        System.out.println("Estimated memory: " + p.memory + " MB");
        System.out.println("Estimated work : " + p.estimatedMs + " ms");
        pause();
    }

    private static void removeDocument() {
        String id = prompt("Document ID").toUpperCase(Locale.ENGLISH);
        System.out.println(files.deleteDocument(id) ? "Document removed." : "Document not found.");
    }

    private static void processingRequestMenu() {
        while (true) {
            header("NEW PROCESSING REQUEST", "Choose what you want the workspace to process");
            item("1", "Search Text", "Find a phrase inside a document");
            item("2", "Compare Documents", "Measure similarity between two documents");
            item("3", "Analyze Document", "View document statistics and processing information");
            item("4", "Find Shared Content", "Inspect common textual information");
            item("5", "Full Document Analysis", "Run several processing stages as one request");
            item("0", "Back", "Return to the workspace");
            divider();
            String c = prompt("Choose an option");
            if ("1".equals(c)) searchTextRequest();
            else if ("2".equals(c)) compareRequest();
            else if ("3".equals(c)) analyzeRequest();
            else if ("4".equals(c)) sharedContentRequest();
            else if ("5".equals(c)) fullAnalysisRequest();
            else if ("0".equals(c)) return;
            else message("Please choose a valid option.");
        }
    }

    private static void searchTextRequest() {
        String id = prompt("Document ID").toUpperCase(Locale.ENGLISH);
        if (!exists(id)) return;
        String query = prompt("Text to find");
        if (query.isEmpty()) {
            message("Search text cannot be empty.");
            return;
        }
        Task task = new Task(nextTaskId("S"), "Fast Text Search", id, "SEARCH", query, "", 3, 1, 128, 400);
        runTask(task);
        showResult(task);
    }

    private static void compareRequest() {
        String a = prompt("First document ID").toUpperCase(Locale.ENGLISH);
        String b = prompt("Second document ID").toUpperCase(Locale.ENGLISH);
        if (!validPair(a, b)) return;
        Task task = new Task(nextTaskId("C"), "Document Comparison", a, "SIMILARITY", "", b, 3, 2, 256, 600);
        runTask(task);
        showResult(task);
    }

    private static void analyzeRequest() {
        String id = prompt("Document ID").toUpperCase(Locale.ENGLISH);
        if (!exists(id)) return;
        Document d = files.loadDocument(id);
        WorkloadAnalyzer.Profile p = analyzer.analyze(d);
        Task task = new Task(nextTaskId("A"), "Content Analysis", id, "ANALYZE", "", "", 2, p.cpu, p.memory, p.estimatedMs);
        long start = System.currentTimeMillis();
        task.setStatus("RUNNING");
        task.setResult(String.format("%d words, %d unique words, %d characters", p.words, p.uniqueWords, p.characters));
        task.setStatus("COMPLETED");
        task.setActualDurationMs(System.currentTimeMillis() - start);
        taskHistory.add(task);
        resultHistory.add(task.getResult());
        showResult(task);
    }

    private static void sharedContentRequest() {
        String a = prompt("First document ID").toUpperCase(Locale.ENGLISH);
        String b = prompt("Second document ID").toUpperCase(Locale.ENGLISH);
        if (!validPair(a, b)) return;
        Task task = new Task(nextTaskId("H"), "Shared Content Analysis", a, "SHARED_CONTENT", "", b, 2, 2, 256, 700);
        runTask(task);
        showResult(task);
    }

    private static void fullAnalysisRequest() {
        String a = prompt("First document ID").toUpperCase(Locale.ENGLISH);
        String b = prompt("Second document ID").toUpperCase(Locale.ENGLISH);
        if (!validPair(a, b)) return;

        Task t1 = new Task(nextTaskId("F"), "Fast Text Search", a, "SEARCH", "data", "", 3, 1, 128, 400);
        Task t2 = new Task(nextTaskId("F"), "Document Comparison", a, "SIMILARITY", "", b, 3, 2, 256, 600);
        Task t3 = new Task(nextTaskId("F"), "Shared Content Analysis", a, "SHARED_CONTENT", "", b, 2, 2, 256, 700);
        Task[] tasks = {t1, t2, t3};

        header("PROCESSING REQUEST", "The scheduler prepares and executes the requested stages");
        System.out.println("Documents selected : 2");
        System.out.println("Processing stages  : 3");
        System.out.println("Execution order    : planned sequence");
        System.out.println();
        System.out.println("Execution plan prepared.");
        System.out.println();

        performance.start();
        for (int i = 0; i < tasks.length; i++) {
            Task task = tasks[i];
            System.out.printf("[%d/3] %-28s ", i + 1, friendlyName(task));
            boolean success = executeScheduledTask(task);
            System.out.println(success ? "DONE" : "FAILED");
        }
        performance.stop();
        performance.setCompleted(countCompleted(tasks));
        System.out.println();
        System.out.println("Processing complete.");
        System.out.println("Measured request time: " + performance.elapsed() + " ms");
        for (Task task : tasks) showCompactResult(task);
        pause();
    }

    private static void pipelineMenu() {
        header("PROCESSING PIPELINES", "Combine several text-processing steps into one organized request");
        System.out.println("Example pipeline:");
        System.out.println();
        System.out.println("Search");
        System.out.println("   |");
        System.out.println("Comparison");
        System.out.println("   |");
        System.out.println("Shared Content");
        System.out.println();
        System.out.println("The existing task and scheduling classes are used for execution.");
        divider();
        String a = prompt("First document ID (or 0 to back)").toUpperCase(Locale.ENGLISH);
        if ("0".equals(a)) return;
        String b = prompt("Second document ID").toUpperCase(Locale.ENGLISH);
        if (!validPair(a, b)) return;

        Task t1 = new Task(nextTaskId("P"), "Fast Text Search", a, "SEARCH", "data", "", 3, 1, 128, 400);
        Task t2 = new Task(nextTaskId("P"), "Document Comparison", a, "SIMILARITY", "", b, 3, 2, 256, 600);
        Task t3 = new Task(nextTaskId("P"), "Shared Content Analysis", a, "SHARED_CONTENT", "", b, 2, 2, 256, 700);
        Task[] tasks = {t1, t2, t3};

        System.out.println();
        System.out.println("PIPELINE READY");
        System.out.println("Documents selected : " + a + " -> " + b);
        System.out.println("Execution order    : Search -> Comparison -> Shared Content");
        System.out.println();
        for (int i = 0; i < tasks.length; i++) {
            System.out.printf("[%d/3] %-28s ", i + 1, friendlyName(tasks[i]));
            System.out.println(executeScheduledTask(tasks[i]) ? "DONE" : "FAILED");
        }
        System.out.println();
        System.out.println("Pipeline execution finished.");
        pause();
    }

    private static void activityMenu() {
        header("LIVE ACTIVITY", "Current processing state");
        boolean running = false;
        for (Task task : taskHistory) {
            if ("RUNNING".equals(task.getStatus())) {
                running = true;
                System.out.printf("%-8s %-28s %s%n", task.getTaskId(), friendlyName(task), task.getStatus());
            }
        }
        if (!running) {
            System.out.println("Current status : READY");
            System.out.println();
            System.out.println("No background jobs are currently running.");
        }
        pause();
    }

    private static void taskQueueMenu() {
        header("TASK QUEUE", "Tasks created during this workspace session");
        if (taskHistory.isEmpty()) {
            System.out.println("No tasks have been created yet.");
            pause();
            return;
        }
        System.out.printf("%-8s %-28s %-10s %-12s%n", "TASK", "WORK", "PRIORITY", "STATUS");
        System.out.println("-".repeat(66));
        for (Task task : taskHistory) {
            System.out.printf("%-8s %-28s %-10s %-12s%n", task.getTaskId(), trim(friendlyName(task), 28), priorityName(task.getPriority()), task.getStatus());
        }
        pause();
    }

    private static void resourceCenterMenu() {
        header("RESOURCE CENTER", "Current processing capacity");
        System.out.printf("%-12s %-20s %-12s %-14s %-10s%n", "WORKER", "PROFILE", "CPU", "MEMORY", "STATUS");
        System.out.println("-".repeat(72));
        for (Resource r : resources) {
            String status = r.isBusy() ? "BUSY" : "IDLE";
            System.out.printf("%-12s %-20s %-12s %-14s %-10s%n", r.getResourceId(), r.getName(), r.getTotalCpu() + " CPU", r.getTotalMemory() + " MB", status);
        }
        System.out.println();
        System.out.println("Total capacity:");
        System.out.println("CPU    : " + totalCpu() + " cores");
        System.out.println("Memory : " + totalMemory() + " MB");
        pause();
    }

    private static void resultsMenu() {
        header("RESULTS & HISTORY", "Results produced by completed processing requests");
        int shown = 0;
        for (int i = taskHistory.size() - 1; i >= 0; i--) {
            Task task = taskHistory.get(i);
            if (!"COMPLETED".equals(task.getStatus())) continue;
            shown++;
            System.out.println("RESULT " + shown);
            System.out.println("  Work       : " + friendlyName(task));
            System.out.println("  Documents  : " + documentPair(task));
            System.out.println("  Result     : " + friendlyResult(task));
            System.out.println("  Time       : " + task.getActualDurationMs() + " ms");
            if (shown < 10) System.out.println();
            if (shown == 10) break;
        }
        if (shown == 0) System.out.println("No completed processing results are available yet.");
        else if (taskHistory.size() > shown) System.out.println("Showing the 10 most recent completed tasks.");
        pause();
    }

    private static void performanceMenu() {
        header("PERFORMANCE", "Measured execution information from this session");
        int completed = 0;
        long totalTime = 0;
        for (Task task : taskHistory) {
            if ("COMPLETED".equals(task.getStatus())) {
                completed++;
                totalTime += task.getActualDurationMs();
            }
        }
        System.out.println("Tasks completed        : " + completed);
        System.out.println("Measured task time     : " + totalTime + " ms");
        System.out.printf("Throughput             : %.2f tasks/sec%n", totalTime == 0 ? 0.0 : completed * 1000.0 / totalTime);
        System.out.println("CPU utilization        : Not measured");
        System.out.println("Memory utilization     : Not measured");
        System.out.println();
        System.out.println("CPU and memory utilization are not sampled after a task releases its resource.");
        System.out.println("Only values measured or calculated from the current session are shown.");
        pause();
    }

    private static void systemInfo() {
        header("SYSTEM OVERVIEW", "Project capabilities and processing model");
        System.out.println("Project:");
        System.out.println("Intelligent Resource Scheduling for Text Processing Pipelines");
        System.out.println();
        System.out.println("Capabilities:");
        System.out.println("  [OK] Dependency-aware processing");
        System.out.println("  [OK] Priority-aware task planning");
        System.out.println("  [OK] Resource-aware execution");
        System.out.println("  [OK] Parallel-ready task model");
        System.out.println("  [OK] Failure recovery");
        System.out.println("  [OK] Runtime measurement");
        System.out.println("  [OK] Text search");
        System.out.println("  [OK] Document comparison");
        System.out.println("  [OK] Content analysis");
        System.out.println();
        System.out.println("Request flow:");
        System.out.println("User Request -> Task Creation -> Planning -> Resource Selection");
        System.out.println("             -> Execution -> Result -> Performance");
        System.out.println();
        System.out.println("Technical implementation details are handled internally by the processing engine.");
        pause();
    }

    private static boolean runTask(Task task) {
        performance.start();
        boolean success = executeScheduledTask(task);
        performance.stop();
        performance.setCompleted(success ? 1 : 0);
        return success;
    }

    private static boolean executeScheduledTask(Task task) {
        taskHistory.add(task);
        HEFTScheduler scheduler = new HEFTScheduler(resources);
        Resource resource = scheduler.chooseResource(task);
        if (resource == null) {
            task.setStatus("WAITING");
            task.setResult("No suitable processing resource is currently available.");
            return false;
        }

        if (!resource.allocate(task)) {
            task.setStatus("WAITING");
            task.setResult("Resource became unavailable before execution.");
            return false;
        }
        boolean success;
        try {
            success = recovery.run(task, executor);
            if (!success && "FAILED".equals(task.getStatus())) task.setStatus("RECOVERED");
        } finally {
            resource.release(task);
        }
        if (success) resultHistory.add(task.getResult());
        return success;
    }

    private static void showResult(Task task) {
        System.out.println();
        System.out.println("PROCESSING COMPLETE");
        System.out.println();
        System.out.println("Work       : " + friendlyName(task));
        System.out.println("Document   : " + task.getDocumentId() + (task.getSecondaryDocumentId().isEmpty() ? "" : " <-> " + task.getSecondaryDocumentId()));
        System.out.println("Result     : " + task.getResult());
        System.out.println("Status     : " + task.getStatus());
        System.out.println("Time       : " + task.getActualDurationMs() + " ms");
        pause();
    }

    private static void showCompactResult(Task task) {
        System.out.println("  " + friendlyName(task) + " : " + friendlyResult(task));
    }

    private static boolean validPair(String a, String b) {
        if (!exists(a) || !exists(b)) return false;
        if (a.equals(b)) {
            message("Please choose two different documents.");
            return false;
        }
        return true;
    }

    private static boolean exists(String id) {
        if (files.loadDocument(id) == null) {
            message("Document " + id + " was not found.");
            return false;
        }
        return true;
    }

    private static String nextTaskId(String prefix) {
        return prefix + String.format("%02d", taskSequence++);
    }

    private static int countCompleted(Task[] tasks) {
        int count = 0;
        for (Task task : tasks) if ("COMPLETED".equals(task.getStatus())) count++;
        return count;
    }

    private static String friendlyName(Task task) {
        String op = task.getOperation();
        if ("SEARCH".equals(op) || "FAST_SEARCH".equals(op)) return "Fast Text Search";
        if ("SIMILARITY".equals(op)) return "Document Comparison";
        if ("SHARED_CONTENT".equals(op)) return "Shared Content Analysis";
        if ("ANALYZE".equals(op)) return "Content Analysis";
        if ("GLOBAL_ALIGNMENT".equals(op) || "LOCAL_ALIGNMENT".equals(op)) return "Document Alignment";
        return task.getTaskName();
    }

    private static String friendlyResult(Task task) {
        String result = task.getResult();
        if (result == null) return "Completed";
        if (result.startsWith("Index created successfully with ")) return "Content analysis completed";
        return result;
    }

    private static String documentPair(Task task) {
        String first = task.getDocumentId();
        String second = task.getSecondaryDocumentId();
        if (second == null || second.isEmpty()) return first;
        return first + " <-> " + second;
    }

    private static String priorityName(int priority) {
        if (priority >= 3) return "HIGH";
        if (priority == 2) return "MEDIUM";
        return "LOW";
    }

    private static int totalCpu() {
        int total = 0;
        for (Resource r : resources) total += r.getTotalCpu();
        return total;
    }

    private static int totalMemory() {
        int total = 0;
        for (Resource r : resources) total += r.getTotalMemory();
        return total;
    }

    private static int utilizationCpu() {
        int used = 0;
        for (Resource r : resources) used += r.getTotalCpu() - r.getAvailableCpu();
        return totalCpu() == 0 ? 0 : used * 100 / totalCpu();
    }

    private static int utilizationMemory() {
        int used = 0;
        for (Resource r : resources) used += r.getTotalMemory() - r.getAvailableMemory();
        return totalMemory() == 0 ? 0 : used * 100 / totalMemory();
    }

    private static void seedDocuments() {
        if (files.loadDocument("D01") == null) {
            files.saveDocument(new Document("D01", "Operating Systems", "Shrenika", "Computer Science",
                    "Operating systems manage computer resources, processes, memory and files. "
                    + "They provide services for applications and manage hardware resources."));
        }
        if (files.loadDocument("D02") == null) {
            files.saveDocument(new Document("D02", "Computer Networks", "Shrenika", "Computer Science",
                    "Computer networks connect systems and allow devices to exchange data. "
                    + "Networks use communication protocols, routing and reliable data transfer."));
        }
        if (files.loadDocument("D03") == null) {
            files.saveDocument(new Document("D03", "Data Structures and Algorithms", "Shrenika", "Computer Science",
                    "Data structures organize information and algorithms process that information. "
                    + "Graphs, trees, arrays and dynamic programming are useful for efficient computation."));
        }
        if (files.loadDocument("D04") == null) {
            files.saveDocument(new Document("D04", "Indian Space Research", "Shrenika", "Science",
                    "Indian space research develops launch vehicles, satellites and scientific missions. "
                    + "Space technology supports communication, navigation and Earth observation."));
        }
        if (files.loadDocument("D05") == null) {
            files.saveDocument(new Document("D05", "Database Systems", "Shrenika", "Computer Science",
                    "Database systems store, organize and retrieve structured information. "
                    + "Transactions, indexing, queries and data management support reliable applications."));
        }
        if (files.loadDocument("D10") == null) {
            files.saveDocument(new Document("D10", "Japanese Language Basics", "Shrenika", "Foreign Language",
                    "Japanese language basics introduce greetings, simple phrases, vocabulary and sentence patterns. "
                    + "Practice supports reading, writing and everyday communication."));
        }
    }

    private static void header(String title, String description) {
        System.out.println();
        System.out.println("+------------------------------------------------------------+");
        System.out.printf("| %-58s |%n", title);
        System.out.println("+------------------------------------------------------------+");
        if (description != null && !description.isEmpty()) System.out.println(description);
        System.out.println();
    }

    private static void section(String title) {
        System.out.println();
        System.out.println(title);
        System.out.println("-".repeat(title.length()));
    }

    private static void item(String number, String title, String description) {
        System.out.printf("  %-2s %-28s %s%n", number, title, description);
    }

    private static void divider() {
        System.out.println("------------------------------------------------------------");
    }

    private static String prompt(String label) {
        System.out.print(label + ": ");
        return scanner.nextLine().trim();
    }

    private static void message(String text) {
        System.out.println("\n" + text);
    }

    private static void pause() {
        System.out.print("\nPress ENTER to continue...");
        scanner.nextLine();
    }

    private static String trim(String text, int width) {
        if (text == null) return "";
        return text.length() <= width ? text : text.substring(0, Math.max(0, width - 3)) + "...";
    }
}
