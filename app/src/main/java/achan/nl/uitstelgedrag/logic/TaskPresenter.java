package achan.nl.uitstelgedrag.logic;

import java.util.List;

import achan.nl.uitstelgedrag.models.Task;

/**
 * Created by Etienne on 29-5-2016.
 */
public interface TaskPresenter {

    Task addTask(Task task);
    Task deleteTask(Task task);
    Task editTask(Task task);
    List<Task> viewTasks();
    Task viewTask(Task task);
}
