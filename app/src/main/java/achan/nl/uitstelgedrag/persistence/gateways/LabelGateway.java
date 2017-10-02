package achan.nl.uitstelgedrag.persistence.gateways;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;

import java.util.List;

import achan.nl.uitstelgedrag.domain.models.Label;
import achan.nl.uitstelgedrag.domain.models.Task;
import achan.nl.uitstelgedrag.persistence.UitstelgedragOpenHelper;
import achan.nl.uitstelgedrag.persistence.definitions.tables.Labels;
import achan.nl.uitstelgedrag.persistence.definitions.tables.Locations;

/**
 * Created by Etienne on 17/01/17.
 */
public class LabelGateway {

    Context context;
    UitstelgedragOpenHelper helper;
    SQLiteDatabase database;

    public LabelGateway(Context context) {
        this.context = context;
        this.helper = new UitstelgedragOpenHelper(context, null);
        this.database = helper.getWritableDatabase();
    }

    public Label get(int id) {
        return Labels.fromCursor(helper.query(Labels.TABLE, Labels.ID, "" + id)).get(0);
    }

    public List<Label> getAll() {
        Cursor cursor = database.rawQuery("SELECT * FROM " + Labels.TABLE, null);
        List<Label> labels = Labels.fromCursor(cursor);
        cursor.close();
        return labels;
    }

    public Label insert(Task task, Label label) {
        label.id = (int) database.insert(Labels.TABLE.name, null, Labels.toValues(task, label));
        if (label.location != null && label.id > 0)
            insertLocation(label, label.location);
        return label;
    }

    // fixme - unique constraint
    public void insertLocation(Label label, Location location){
        database.insert(Locations.TABLE.name, null, Locations.toValues(label, location));
    }

    public void update(Task task, Label label) {
        delete(label);
        insert(task, label);
    }

    public boolean delete(Label label) {
        database.delete(Labels.TABLE.name, Labels.ID + " = ?", new String[]{"" + label.id});
        return true; // fixme - int check instead.
    }

}
