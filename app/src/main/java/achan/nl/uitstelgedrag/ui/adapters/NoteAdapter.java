package achan.nl.uitstelgedrag.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import achan.nl.uitstelgedrag.R;
import achan.nl.uitstelgedrag.domain.models.Attachment;
import achan.nl.uitstelgedrag.domain.models.Note;
import achan.nl.uitstelgedrag.domain.models.Timestamp;
import achan.nl.uitstelgedrag.persistence.gateways.NoteGateway;
import achan.nl.uitstelgedrag.ui.activities.NoteDetailActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Etienne on 9-10-2016.
 */
public class NoteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEWTYPE_NOTE = 1;
    private static final int VIEWTYPE_RECORDING = 2;
    private static final int VIEWTYPE_PHOTO = 3;

    Context context;
    List<Note> notes;

    public NoteAdapter(List<Note> notes, Context context) {
        this.notes = notes;
        this.context = context;
    }

    public void addItem(final int position, Note note) {
        notes.add(position, note);
        Handler handler = new Handler(Looper.getMainLooper());  //  Fixes IllegalStateException: redraw while calculating layout
        handler.post(() -> {
            //structural events = whole list, item events = single items
            notifyItemInserted(position); // Exception - Inconsistency detected.
            //notifyDataSetChanged(); Stops animation and re-layout.
        });
    }

    public void removeItem(final int position) {
        notes.remove(position);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            notifyItemRemoved(position);
            //notifyDataSetChanged(); is obsolete because notifyItemRemoved specifies what changed.
            //                        Same goes for notifyItemInserted.
        });
    }

    @Override
    public int getItemViewType(int position) {
        Note note = notes.get(position);
        if (note.attachment == null)
            return VIEWTYPE_NOTE;
        else if (note.attachment.type == Attachment.ATTACHMENT_TYPE_AUDIO)
            return VIEWTYPE_RECORDING;
        else if (note.attachment.type == Attachment.ATTACHMENT_TYPE_PHOTO)
            return VIEWTYPE_PHOTO;
        else
            return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        View view;
        switch (viewType) {
            case VIEWTYPE_RECORDING:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rowlayout_note_recording, parent, false);
                holder = new NoteRecordingAttachmentViewHolder(view, context);
                break;
            case VIEWTYPE_PHOTO:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rowlayout_note_photo, parent, false);
                holder = new NotePhotoAttachmentViewHolder(view, context);
                break;
            case VIEWTYPE_NOTE:
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rowlayout_note, parent, false);
                holder = new NoteViewHolder(view, context);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Note note = notes.get(holder.getAdapterPosition());

        if (holder instanceof NoteViewHolder) {

            NoteViewHolder noteview = (NoteViewHolder)holder;
            noteview.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, NoteDetailActivity.class);
                intent.putExtra("note_id", note.id);
                context.startActivity(intent);
            });
            noteview.itemView.setOnLongClickListener(v ->
            {
                PopupMenu popup = new PopupMenu(context, v);
                popup.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.popup_edit:
                            Snackbar.make(v, "Nog niet beschikbaar :')", Snackbar.LENGTH_SHORT).show();
                            break;
                        case R.id.popup_delete:
                            removeItem(noteview.getAdapterPosition());
                            new NoteGateway(context).delete(note);
                            Snackbar.make(v, "Notitie verwijderd! ", Snackbar.LENGTH_SHORT).show();
                            break;
                    }

                    return true;
                });
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.notepopupmenu, popup.getMenu());
                popup.show();
                return true;
            });
            noteview.text.setText(note.text);
            noteview.timestamp.setText(Timestamp.formatDate(note.created));

        } else if(holder instanceof NoteRecordingAttachmentViewHolder){

            NoteRecordingAttachmentViewHolder recordingview = (NoteRecordingAttachmentViewHolder) holder;
            MediaPlayer player = MediaPlayer.create(recordingview.context, Uri.parse(note.attachment.path));
            recordingview.title.setText(note.text);
            recordingview.mediaButton.setOnClickListener(v -> {
                if (!player.isPlaying()) {
                    player.start();
                    v.setPressed(true);
                    Log.i("MediaPlayer", "Playing file: " + note.text + "(" + note.attachment.path + ")");
                }
                else {
                    player.stop();
                    v.setPressed(false);
                    Log.i("MediaPlayer", "Stopped playing file: " + note.text + "(" + note.attachment.path + ")");
                }
            });
            recordingview.itemView.setOnLongClickListener(v -> {

                PopupMenu popup = new PopupMenu(context, v);
                popup.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.popup_edit:
                            Snackbar.make(v, "Nog niet beschikbaar :')", Snackbar.LENGTH_SHORT).show();
                            break;
                        case R.id.popup_delete:
                            if (player != null && player.isPlaying())
                                player.stop();
                            removeItem(recordingview.getAdapterPosition());
                            new NoteGateway(context).delete(note);
                            Snackbar.make(v, "Geluidsfragment verwijderd! ", Snackbar.LENGTH_SHORT).show();
                            break;
                    }

                    return true;
                });
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.notepopupmenu, popup.getMenu());
                popup.show();
                return true;
            });

        } else if (holder instanceof NotePhotoAttachmentViewHolder){

            NotePhotoAttachmentViewHolder photoview = ((NotePhotoAttachmentViewHolder) holder);
            photoview.title.setText("Foto gemaakt op " + Timestamp.formatDate(note.created));
            Bitmap mImageBitmap = null;
            try {
                mImageBitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(note.attachment.path));
                photoview.photo.setImageBitmap(mImageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
//            photoview.photo.setImageURI(Uri.parse(note.attachment.path)); // FIXME: 8-11-2016 UI thread.
            photoview.itemView.setOnLongClickListener(v -> {

                PopupMenu popup = new PopupMenu(context, v);
                popup.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.popup_edit:
                            Snackbar.make(v, "Nog niet beschikbaar :')", Snackbar.LENGTH_SHORT).show();
                            break;
                        case R.id.popup_delete:
                            removeItem(photoview.getAdapterPosition());
                            new NoteGateway(context).delete(note);
                            Snackbar.make(v, "Foto verwijderd! ", Snackbar.LENGTH_SHORT).show();
                            break;
                    }

                    return true;
                });
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.notepopupmenu, popup.getMenu());
                popup.show();
                return true;
            });

        } else{
            Log.e("NoteAdapter", "Undefined ViewHolder type");
            throw new RuntimeException("ViewHolder undefined!");
        }
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.rowlayout_notes_timestamp) TextView timestamp;
        @BindView(R.id.rowlayout_notes_text) TextView text;

        Context context;

        public NoteViewHolder(View itemView, Context context) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.context = context;
        }
    }

    public class NoteRecordingAttachmentViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.rowlayout_note_recording_mediabutton)
        ImageButton mediaButton;
        @BindView(R.id.rowlayout_note_recording_title)
        TextView title;

        Context context;

        public NoteRecordingAttachmentViewHolder(View itemView, Context context) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.context = context;
        }
    }

    public class NotePhotoAttachmentViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.rowlayout_note_photo_title) TextView  title;
        @BindView(R.id.rowlayout_note_photo_image) ImageView photo;

        Context context;

        public NotePhotoAttachmentViewHolder(View itemView, Context context) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.context = context;
        }
    }
}
