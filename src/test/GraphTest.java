package test;

import structures.Graph;

public class GraphTest {

    public static void main(String[] args) {

        Graph graph = new Graph(10);

        graph.addTask("T01");
        graph.addTask("T02");
        graph.addTask("T03");
        graph.addTask("T04");
        graph.addTask("T05");
        graph.addTask("T06");

        graph.addDependency("T01", "T04");
        graph.addDependency("T02", "T04");
        graph.addDependency("T03", "T05");
        graph.addDependency("T04", "T06");
        graph.addDependency("T05", "T06");

        System.out.println("\n");
        System.out.println("==============================================");
        System.out.println("       INTELLIGENT DEPENDENCY GRAPH TEST");
        System.out.println("==============================================");

        graph.displayGraph();

        System.out.println("\nCycle detected: "
                + graph.hasCycle());

        System.out.println(
                "\nTopological execution order:");

        String[] order = graph.topologicalSort();

        if (order != null) {

            for (int i = 0; i < order.length; i++) {

                System.out.println(
                        (i + 1) + ". " + order[i]);
            }
        }

        System.out.println("\n==============================================");
        System.out.println("              TEST COMPLETED");
        System.out.println("==============================================");
    }
}