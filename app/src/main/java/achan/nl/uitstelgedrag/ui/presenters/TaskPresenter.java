package achan.nl.uitstelgedrag.ui.presenters;

import android.location.Location;

import java.util.List;

import achan.nl.uitstelgedrag.domain.models.Task;

/**
 * Created by Etienne on 29-5-2016.
 */
public interface TaskPresenter {

    // -- Tasks. --------------------------------------------------
    Task addTask(Task task);
    Task deleteTask(Task task);
    Task editTask(Task task);
    List<Task> viewTasks();
    List<Task> filterTasks(List<Task> tasks, Location location);
    Task viewTask(Task task);

    // -- Location Services. --------------------------------------
    Location getCurrentLocation();
    Location geocode(String address);
    String reverseGeocode(Location location);

    // -- View Handling. --------------------------------------------------
    // ...belongs to a view interface - N/A
}
