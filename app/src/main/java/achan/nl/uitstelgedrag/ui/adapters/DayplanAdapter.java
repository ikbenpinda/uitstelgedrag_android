package achan.nl.uitstelgedrag.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Date;

import achan.nl.uitstelgedrag.R;
import achan.nl.uitstelgedrag.domain.models.Dayplan;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter for dayplans.
 *
 * Created by Etienne on 17-8-2016.
 */
public class DayplanAdapter extends RecyclerView.Adapter<DayplanAdapter.DayplanViewHolder>{

    Context context;
    Dayplan plan;

    DayplanViewHolder holder;

    public DayplanAdapter(Dayplan plan, Context context) {
        this.plan = plan;
        this.context = context;
    }

    @Override
    public DayplanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rowlayout_dayplan, parent, false);
        holder = new DayplanViewHolder(view, context);
        return holder;
    }

    @Override
    public void onBindViewHolder(DayplanViewHolder holder, int position) {
        Date day = plan.day;
        holder.grip.setOnClickListener(v -> Toast.makeText(holder.context, "Reorder icon clicked", Toast.LENGTH_SHORT).show());
        holder.title.setText(plan.tasks.get(holder.getAdapterPosition()).description);

        if (DateUtils.isToday(day.getTime())){
            holder.title.setTextColor(Color.argb(150, 100, 100, 255));
        }
    }

    @Override
    public int getItemCount() {
        return plan == null || plan.tasks == null? 0 : plan.tasks.size();
    }

    class DayplanViewHolder extends RecyclerView.ViewHolder{

        public @BindView(R.id.rowlayout_dayplan_grip_icon) ImageView grip;
        public @BindView(R.id.rowlayout_dayplan_task_title) TextView title;

        Context context;

        public DayplanViewHolder(View itemView, final Context context) {
            super(itemView);
            this.context = context;
            ButterKnife.bind(this, itemView);
        }
    }
}
