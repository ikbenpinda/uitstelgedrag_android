package achan.nl.uitstelgedrag.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import achan.nl.uitstelgedrag.R;
import achan.nl.uitstelgedrag.domain.models.Dayplan;
import achan.nl.uitstelgedrag.domain.models.Label;
import achan.nl.uitstelgedrag.domain.models.Task;
import achan.nl.uitstelgedrag.domain.models.Timestamp;
import achan.nl.uitstelgedrag.persistence.gateways.DayplanGateway;
import achan.nl.uitstelgedrag.persistence.gateways.TaskGateway;
import achan.nl.uitstelgedrag.ui.activities.TaskDetailActivity;
import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * // FIXME: 29-4-2016 Transactions
 * // FIXME: 29-4-2016 Error handling
 * // FIXME: 29-4-2016 Data layer in view layer.
 * // FIXME: 29-4-2016 Categories
 *
 * Created by Etienne on 26-3-2016.
 */
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    @BindColor(R.color.colorAccent) int accent;

    public static final int ITEM = 2;
    public static final int EMPTY = 1;

    public List<Task>       tasks;
    TaskViewHolder          holder;
    Context                 context;
    DayplanGateway          dayplanGateway;
    TaskGateway             taskGateway;
    Location                current;
    OnListChangedListener   callback;

    public TaskAdapter(List<Task> tasks, Context context){
        this.tasks = tasks;
        this.context = context;
        dayplanGateway = new DayplanGateway(context);
        taskGateway = new TaskGateway(context);
    }

    public void addItem(final int position, Task task) {
        tasks.add(position, task);
        Handler handler = new Handler(Looper.getMainLooper());  //  Fixes IllegalStateException: redraw while calculating layout
        handler.post(() -> {
            //structural events = whole list, item events = single items
            notifyItemInserted(position); // Exception - Inconsistency detected.
            //notifyDataSetChanged(); Stops animation and re-layout.
        });
        if (callback != null)
            callback.onListChanged();
    }

    public void removeItem(final int position) {
        tasks.remove(position);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            notifyItemRemoved(position);
            //notifyDataSetChanged(); is obsolete because notifyItemRemoved specifies what changed.
            //                        Same goes for notifyItemInserted.
        });
        if (callback != null)
            callback.onListChanged();
    }

    public void setOnListChangedListener(OnListChangedListener callback){
        this.callback = callback;
    }

    @Override
    public int getItemViewType(int position) {
        return tasks.get(position) != null/*instanceof Task*/ ? ITEM : EMPTY;
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TaskViewHolder holder;
        View view;

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rowlayout_task, parent, false);
        holder = new TaskViewHolder(view, context);

        return holder;
    }

    @Override
    public void onBindViewHolder(final TaskViewHolder holder, final int position) {

        // FIXME: 29-4-2016 Document what the hell is going on here.
        Task task = tasks.get(holder.getAdapterPosition());

        holder.taskdescription.setText(task.description);
        holder.deadline.setText(Timestamp.formatDate(task.deadline));
//        String label = task.category != null && task.category.category != null ? task.category.category : ""; FIXME
        holder.labels.removeAllViews();
        if (task.labels != null && !task.labels.isEmpty()) {
            for (Label label : task.labels) {
                TextView labelsview = new TextView(context);
                labelsview.setText(label.title);
                labelsview.setTextColor(0xFF4081FF);
                holder.labels.addView(labelsview);
            }
        }
        holder.view.setOnClickListener(v1 -> {
            Intent intent = new Intent(context, TaskDetailActivity.class);
            intent.putExtra("task_id", task.id);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
        holder.view.setOnLongClickListener(v -> {
            PopupMenu popup = new PopupMenu(context, v);
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.popup_edit:
                        Snackbar.make(v, "Nog niet beschikbaar :')", Snackbar.LENGTH_SHORT).show();
                        break;
                    case R.id.popup_plan_today:
                        Date today = new Date();

                        Dayplan plan = dayplanGateway.get(today); // Null or Dayplan.

                        if (plan == null) {
                            Log.i("TaskAdapter", "No dayplan found for today - created new one.");
                            plan = new Dayplan(new Date(), task);
                        } else {
                            Log.i("TaskAdapter", "Dayplan found! plan = " + plan.toString());
                            plan.tasks.add(task);
                        }
//                        task.related_planning = plan;
//                        dayplanGateway.update(plan);
                        taskGateway.update(task); // FIXME NPE

                        Snackbar.make(v, "Gepland voor vandaag! " + holder.taskdescription.getText().toString(), Snackbar.LENGTH_SHORT).show();
                        break;
                    case R.id.popup_plan_tomorrow:
                        Snackbar.make(v, "Gepland voor morgen! " + holder.taskdescription.getText().toString(), Snackbar.LENGTH_SHORT).show();
                        // TODO: 24-5-2016 move to planner
                        break;
                }

                return true;
            });
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.taskpopupmenu, popup.getMenu());
            popup.show();
            return true;
        });
        holder.creationDate.setText(Timestamp.formatDate(task.createdOn));
        holder.taskDone.setChecked(false);
        holder.taskDone.setOnCheckedChangeListener(
                new OnCheckedChangeListener() {

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
                                .setAction("Ongedaan maken", v -> {
                                    final int bottom = getItemCount();
                                    addItem(adapterposition, selected);
                                })
                                .setCallback(new Snackbar.Callback() {
                                    @Override
                                    public void onDismissed(Snackbar snackbar, int event) {
                                        super.onDismissed(snackbar, event);
                                        // User consciously deletes the item.
                                        if (event == DISMISS_EVENT_SWIPE
                                                | event == DISMISS_EVENT_TIMEOUT
                                                | event == DISMISS_EVENT_CONSECUTIVE) {
                                            new TaskGateway(context).delete(selected);
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

    //  Custom ViewHolders for TaskAdapter.
    //  Caches UI views by holding findviewbyid() results in-memory. Reduces performance hit.

    public class TaskViewHolder extends RecyclerView.ViewHolder{

        // binding happens here.
        @BindView(R.id.TaskViewCheckBox) CheckBox taskDone;
        @BindView(R.id.TaskViewDescriptionTextView) TextView taskdescription;
        @BindView(R.id.rowlayout_task_layout) RelativeLayout view;
        @BindView(R.id.TaskViewLabelsLayout) LinearLayout labels;
        @BindView(R.id.taskCreationDate) TextView creationDate;
        @BindView(R.id.TaskViewDeadlineTextView) TextView deadline;

        public Context context;

        public TaskViewHolder(View itemView, Context context) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}
