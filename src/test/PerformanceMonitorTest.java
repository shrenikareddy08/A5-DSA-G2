package test;

import execution.PerformanceMonitor;

public class PerformanceMonitorTest {

    public static void main(String[] args) {

        PerformanceMonitor monitor =
                new PerformanceMonitor();

        monitor.setTaskStatistics(
                4,
                4,
                0,
                0
        );

        monitor.startMonitoring();

        try {

            Thread.sleep(3000);

        } catch (InterruptedException e) {

            System.out.println(
                    "Monitoring interrupted.");
        }

        monitor.stopMonitoring();

        monitor.displayPerformance();

        System.out.println(
                "\n==============================================");

        System.out.println(
                "       PERFORMANCE TEST COMPLETED");

        System.out.println(
                "==============================================");
    }
}