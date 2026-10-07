package structures;

import model.Task;

public class CustomPriorityQueue {

    private Task[] tasks;
    private int size;

    public CustomPriorityQueue(int capacity) {

        tasks = new Task[capacity];
        size = 0;
    }

    public boolean isEmpty() {

        return size == 0;
    }

    public int size() {

        return size;
    }

    public void insert(Task task) {

        if (size == tasks.length) {

            System.out.println("Priority queue is full.");
            return;
        }

        tasks[size] = task;
        size++;

        arrange();
    }

    private void arrange() {

        for (int i = 0; i < size - 1; i++) {

            for (int j = 0; j < size - i - 1; j++) {

                if (tasks[j].getPriorityValue()
                        < tasks[j + 1].getPriorityValue()) {

                    Task temp = tasks[j];

                    tasks[j] = tasks[j + 1];

                    tasks[j + 1] = temp;
                }
            }
        }
    }

    public Task removeHighestPriority() {

        if (isEmpty()) {

            return null;
        }

        Task selected = tasks[0];

        for (int i = 0; i < size - 1; i++) {

            tasks[i] = tasks[i + 1];
        }

        tasks[size - 1] = null;

        size--;

        return selected;
    }

    public Task peek() {

        if (isEmpty()) {

            return null;
        }

        return tasks[0];
    }

    public void displayQueue() {

        System.out.println("\n==============================================");
        System.out.println("             PRIORITY QUEUE");
        System.out.println("==============================================");

        if (isEmpty()) {

            System.out.println("Queue is empty.");

        } else {

            for (int i = 0; i < size; i++) {

                System.out.println(
                        (i + 1)
                        + ". "
                        + tasks[i].getTaskId()
                        + " - "
                        + tasks[i].getTaskName()
                        + " - "
                        + tasks[i].getPriority());
            }
        }

        System.out.println("==============================================");
    }
}