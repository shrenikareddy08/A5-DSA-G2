package structures;

import model.Task;

public class CustomPriorityQueue {
    private final Task[] data;
    private int size;

    public CustomPriorityQueue(int capacity) {
        data = new Task[capacity];
    }

    public boolean isEmpty() { return size == 0; }

    public void add(Task task) {
        if (size == data.length) return;
        data[size++] = task;
        for (int i = size - 1; i > 0; i--) {
            if (data[i].getPriority() > data[i - 1].getPriority()) {
                Task temp = data[i];
                data[i] = data[i - 1];
                data[i - 1] = temp;
            }
        }
    }

    public Task remove() {
        if (size == 0) return null;
        Task result = data[0];
        for (int i = 1; i < size; i++) data[i - 1] = data[i];
        data[--size] = null;
        return result;
    }
}
