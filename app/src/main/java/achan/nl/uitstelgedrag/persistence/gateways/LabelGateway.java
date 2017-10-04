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

    /**
     * Tries to find the given label by its title in the database.
     * Returns null if nothing was found, or the item if it was.
     * @param label the label to look for.
     */
    public Label find(Label label){
        List<Label> results = Labels.fromCursor(helper.query(Labels.TABLE, Labels.TITLE, label.title));
        return results.isEmpty()?
                null : results.get(0);
    }

    public List<Label> getAll() {
        Cursor cursor = database.rawQuery("SELECT * FROM " + Labels.TABLE, null);
        List<Label> labels = Labels.fromCursor(cursor);
        cursor.close();
        return labels;
    }

    public Label insert(Label label) {
        label.id = (int) database.insert(Labels.TABLE.name, null, Labels.toValues(label));
//        if (label.location != null && label.id > 0) todo - not implemented yet.
//            insertLocation(label, label.location);
        return label;
    }

    // fixme - unique constraint
    public void insertLocation(Label label, Location location){
        database.insert(Locations.TABLE.name, null, Locations.toValues(label, location));
    }

    public void update(Label label) {
        delete(label);
        insert(label);
    }

    public boolean delete(Label label) {
        database.delete(Labels.TABLE.name, Labels.ID + " = ?", new String[]{"" + label.id});
        return true; // fixme - int check instead.
    }

}
