package scheduling;

import model.Task;
import model.Resource;

public class ParallelScheduler {

    private Task[] tasks;
    private Resource[] resources;

    private int taskCount;
    private int resourceCount;

    public ParallelScheduler(int maxTasks, int maxResources) {

        tasks = new Task[maxTasks];
        resources = new Resource[maxResources];

        taskCount = 0;
        resourceCount = 0;
    }

    public void addTask(Task task) {

        tasks[taskCount] = task;
        taskCount++;
    }

    public void addResource(Resource resource) {

        resources[resourceCount] = resource;
        resourceCount++;
    }

    public void displayParallelAssignments() {

        System.out.println("\n==============================================");
        System.out.println("        PARALLEL RESOURCE ASSIGNMENT");
        System.out.println("==============================================");

        int assignedTasks = 0;

        for (int i = 0; i < taskCount; i++) {

            Task task = tasks[i];

            if (!task.getStatus().equals("WAITING")) {
                continue;
            }

            for (int j = 0; j < resourceCount; j++) {

                Resource resource = resources[j];

                if (!resource.isBusy() && resource.canRunTask(task)) {

                    resource.allocate(task);

                    task.setStatus("RUNNING");

                    System.out.println(
                            task.getTaskId()
                            + " -> "
                            + resource.getResourceId()
                            + " -> RUNNING");

                    assignedTasks++;

                    break;
                }
            }
        }

        System.out.println("----------------------------------------------");

        System.out.println(
                "Tasks assigned in parallel: "
                + assignedTasks);

        System.out.println("==============================================");
    }
}