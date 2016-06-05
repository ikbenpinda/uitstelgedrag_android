package achan.nl.uitstelgedrag.ui.presenters;

import android.content.Context;
import android.util.Log;

import java.util.List;

import achan.nl.uitstelgedrag.domain.models.Task;
import achan.nl.uitstelgedrag.persistence.TaskRepository;
import achan.nl.uitstelgedrag.persistence.gateways.TaskGateway;

/**
 * Created by Etienne on 29-5-2016.
 */
public class TaskPresenterImpl implements TaskPresenter {

    // TODO: 30-5-2016 Error handling.

    TaskRepository database;
    Context        context;

    public TaskPresenterImpl(Context context) {
        this.context = context;
        this.database = new TaskGateway(context);
    }

    @Override
    public Task addTask(Task task) {
        Log.i("TaskPresenter", "Added task!");
        return database.insert(task);
    }

    @Override
    public Task deleteTask(Task task) {
        Log.i("TaskPresenter", "Deleted task!");
        database.delete(task);
        return task;
    }

    @Override
    public Task editTask(Task task) {
        Log.i("TaskPresenter", "Edited task!");
        return database.update(task);
    }

    @Override
    public List<Task> viewTasks() {
        return database.getAll();
    }

    @Override
    public Task viewTask(Task task) {
        Log.i("TaskPresenter", "Showing task!");
        return database.get(task.id);
    }
}
