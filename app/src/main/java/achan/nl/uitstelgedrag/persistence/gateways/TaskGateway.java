package achan.nl.uitstelgedrag.persistence.gateways;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import achan.nl.uitstelgedrag.domain.models.Label;
import achan.nl.uitstelgedrag.domain.models.Task;
import achan.nl.uitstelgedrag.persistence.Repository;
import achan.nl.uitstelgedrag.persistence.UitstelgedragOpenHelper;
import achan.nl.uitstelgedrag.persistence.definitions.tables.Labels;
import achan.nl.uitstelgedrag.persistence.definitions.tables.Locations;
import achan.nl.uitstelgedrag.persistence.definitions.tables.Tasks;

/**
 * Created by Etienne on 17-4-2016.
 */
public class TaskGateway implements Repository<Task> {

    Context context;
    SQLiteDatabase database;
    List<Task> cache;

    public TaskGateway(Context context) {
        this.context = context;
        this.database = new UitstelgedragOpenHelper(context, null).getWritableDatabase();
    }

    /**
     *
     * @return the row id of the last insert.
     */
    public int getLastRow_id(){
        String getKey = "SELECT last_insert_rowid()";
        Cursor cursor = database.rawQuery(getKey, null);
        cursor.moveToFirst();
        int id = cursor.getInt(0);
        cursor.close();
        return id;
    }

    @Override
    public Task get(int id) {
        Task task = null;
        String taskQuery = "SELECT * FROM " + Tasks.TABLE + " WHERE " + Tasks.ID + " = " + id;
        Cursor cursor = database.rawQuery(taskQuery, null);
        if(cursor != null && cursor.moveToFirst()) { // Fixes issue where doubleclicking the view would cause the database to get called with a nulled object.
            task = Tasks.fromCursor(cursor);
            task.labels = getLabels(task);
        } // FIXME - why the logging? NPE sometimes happening here.
        Log.w("TaskGateway", "Returned null - cursor was null or moveToFirst returned false!");
        cursor.close();
        return task;
    }

    @Override
    public List<Task> getAll() {
        Cursor     cursor = database.rawQuery("SELECT * FROM " + Tasks.TABLE, null);
        List<Task> tasks  = new ArrayList<>();

        // Cursor starts at -1,
        // first call will make it start at first value of set.
//        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {
            Task task = Tasks.fromCursor(cursor);
            task.labels = getLabels(task);
            tasks.add(task); // TODO: 11-4-2016 category support
            Log.i("UITSTELGEDRAG", "OpenHelper.getAll(): cursorPosition:" + cursor.getPosition() + ", Added task: " + task.id + ".");
        }
        cursor.close();
        return tasks;
    }

    @Override
    public Task insert(Task task) {
        task.id = (int) database.insert(Tasks.TABLE.name, null, Tasks.toValues(task));
        if (!task.labels.isEmpty())
            for (Label label : task.labels) {
                label.id = (int) database.insert(Labels.TABLE.name, null, Labels.toValues(task, label));
                if (label.location != null)
                    database.insert(Locations.TABLE.name, null, Locations.toValues(label, label.location));
            }
        Log.i("UITSTELGEDRAG", "Added task " + task.id + ":" + task.description);
        return task;
    }

    @Override
    public void update(Task task) {
        database.update(Tasks.TABLE.name, Tasks.toValues(task), "id = ?", new String[]{"" + task.id});
        Log.i("UITSTELGEDRAG", "Updated task " + task.id + ":" + task.description);
    }

    @Override
    public boolean delete(Task task) {
        database.delete(Tasks.TABLE.name, "id = ?", new String[]{"" + task.id});
        Log.i("UITSTELGEDRAG", "Deleted task " + task.id + ":" + task.description);
        return true;
    }

    private List<Label> getLabels(Task task){
        Log.i("TaskGateway", "Querying labels for task " + task.id);
        String labelQuery = "SELECT * FROM " + Labels.TABLE + " WHERE " + Labels.TASK + " = " + task.id;
        Cursor labelCursor = database.rawQuery(labelQuery, null);

        List<Label> labels = new ArrayList<>();
        for (Label label : Labels.fromCursor(labelCursor)){
            String locationQuery = "SELECT * FROM " + Locations.TABLE + " WHERE " + Locations.LABEL_ID + " = " + label.id;
            Cursor locationCursor = database.rawQuery(locationQuery, null);
            label.location = Locations.fromCursor(locationCursor);
            Log.i("TaskGateway", "Location: lon:" + label.location.getLongitude() + " / lat:" + label.location.getLatitude());
            labels.add(label);
        }

        return labels;
    }

    /**
     * Filters tasks to the relevant location.
     */
    public static List<Task> filterByLocation(List<Task> tasks, Location current){
        List<Task> filteredTasks = new ArrayList<>();
        for (Task task : tasks) {
            for (Label label : task.labels) {
                if (isNearby(label.location, current))
                    filteredTasks.add(task);
            }
        }

        return filteredTasks;
    }

    /**
     * Returns whether the two locations are within 500 meters of each other.
     */
    public static boolean isNearby(Location l1, Location l2){

        int range = 500;
        double R = 6378.137; // Radius of earth in KM
        double dLat = l2.getLatitude() * Math.PI / 180 - l1.getLatitude() * Math.PI / 180;
        double dLon = l2.getLongitude() * Math.PI / 180 - l1.getLongitude() * Math.PI / 180;
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(l1.getLatitude() * Math.PI / 180) * Math.cos(l2.getLatitude() * Math.PI / 180) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = R * c;
        double distance = d * 1000; // meters

//        boolean latitudeIsNearby = l1.getLatitude() - l2.getLatitude() < range;
//        boolean longitudeIsNearby = l1.getLongitude() - l2.getLongitude() < range;
//        if (latitudeIsNearby && longitudeIsNearby)
        if (distance < range)
            return true;
        return false;
    }

    public static List<Task> sortByLocation(List<Task> tasks, Location location){
        Collections.sort(tasks, (task, t1) -> {
            for (Label l : task.labels) {
                if (l != null && l.location.equals(location))
                    return 1;
            }
            return -1;
        });
        return tasks;
    }

    /**
     * --
     */
    public static List<Task> sortByCreationDate(List<Task> tasks){
        Collections.sort(tasks, (task, t1) -> task.createdOn.equals(t1.createdOn)? 0: task.createdOn.before(t1.createdOn)? 1: -1);
        return tasks;
    }

    /**
     * --
     */
    public static List<Task> sortByPlanned(List<Task> tasks){
        Collections.sort(tasks, (task, t1) -> {
            if (task.deadline == null || t1.deadline == null)
                return -1;
            else
                return task.deadline.equals(t1.deadline) ? 0 : task.deadline.before(t1.deadline) ? 1 : -1;
        });
        return tasks;
    }

}
