package test;

import model.Task;
import model.Resource;
import scheduling.Scheduler;

public class SchedulerTest {

    public static void main(String[] args) {

        Scheduler scheduler =
                new Scheduler(10, 5);

        Task t1 = new Task(
                "T01",
                "Pattern Search",
                "D01",
                "KMP",
                "HIGH",
                2,
                100,
                2
        );

        Task t2 = new Task(
                "T02",
                "Document Similarity",
                "D01",
                "Edit Distance",
                "MEDIUM",
                2,
                200,
                2
        );

        Task t3 = new Task(
                "T03",
                "Keyword Search",
                "D01",
                "Rabin-Karp",
                "HIGH",
                1,
                100,
                2
        );

        Task t4 = new Task(
                "T04",
                "Text Indexing",
                "D01",
                "Suffix Array",
                "HIGH",
                3,
                300,
                2
        );

        /*
         * T02 will fail permanently.
         */

        t2.setPermanentFailure(true);

        scheduler.addTask(t1);
        scheduler.addTask(t2);
        scheduler.addTask(t3);
        scheduler.addTask(t4);

        Resource r1 = new Resource(
                "R01",
                "Text Processing Worker 1",
                4,
                2000
        );

        Resource r2 = new Resource(
                "R02",
                "Text Processing Worker 2",
                2,
                1000
        );

        Resource r3 = new Resource(
                "R03",
                "Analysis Worker",
                4,
                4000
        );

        scheduler.addResource(r1);
        scheduler.addResource(r2);
        scheduler.addResource(r3);

        /*
         * Dependencies:
         *
         * T01 ──┐
         * T02 ──┼──> T04
         * T03 ──┘
         */

        scheduler.addDependency(
                "T01",
                "T04");

        scheduler.addDependency(
                "T02",
                "T04");

        scheduler.addDependency(
                "T03",
                "T04");

        scheduler.displayTasks();

        scheduler.displayResources();

        scheduler.generateSchedule();

        System.out.println(
                "\n==============================================");

        System.out.println(
                "              TEST COMPLETED");

        System.out.println(
                "==============================================");
    }
}