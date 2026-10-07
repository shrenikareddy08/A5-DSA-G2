package test;

import scheduling.WorkloadAnalyzer;
import scheduling.WorkloadAnalyzer.WorkloadProfile;
import scheduling.SystemResourceDiscovery;
import scheduling.SystemResourceDiscovery.SystemProfile;

public class RealWorkloadTest {

    public static void main(String[] args) {

        System.out.println(
                "==============================================");

        System.out.println(
                "        REAL WORKLOAD DISCOVERY TEST");

        System.out.println(
                "==============================================");

        /*
         * STEP 1:
         * Discover actual machine resources.
         */
        SystemResourceDiscovery discovery =
                new SystemResourceDiscovery();

        SystemProfile system =
                discovery.discover();

        system.display();

        /*
         * STEP 2:
         * Analyze actual documents.
         */
        WorkloadAnalyzer analyzer =
                new WorkloadAnalyzer();

        System.out.println(
                "\n==============================================");

        System.out.println(
                "          DOCUMENT WORKLOAD ANALYSIS");

        System.out.println(
                "==============================================");

        WorkloadProfile d01 =
                analyzer.analyze("D01");

        if (d01 != null) {
            d01.display();
        }

        WorkloadProfile d30 =
                analyzer.analyze("30");

        if (d30 != null) {
            d30.display();
        }

        /*
         * STEP 3:
         * Analyze every .txt document.
         */
        analyzer.analyzeAllDocuments();

        System.out.println(
                "\n==============================================");

        System.out.println(
                "SUCCESS: REAL WORKLOAD DISCOVERY COMPLETED");

        System.out.println(
                "==============================================");
    }
}