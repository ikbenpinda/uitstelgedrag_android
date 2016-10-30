package achan.nl.uitstelgedrag.persistence.definitions.tables;

import android.content.ContentValues;
import android.database.Cursor;

import achan.nl.uitstelgedrag.domain.models.Note;
import achan.nl.uitstelgedrag.domain.models.Timestamp;
import achan.nl.uitstelgedrag.persistence.definitions.Column;
import achan.nl.uitstelgedrag.persistence.definitions.Constraints;
import achan.nl.uitstelgedrag.persistence.definitions.Table;
import achan.nl.uitstelgedrag.persistence.definitions.Types;

/**
 * Created by Etienne on 9-10-2016.
 */
public class Notes {

    public static final int DEFAULT_ID_VALUE = 0;

    public static final Column ID = new Column("id", Types.INTEGER, Constraints.PRIMARY_KEY_AUTOINCREMENT);
    public static final Column TEXT = new Column("text", Types.TEXT, Constraints.NOT_NULL);
    public static final Column CREATED_ON = new Column("created_on", Types.TEXT, Constraints.NOT_NULL);
    public static final Table  TABLE = new Table("Notes", ID, TEXT, CREATED_ON);

    /**
     * Fills a ContentValues object with the given model.
     * @param note
     * @return
     */
    public static ContentValues toValues(Note note){
        ContentValues values = new ContentValues();

        if (note.id > DEFAULT_ID_VALUE)
            values.put(ID.name, note.id);

        values.put(TEXT.name, note.text);
        values.put(CREATED_ON.name, Timestamp.formatDate(note.created));

        return values;
    }

    /**
     * Fills the object from the cursor.
     *
     * @param cursor
     * @return
     */
    public static Note fromCursor(Cursor cursor){
        Note note = new Note();

        note.id = cursor.getInt(cursor.getColumnIndexOrThrow(ID.name));
        note.text = cursor.getString(cursor.getColumnIndexOrThrow(TEXT.name));
        note.created = Timestamp.formatDate(cursor.getString(cursor.getColumnIndexOrThrow(CREATED_ON.name)));

        return note;
    }
}
