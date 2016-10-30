package achan.nl.uitstelgedrag.persistence.gateways;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import achan.nl.uitstelgedrag.domain.models.Dayplan;
import achan.nl.uitstelgedrag.domain.models.Task;
import achan.nl.uitstelgedrag.persistence.DayplanRepository;
import achan.nl.uitstelgedrag.persistence.UitstelgedragOpenHelper;

/**
 * Implementation of dayplanner returning generic dayplans.
 *
 * Created by Etienne on 17-8-2016.
 */
public class DayplanGateway implements DayplanRepository{

    Context context;
    SQLiteDatabase database;
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    public DayplanGateway(Context context) {
        this.context = context;
        database = new UitstelgedragOpenHelper(context, null).getWritableDatabase();
    }

    /**
     * Migration constructor - only use for onUpgrade!
     * @param database
     */
    public DayplanGateway(SQLiteDatabase database){
        this.database = database;
    }

    @Override
    public Dayplan get(Date datetime) {

        Dayplan result    = null;

        // Compare dates using date string.
        String  date      = formatter.format(datetime);

        Log.i("DayplanGateway", "Returning planning for today(" + date + ").");

        // Get the related dayplan, if available.
        Cursor cursor = database.query("Dayplans", null, "date = ?", new String[]{date}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {

            // Get dayplan - single result.
            long id = cursor.getLong(0);

            Date day = null;

            try { day = formatter.parse(cursor.getString(1));}
            catch (ParseException e) {e.printStackTrace();}

            // Query tasks for any with the current dayplan.
            Cursor subcursor = database.query("Tasks", null, "id = ?", new String[]{"" + id}, null, null, null);
            result = new Dayplan(day, new ArrayList<>());

            Log.i("DayplanGateway", "Result found with id " + id + ")");

            // Get associated tasks - multiple rows.
            if (subcursor != null && subcursor.moveToFirst()) {
                while (subcursor.moveToNext()) {
                    int    task_id          = subcursor.getInt(0);
                    String task_description = subcursor.getString(1);
                    //long    category_id      = subcursor.getLong(2);

                    Task   task             = new Task(task_description);
                    task.id = task_id;
                    task.description = task_description;
                    //task.category = new Category();
                    result.tasks.add(task);
                }
                subcursor.close();
            }
            cursor.close();
        }
        else
            Log.i("DayplanGateway","Nothing found.");

        return result;
    }

    @Override
    public Dayplan get(int id) {
        Log.e("DayplanGateway", "Returning null as get(id) isn't implemented.");
        return null;
    }

    @Override
    public List<Dayplan> getAll() {
        Log.e("DayplanGateway", "Returning null as getAll() isn't implemented.");
        return null;
    }

    @Override
    public Dayplan insert(Dayplan plan) {
        SQLiteStatement statement = database.compileStatement("INSERT INTO Dayplans(date) VALUES("+ formatter.format(plan.day) + ")");
        plan.id = statement.executeInsert();
        Log.i("DayplanGateway", "Insert successful.");
        return plan;
    }

    @Override
    public boolean delete(Dayplan plan) {
        int affected = database.delete("Dayplans", "date = ?", new String[]{formatter.format(plan.day)});
        Log.i("DayplanGateway", "Deletion successful.");
        return affected > 0;
    }

    @Override
    public void update(Dayplan plan) {
        Log.w("DayplanGateway", "There's nothing to update.");
//        ContentValues values = new ContentValues();
//        values.put("date", formatter.format(plan.day));
//        database.update("Dayplans", values, "date = ?", new String[]{formatter.format(plan.day)});
//        Log.i("DayplanGateway", "Update successful!");
    }
}
