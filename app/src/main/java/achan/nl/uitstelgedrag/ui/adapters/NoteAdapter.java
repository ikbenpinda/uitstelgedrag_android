package achan.nl.uitstelgedrag.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import achan.nl.uitstelgedrag.R;
import achan.nl.uitstelgedrag.domain.models.Note;
import achan.nl.uitstelgedrag.domain.models.Timestamp;
import achan.nl.uitstelgedrag.persistence.gateways.NoteGateway;
import achan.nl.uitstelgedrag.ui.activities.NoteDetailActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Etienne on 9-10-2016.
 */
public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    Context    context;
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
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        NoteViewHolder holder;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rowlayout_note, parent, false);
        holder = new NoteViewHolder(view, context);
        return holder;
    }

    @Override
    public void onBindViewHolder(NoteViewHolder holder, int position) {
        Note note = notes.get(holder.getAdapterPosition());
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, NoteDetailActivity.class);
            intent.putExtra("note_id", note.id);
            context.startActivity(intent);
        });
        holder.itemView.setOnLongClickListener(v ->
        {
            PopupMenu popup = new PopupMenu(context, v);
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()){
                    case R.id.popup_edit:
                        Snackbar.make(v, "Nog niet beschikbaar :')", Snackbar.LENGTH_SHORT).show();
                        break;
                    case R.id.popup_delete:
                        removeItem(holder.getAdapterPosition());
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
        holder.text.setText(note.text);
        holder.timestamp.setText(Timestamp.formatDate(note.created));
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
}
