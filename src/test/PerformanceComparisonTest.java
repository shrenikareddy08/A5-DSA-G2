package test;

import execution.PerformanceComparison;
import model.Task;

public class PerformanceComparisonTest {

    public static void main(String[] args) {

        Task t1 =
                new Task(
                        "T01",
                        "Pattern Search",
                        "D01",
                        "KMP",
                        "HIGH",
                        2,
                        100,
                        3);

        Task t2 =
                new Task(
                        "T02",
                        "Document Similarity",
                        "D01",
                        "Edit Distance",
                        "MEDIUM",
                        2,
                        200,
                        4);

        Task t3 =
                new Task(
                        "T03",
                        "Keyword Search",
                        "D01",
                        "Rabin-Karp",
                        "HIGH",
                        1,
                        100,
                        2);

        Task[] tasks = {
                t1,
                t2,
                t3
        };

        PerformanceComparison comparison =
                new PerformanceComparison();

        comparison.compare(tasks);

        System.out.println(
                "\n==============================================");

        System.out.println(
                "     PERFORMANCE COMPARISON TEST COMPLETED");

        System.out.println(
                "==============================================");
    }
}