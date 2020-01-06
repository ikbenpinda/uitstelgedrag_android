package achan.nl.uitstelgedrag.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import achan.nl.uitstelgedrag.R;
import achan.nl.uitstelgedrag.domain.models.Timestamp;

/**
 * Created by Etienne on 2-5-2016.
 */
public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder> {

    AttendanceViewHolder holder;
    List<Timestamp>      timestamps;
    Context              context;

    public AttendanceAdapter(List<Timestamp> timestamps, Context context) {
        this.timestamps = timestamps;
        this.context = context;
        Log.i("Uitstelgedrag", "timestamps.size="+timestamps.size());
    }

    public void addItem(int position, Timestamp timestamp) {
        timestamps.add(position, timestamp);
        notifyItemInserted(position);
    }

    public void removeItem(int position) {
        timestamps.remove(position);
        notifyItemRemoved(position); // FIXME: 22-4-2016 java.lang.IllegalStateException: Cannot call this method while RecyclerView is computing a layout or scrolling.
    }

    @Override
    public AttendanceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rowlayout_timestamp, parent, false);
        holder = new AttendanceViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(AttendanceViewHolder holder, int position) {
        Timestamp ts = timestamps.get(position);
        StringBuilder ts_str = new StringBuilder()
                .append(ts.translateType(ts.type))
                .append(":\t ")
                .append(ts.translateDay(ts.weekday))
                .append(", ")
                .append(ts.day)
                .append(" ")
                .append(ts.translateMonth(ts.month))
                .append(", om ")
                .append(ts.hours)
                .append(":")
                .append(ts.minutes);
        Log.i("AttendanceAdapter", "Weekday=" + ts.translateDay(ts.weekday));

        holder.textView.setText(ts_str.toString());

        if (ts.type.equals(Timestamp.ARRIVAL)){
            holder.textView.setTextColor(Color.argb(255, 125,200,125));
            holder.textView.setGravity(Gravity.LEFT);
        }
        else {
            holder.textView.setTextColor(Color.argb(255, 200, 125,125));
            holder.textView.setGravity(Gravity.RIGHT);
        }
        holder.textView.setOnClickListener(v -> {
            //
        });
    }

    @Override
    public int getItemCount() {
        return timestamps.size();
    }

    public class AttendanceViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;

        public AttendanceViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.rowlayout_attendances_tv);
        }
    }
}
