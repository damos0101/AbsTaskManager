package moskvin.controllers;

import moskvin.models.Person;
import moskvin.models.PersonTasks;
import moskvin.models.Task;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Optimization {
    public static void optimizeTaskDistribution(List<PersonTasks> people, List<Task> tasks) {
        Collections.sort(tasks, Comparator.comparingDouble(Task::getPeriod).reversed());
        int peopleCount = people.size();
        for (Task task : tasks) {
            PersonTasks minPerson = Collections.min(people, Comparator.comparingDouble(PersonTasks::getTotalDuration));
            minPerson.addTask(task);
        }
        double averageDuration = tasks.stream().mapToDouble(Task::getPeriod).sum() / peopleCount;
        for (PersonTasks personTasks : people) {
            personTasks.setAllocatedDuration(averageDuration);
        }
    }

}
