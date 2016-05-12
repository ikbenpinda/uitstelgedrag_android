package achan.nl.uitstelgedrag.ui.adapters;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

import achan.nl.uitstelgedrag.R;
import achan.nl.uitstelgedrag.models.Task;
import achan.nl.uitstelgedrag.persistence.UitstelgedragOpenHelper;

/**
 * // FIXME: 29-4-2016 Transactions
 * // FIXME: 29-4-2016 Error handling
 * // FIXME: 29-4-2016 IllegalStateException on RecyclerView
 * // FIXME: 29-4-2016 Data layer in view layer.
 * // FIXME: 29-4-2016 Categories
 *
 * Created by Etienne on 26-3-2016.
 */
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    TaskViewHolder holder;
    List<Task>     tasks;
    Context        context;

    public TaskAdapter(List<Task> tasks, Context context){
        this.tasks = tasks;
        this.context = context;
    }

    public void addItem(final int position, Task task) {
        tasks.add(position, task);
        Handler handler = new Handler(Looper.getMainLooper());  //  Fixes IllegalStateException: redraw while calculatiing layout
        handler.post(new Runnable() {
            @Override
            public void run() {
                //structural events = whole list, item events = single items
                notifyItemInserted(position); // Exception - Inconsistency detected.
                //notifyDataSetChanged();       // Stops animation and re-layout.
            }
        });
    }

    public void removeItem(final int position) {
        tasks.remove(position);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                notifyItemRemoved(position);
                //notifyDataSetChanged();
            }
        });
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rowlayout_task, parent, false);
        holder = new TaskViewHolder(view, context);
        return holder;
    }

    @Override
    public void onBindViewHolder(final TaskViewHolder holder, final int position) {

        // FIXME: 29-4-2016 Document what the hell is going on here.
        String description = tasks.get(holder.getAdapterPosition()).description;
        holder.taskdescription.setText(description);
        holder.taskDone.setChecked(false);
        holder.taskDone.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (!isChecked)
                    return;

                final int adapterposition = holder.getAdapterPosition();

                Log.i("UITSTELGEDRAG", "Adapterpos=" + adapterposition);

                final Task selected = tasks.get(adapterposition);

                removeItem(adapterposition);
                Log.w("", selected.toString());

                // http://stackoverflow.com/questions/30850494/confirmation-and-undo-removing-in-recyclerview

                Log.i("UITSTELGEDRAG", "Task #" + selected.id + " removed: " + selected.description);

                Snackbar.make(holder.itemView, "Taak verwijderd: " + selected.description, Snackbar.LENGTH_SHORT)
                        .setAction("Ongedaan maken", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final int bottom = getItemCount();
                                addItem(/* FIXME: adapterposition */bottom, selected);
                            }
                        })
                        .setCallback(new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar snackbar, int event) {
                                super.onDismissed(snackbar, event);

                                // User consciously deletes the item.
                                if (event == DISMISS_EVENT_SWIPE
                                        | event == DISMISS_EVENT_TIMEOUT
                                        | event == DISMISS_EVENT_CONSECUTIVE){
                                    new UitstelgedragOpenHelper(context, null).deleteTask(selected);
                                    Log.i("Snackbar", "Item removed. pos=" + adapterposition);
                                }
                            }
                        })
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    /**
     * Custom AttendanceViewHolder for TaskAdapter.
     *
     * Caches UI views by holding findviewbyid() results in-memory. Reduces performance hit.
     */
    public class TaskViewHolder extends RecyclerView.ViewHolder{

        // binding happens here.
        public CheckBox taskDone;
        public TextView taskdescription;
        public Context context;

        public TaskViewHolder(final View itemView, final Context context) {
            super(itemView);
            this.context = context;

            taskDone = (CheckBox) itemView.findViewById(R.id.TaskViewCheckBox);
            taskdescription = (TextView) itemView.findViewById(R.id.TaskViewDescriptionTextView);


        }
    }
}
