package achan.nl.uitstelgedrag.persistence.gateways;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import achan.nl.uitstelgedrag.domain.models.Label;
import achan.nl.uitstelgedrag.domain.models.Task;
import achan.nl.uitstelgedrag.persistence.Repository;
import achan.nl.uitstelgedrag.persistence.UitstelgedragOpenHelper;
import achan.nl.uitstelgedrag.persistence.definitions.tables.Labels;
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
        return Labels.fromCursor(labelCursor);
    }
}
