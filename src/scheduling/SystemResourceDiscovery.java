package scheduling;

public class SystemResourceDiscovery {

    public static class SystemProfile {

        private int availableCpu;
        private long availableMemoryMB;
        private long totalMemoryMB;

        public SystemProfile(
                int availableCpu,
                long availableMemoryMB,
                long totalMemoryMB) {

            this.availableCpu = availableCpu;
            this.availableMemoryMB =
                    availableMemoryMB;

            this.totalMemoryMB =
                    totalMemoryMB;
        }

        public int getAvailableCpu() {
            return availableCpu;
        }

        public long getAvailableMemoryMB() {
            return availableMemoryMB;
        }

        public long getTotalMemoryMB() {
            return totalMemoryMB;
        }

        public void display() {

            System.out.println(
                    "\n==============================================");

            System.out.println(
                    "          ACTUAL SYSTEM RESOURCES");

            System.out.println(
                    "==============================================");

            System.out.println(
                    "Available CPU Cores : "
                    + availableCpu);

            System.out.println(
                    "Available Memory    : "
                    + availableMemoryMB
                    + " MB");

            System.out.println(
                    "Total Memory        : "
                    + totalMemoryMB
                    + " MB");

            System.out.println(
                    "==============================================");
        }
    }

    public SystemProfile discover() {

        Runtime runtime =
                Runtime.getRuntime();

        int processors =
                runtime.availableProcessors();

        long totalMemory =
                runtime.maxMemory()
                / (1024 * 1024);

        long freeMemory =
                runtime.freeMemory()
                / (1024 * 1024);

        if (processors < 1) {
            processors = 1;
        }

        if (totalMemory < 256) {
            totalMemory = 256;
        }

        if (freeMemory < 128) {
            freeMemory = 128;
        }

        return new SystemProfile(
                processors,
                freeMemory,
                totalMemory);
    }

    public void displaySystemResources() {

        SystemProfile profile =
                discover();

        profile.display();
    }
}