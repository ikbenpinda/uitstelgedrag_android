package achan.nl.uitstelgedrag.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.text.SimpleDateFormat;
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
    private SimpleDateFormat sf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
    private OnListChangedListener callback;
    public NoteRecordingAttachmentViewHolder lastCaller;
    private MediaPlayer player;

    public NoteAdapter(List<Note> notes, Context context, MediaPlayer player) {
        this.notes = notes;
        this.context = context;
        this.player = player; // has to be managed outside of the adapter due to lifecycle issues.
    }

    public void setOnListChangedListener(OnListChangedListener callback){
        this.callback = callback;
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
        else //(note.attachment.type == Attachment.ATTACHMENT_TYPE_PHOTO)
            return VIEWTYPE_PHOTO;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        View view;
        switch (viewType) {
            case VIEWTYPE_RECORDING:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_recording, parent, false);
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
            NoteRecordingAttachmentViewHolder recordingView = (NoteRecordingAttachmentViewHolder) holder;
            //MediaPlayer player = MediaPlayer.create(recordingView.context, Uri.parse(note.attachment.path)); note fixes non-final player issue.
            recordingView.title.setText(note.text);

            // fixme - take this out of the onBind().
            // If the player is still playing, stop it and set it to the new file.
            if (player != null) {
                if (player.isPlaying()) {
                    player.pause();
                    if (lastCaller != null)
                        lastCaller.playPause.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_play_arrow_black_24dp));
                }
                player.stop();
                player.reset();
//                    player.release();
            }

            // Create it for reading data, not for playback. Not yet.
            player = player.create(context, Uri.parse(note.attachment.path));

            // Measure metadata: get track length in minutes:seconds.
            int length = player.getDuration(); // Length in milliseconds.
            int current_position = player.getCurrentPosition(); // Current position in milliseconds.

            int display_seconds = length/1000%60;
            int display_minutes = Math.round(length/1000/60);

            int length_seconds = length/1000;
            int length_minutes = Math.round(length_seconds / 60);// fixme modulo shit
            int current_seconds = current_position/1000;
            int current_minutes = current_seconds/60;// fixme modulo shit

            Log.i("MediaPlayer", String.format("path: %s, length: %d, current: %d", note.attachment.path, length, current_position));

            recordingView.title.setText("Opname");
            recordingView.created.setText("toegevoegd op " + sf.format(note.created));
            recordingView.current.setText("00:00"); // Player has not started yet anyway.
//                recordingView.current.setText(String.format("%02d:%02d", current_minutes, current_seconds));
            recordingView.length.setText(String.format("%02d:%02d", display_minutes, display_seconds));
            recordingView.seekbar.setMax(length);

            player.reset(); // Free it up to let other viewholder bind.

            recordingView.setOnPlayerStateChangedListener(() -> {
                recordingView.playPause.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_play_arrow_black_24dp));
            });
            recordingView.playPause.setOnClickListener(v -> {

                // fixme - change imagebutton state when other viewholder is toggled.
                // todo - seekbar
//
                if (player.isPlaying() && lastCaller == recordingView) {
                    Log.i("LogViewAdapter", "OnClickListener: paused mediaplayer.");
                    player.pause();
                    recordingView.playPause.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_play_arrow_black_24dp));
                }
                else {
                    if (player != null) {
                        if (player.isPlaying()) {
                            player.pause();
                        }
                        player.stop();
                        player.reset();
//                          player.release();
                        player = player.create(context, Uri.parse(note.attachment.path));
                    }

                    Log.i("LogViewAdapter", "OnClickListener: started mediaplayer.");
                    if (lastCaller != null)
                        lastCaller.playPause.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_play_arrow_black_24dp));
                    player.start();
                    player.setOnCompletionListener(mp -> {
                        Log.i("LogViewAdapter", "Mediaplayer finished playing.");
                        lastCaller.playPause.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_play_arrow_black_24dp));
                    });
                    recordingView.playPause.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_pause_black_24dp));
                    lastCaller = recordingView;
                }
            });

            recordingView.itemView.setOnLongClickListener(v -> {

                PopupMenu popup = new PopupMenu(context, v);
                popup.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.popup_edit:
                            Snackbar.make(v, "Nog niet beschikbaar :')", Snackbar.LENGTH_SHORT).show();
                            break;
                        case R.id.popup_delete:
                            if (player != null && player.isPlaying())
                                player.stop();
                            removeItem(recordingView.getAdapterPosition());
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

        @BindView(R.id.recording_created)           TextView    created;
        @BindView(R.id.recording_title)             TextView    title;
        @BindView(R.id.recording_playpause)         ImageButton playPause;
        @BindView(R.id.recording_current_position)  TextView    current;
        @BindView(R.id.recording_seekbar)           SeekBar     seekbar;
        @BindView(R.id.recording_length)            TextView    length;
        @BindView(R.id.recording_layout)            CardView layout;

        Context context;
        PlayerStateChangeListener listener;

        public NoteRecordingAttachmentViewHolder(View itemView, Context context) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.context = context;
        }

        public void setOnPlayerStateChangedListener(PlayerStateChangeListener listener) {
            this.listener = listener;
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

    /**
     * Listener to detect changes to the media player.
     */
    public interface PlayerStateChangeListener{

        void onChange();
    }
}
