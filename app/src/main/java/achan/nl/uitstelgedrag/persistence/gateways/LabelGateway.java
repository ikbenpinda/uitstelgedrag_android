package achan.nl.uitstelgedrag.persistence.gateways;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.List;

import achan.nl.uitstelgedrag.domain.models.Label;
import achan.nl.uitstelgedrag.domain.models.Location;
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
        List<Label> results = Labels.fromCursor(helper.query(Labels.TABLE, Labels.ID, "" + id));
        if (results.isEmpty())
            return null;

        Label result = results.get(0);
        result.location = Locations.fromCursor(helper.query(Locations.TABLE, Locations.LABEL_ID, "" + id));
        return result;
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
        for (Label label : labels) {
            Cursor subcursor = database.rawQuery("SELECT * FROM " + Locations.TABLE + " WHERE " + Locations.LABEL_ID + " = ?", new String[]{"" + label.id});
            label.location = Locations.fromCursor(subcursor);
            subcursor.close();
        }
        cursor.close();
        return labels;
    }

    public Label insert(Label label) {
        label.id = (int) database.insert(Labels.TABLE.name, null, Labels.toValues(label));
        if (label.location != null)
            insertLocation(label, label.location);
        return label;
    }

    public void insertLocation(Label label, Location location){
        Log.i("LabelGateway", "Persisting location for label.");
        database.insert(Locations.TABLE.name, null, Locations.toValues(label, location));
    }

    public void update(Label label) {
        Log.i("LabelGateway", "Updating label.");
        database.update(Labels.TABLE.name, Labels.toValues(label), Labels.ID.name + " = ?", new String[]{""+label.id});
        if (label.location != null) {
            database.delete(Locations.TABLE.name, Locations.LABEL_ID + " = ?", new String[]{"" + label.id});
            database.insert(Locations.TABLE.name, null, Locations.toValues(label, label.location)/*, Locations.LABEL_ID.name + " = ?", new String[]{""+label.id}*/);// FIXME: 27-10-2017 insert/update location fails
        }
    }

    public boolean delete(Label label) {
        return database.delete(Labels.TABLE.name, Labels.ID + " = ?", new String[]{"" + label.id}) > 0;
    }

}
