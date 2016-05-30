package achan.nl.uitstelgedrag.logic;

import android.content.Context;
import android.util.Log;

import java.util.List;

import achan.nl.uitstelgedrag.models.Task;
import achan.nl.uitstelgedrag.persistence.UitstelgedragOpenHelper;

/**
 * Created by Etienne on 29-5-2016.
 */
public class TaskPresenterImpl implements TaskPresenter {

    UitstelgedragOpenHelper database;
    Context context;

    List<Task> tasks;

    public TaskPresenterImpl(Context context) {
        this.context = context;
        database = new UitstelgedragOpenHelper(context, null);
    }

    @Override
    public Task addTask(Task task) {
        Log.i("TaskPresenter", "Added task!");
        return database.tasks.insert(task);
    }

    @Override
    public Task deleteTask(Task task) {
        Log.i("TaskPresenter", "Deleted task!");
        database.tasks.delete(task.id);
        return task;
    }

    @Override
    public Task editTask(Task task) {
        Log.i("TaskPresenter", "Edited task!");
        database.tasks.update(task);
        return null;
    }

    @Override
    public List<Task> viewTasks() {
        return database.tasks.getAll();
    }

    @Override
    public Task viewTask(Task task) {
        Log.i("TaskPresenter", "Showing task!");
        return database.tasks.get(task.id);
    }
}
