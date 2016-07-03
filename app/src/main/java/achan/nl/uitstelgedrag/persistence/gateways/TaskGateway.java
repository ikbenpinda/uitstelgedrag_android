package achan.nl.uitstelgedrag.persistence.gateways;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import achan.nl.uitstelgedrag.domain.models.Task;
import achan.nl.uitstelgedrag.persistence.TaskRepository;
import achan.nl.uitstelgedrag.persistence.UitstelgedragOpenHelper;
import achan.nl.uitstelgedrag.persistence.definitions.tables.TaskDefinition;

/**
 * Created by Etienne on 17-4-2016.
 */
public class TaskGateway implements TaskRepository {

    SQLiteDatabase database;

    public TaskGateway(Context context) {
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
        int col = 0;
        Task task = new Task("");
        String query = "SELECT * FROM " + TaskDefinition.TASKS.name + " WHERE " + TaskDefinition.ID.name + " = " + id;
        Cursor cursor = database.rawQuery(query, null);
        if(cursor != null && cursor.moveToFirst()) { // Fixes issue where doubleclicking the view would cause the database to get called with a nulled object.
            task.id = cursor.getInt(col++);
            task.description = cursor.getString(col++);
            cursor.close();
            return task;
        }
        Log.w("TaskGateway", "Returned null - cursor was null or moveToFirst returned false!");
        return null;
    }

    @Override
    public List<Task> getAll() {
        Cursor     cursor = database.rawQuery("SELECT * FROM Tasks", null);
        List<Task> tasks  = new ArrayList<>();

        // Cursor starts at -1,
        // first call will make it start at first value of set.
        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String description = cursor.getString(1);
            //int category_id = cursor.getInt(2);
            Task task = new Task();
            task.id = id;
            task.description = description;
            tasks.add(task); // TODO: 11-4-2016 category support, etc.
            Log.i("UITSTELGEDRAG", "OpenHelper.getAll(): cursorPosition:" + cursor.getPosition() + ", Added task: " + id + ".");
        }
        cursor.close(); // FIXME: 22-4-2016 Try-catch-finally.
        return tasks;
    }

    @Override
    public Task insert(Task object) {
        String query = "INSERT INTO Tasks(" + TaskDefinition.DESCRIPTION.name + "," + TaskDefinition.CATEGORY_ID.name + ") VALUES(? ,0)";
        SQLiteStatement statement = database.compileStatement(query);
        statement.bindString(1, object.description);
     // Returns the SQLite-generated primary key generated after the insert.
        object.id = (int)statement.executeInsert();
        Log.i("UITSTELGEDRAG", "Added task " + object.id + ":" + object.description);
        return object;
    }

    @Override
    public Task update(Task row) {
        Log.w("UITSTELGEDRAG", "Called update() but should've been calling insert()!");
        return null;
        //UPDATE table_name
        //SET column1 = value1, column2 = value2...., columnN = valueN
        //WHERE [condition];
    }

    @Override
    public boolean delete(Task task) {

        if (task == null)
            return false;

        String query = "DELETE FROM " + TaskDefinition.TASKS.name + " WHERE " + TaskDefinition.ID.name + " = " + task.id + "";
        database.execSQL(query);
        Log.i("UITSTELGEDRAG", "Deleted task " + task.id + ":" + task.description);
        return true;
    }

}
