package achan.nl.uitstelgedrag.ui.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import achan.nl.uitstelgedrag.R;
import achan.nl.uitstelgedrag.domain.models.Label;
import achan.nl.uitstelgedrag.domain.models.Task;
import achan.nl.uitstelgedrag.domain.models.Timestamp;
import achan.nl.uitstelgedrag.persistence.gateways.TaskGateway;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskDetailActivity extends Base {

//    Context context;

    @BindView(R.id.taskdetail_title)
    TextView taskdetailTitle;

    @BindView(R.id.taskdetail_deadline)
    TextView     taskdetailDeadline;

    @BindView(R.id.taskdetail_labels_layout)
    LinearLayout taskdetailLabelsLayout;

    @BindView(R.id.button)
    Button button;

    @Override
    Activities getCurrentActivity() {
        return Activities.TASK_DETAIL;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        int id = getIntent().getIntExtra("task_id", -1);
        Task t = getTask(id);
        taskdetailTitle.setText(t.description);
        if (t.deadline != null)
            taskdetailDeadline.setText(Timestamp.formatDate(t.deadline));
        if (!t.labels.isEmpty())
            taskdetailLabelsLayout.removeAllViews();
            for (Label label : t.labels) {
                TextView view = new TextView(this);
                view.setText(label.title);
                view.setBackgroundColor(Color.DKGRAY);
                view.setTextColor(Color.WHITE);
                taskdetailLabelsLayout.addView(view);
            }
    }

    private Task getTask(int id){
        return new TaskGateway(this).get(id);
    }
}
