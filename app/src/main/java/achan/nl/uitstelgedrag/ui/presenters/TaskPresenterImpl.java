package achan.nl.uitstelgedrag.ui.presenters;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import java.util.List;

import achan.nl.uitstelgedrag.domain.models.Task;
import achan.nl.uitstelgedrag.persistence.Repository;
import achan.nl.uitstelgedrag.persistence.gateways.LabelGateway;
import achan.nl.uitstelgedrag.persistence.gateways.TaskGateway;

/**
 * Created by Etienne on 29-5-2016.
 */
public class TaskPresenterImpl implements TaskPresenter {

    // Note - there is no gateway for labels because they
    // are but extra data/part of to other entities.
    TaskGateway database;
    Context    context;

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
        database.update(task);
        return task;
    }

    @Override
    public List<Task> viewTasks() {
        return TaskGateway.sortByCreationDate(database.getAll());
    }

    @Override
    public List<Task> filterTasks(List<Task> tasks, Location location){
        return TaskGateway.filterByLocation(tasks, location);
    }

    @Override
    public Task viewTask(Task task) {
        Log.i("TaskPresenter", "Showing task!");
        return database.get(task.id);
    }

    @Override
    public Location getCurrentLocation() {
        return null;
    }

    @Override
    public Location geocode(String address) {
        return null;
    }

    @Override
    public String reverseGeocode(Location location) {
        return null;
    }
}
