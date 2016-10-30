package achan.nl.uitstelgedrag.persistence.gateways;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import achan.nl.uitstelgedrag.domain.models.Note;
import achan.nl.uitstelgedrag.persistence.Repository;
import achan.nl.uitstelgedrag.persistence.UitstelgedragOpenHelper;
import achan.nl.uitstelgedrag.persistence.definitions.tables.Attachments;
import achan.nl.uitstelgedrag.persistence.definitions.tables.Notes;

/**
 * Created by Etienne on 10-10-2016.
 */
public class NoteGateway implements Repository<Note> {

    Context        context;
    SQLiteDatabase database;

    public NoteGateway(Context context) {
        this.context = context;
        this.database = new UitstelgedragOpenHelper(context, null).getWritableDatabase();
    }

    @Override
    public Note get(int id) {
        Note note = new Note();

        Cursor notesCursor = database.query(
                Notes.TABLE.name,
                null,
                Notes.ID + " = ?",
                new String[]{"" + id}, null, null, null);

        Cursor attachmentsCursor = database.query(
                Attachments.TABLE.name,
                null,
                Attachments.NOTE + " = ?",
                new String[]{""+id}, null, null, null);

        while(notesCursor.moveToNext()){
            note = Notes.fromCursor(notesCursor);
            note.attachment = Attachments.fromCursor(attachmentsCursor); // returns null or the attachment.
        }

        return note;
    }

    @Override
    public List<Note> getAll() {
        Cursor notesCursor = database.query(
                Notes.TABLE.name,
                null, null, null, null, null, null);

        List<Note> notes = new ArrayList<>();

        while(notesCursor.moveToNext()){ // FIXME efficiency
            Note note = Notes.fromCursor(notesCursor);

            Cursor attachmentsCursor = database.query(
                    Attachments.TABLE.name,
                    null,
                    Attachments.NOTE + " = ?",
                    new String[]{""+note.id}, null, null, null);

            note.attachment = Attachments.fromCursor(attachmentsCursor); // returns null or the attachment.

            notes.add(note);
        }

        return notes;
    }

    @Override
    public Note insert(Note note) {
        note.id = (int)database.insert(Notes.TABLE.name, null, Notes.toValues(note));
        return note;
    }

    @Override
    public boolean delete(Note note) {
        database.delete(Notes.TABLE.name, Notes.ID + " = ?", new String[]{"" + note.id});
        return true;
    }

    @Override
    public void update(Note note) {
        delete(note);
        insert(note);
    }
}
