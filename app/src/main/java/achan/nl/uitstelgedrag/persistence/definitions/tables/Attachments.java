package achan.nl.uitstelgedrag.persistence.definitions.tables;

import android.content.ContentValues;
import android.database.Cursor;

import achan.nl.uitstelgedrag.domain.models.Attachment;
import achan.nl.uitstelgedrag.domain.models.Note;
import achan.nl.uitstelgedrag.persistence.definitions.Column;
import achan.nl.uitstelgedrag.persistence.definitions.Constraints;
import achan.nl.uitstelgedrag.persistence.definitions.Table;
import achan.nl.uitstelgedrag.persistence.definitions.Types;

/**
 *
 *
 * Created by Etienne on 25-10-2016.
 */
public class Attachments{

    public static final int DEFAULT_ID_VALUE = 0;

    public static final Column ID         = new Column("id",        Types.INTEGER,  Constraints.PRIMARY_KEY_AUTOINCREMENT);
    public static final Column PATH       = new Column("path",      Types.TEXT,     Constraints.NOT_NULL);
    public static final Column TYPE       = new Column("type",      Types.TEXT,     Constraints.NOT_NULL);
    public static final Column NOTE       = new Column("note_id",   Types.TEXT,     Constraints.FOREIGN_KEY_REFERENCES(Notes.TABLE, Notes.ID));
    public static final Table  TABLE      = new Table("attachments", ID, PATH, TYPE, NOTE);

    /**
     * Fills a ContentValues object with the given model.
     * @param note to model the relationship, provide the attached note.
     * @param attachment
     * @return
     */
    public static ContentValues toValues(Note note, Attachment attachment){
        ContentValues values = new ContentValues();

        if (attachment.id > DEFAULT_ID_VALUE)
            values.put(ID.name, attachment.id);
        values.put(PATH.name, attachment.path);
        values.put(TYPE.name, attachment.type);
        values.put(NOTE.name, note.id);

        return values;
    }

    /**
     * Fills the object from the cursor.
     *
     * @param cursor
     * @return
     */
    public static Attachment fromCursor(Cursor cursor){

        if (cursor.getCount() == 0)
            return null;

        Attachment attachment = new Attachment();

        attachment.id = cursor.getInt(cursor.getColumnIndexOrThrow(ID.name));
        attachment.path = cursor.getString(cursor.getColumnIndexOrThrow(PATH.name));
        attachment.type = cursor.getString(cursor.getColumnIndexOrThrow(TYPE.name));

        return attachment;
    }
}
