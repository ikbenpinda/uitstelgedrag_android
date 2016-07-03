package achan.nl.uitstelgedrag.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import achan.nl.uitstelgedrag.R;
import achan.nl.uitstelgedrag.domain.models.Task;
import achan.nl.uitstelgedrag.persistence.gateways.TaskGateway;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskDetailActivity extends AppCompatActivity {

    Context context;

    @BindView(R.id.taskdetail_title)
    TextView     taskdetailTitle;

    @BindView(R.id.taskdetail_deadline)
    TextView     taskdetailDeadline;

    @BindView(R.id.taskdetail_labels_layout)
    LinearLayout taskdetailLabelsLayout;

    @BindView(R.id.button)
    Button       button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        ButterKnife.bind(this);
        context = getBaseContext();
        int id = getIntent().getIntExtra("task_id", -1);
        Task task = getTask(id);
        taskdetailTitle.setText(task.description);
        taskdetailDeadline.setText("geen deadline");
        if (task.category != null) {
            TextView textView = new TextView(context);
            textView.setText(task.category.toString());
            taskdetailLabelsLayout.addView(textView);
        };
    }

    private Task getTask(int id){
        return new TaskGateway(context).get(id);
    }
}
