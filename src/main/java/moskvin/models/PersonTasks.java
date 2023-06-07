package moskvin.models;

import java.util.ArrayList;
import java.util.List;

public class PersonTasks {
    private int id;
    private String name;
    private List<Task> tasks;
    private double totalDuration;
    private double allocatedDuration;

    public PersonTasks(String name, int id) {
        this.name = name;
        this.id=id;
        this.tasks = new ArrayList<>();
        this.totalDuration = 0.0;
        this.allocatedDuration = 0.0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public double getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(double totalDuration) {
        this.totalDuration = totalDuration;
    }

    public double getAllocatedDuration() {
        return allocatedDuration;
    }

    public void setAllocatedDuration(double allocatedDuration) {
        this.allocatedDuration = allocatedDuration;
    }

    public void addTask(Task task) {
        tasks.add(task);
        totalDuration += task.getPeriod();
    }

    public int getId() {
        return id;
    }
}
