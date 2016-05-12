package achan.nl.uitstelgedrag.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import achan.nl.uitstelgedrag.R;
import achan.nl.uitstelgedrag.models.Timestamp;

/**
 * Created by Etienne on 2-5-2016.
 */
public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder> {

    AttendanceViewHolder holder;
    List<Timestamp>      timestamps;
    Context              context;

    public AttendanceAdapter(List<Timestamp> timestamps,Context context) {
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
                .append(ts.translateDay(ts.day))
                .append(", ")
                .append(ts.day)
                .append(" ")
                .append(ts.translateMonth(ts.month))
                .append(", om ")
                .append(ts.hours)
                .append(":")
                .append(ts.minutes);

        holder.textView.setText(ts_str.toString());
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
            }
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
