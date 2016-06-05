package achan.nl.uitstelgedrag.ui.presenters;

import java.util.List;

import achan.nl.uitstelgedrag.domain.models.Task;

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
