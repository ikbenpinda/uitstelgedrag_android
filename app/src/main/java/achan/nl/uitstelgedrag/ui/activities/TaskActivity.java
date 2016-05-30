package achan.nl.uitstelgedrag.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.List;

import achan.nl.uitstelgedrag.R;
import achan.nl.uitstelgedrag.logic.TaskPresenter;
import achan.nl.uitstelgedrag.logic.TaskPresenterImpl;
import achan.nl.uitstelgedrag.models.Task;
import achan.nl.uitstelgedrag.ui.adapters.TaskAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TaskActivity extends BaseActivity {

    Context    context;
    List<Task> tasks;
    TaskAdapter adapter;
    TaskPresenter presenter;

    @BindView(R.id.AddTaskButton)   Button       AddTaskButton;
    @BindView(R.id.MainList)        RecyclerView list;
    @BindView(R.id.drawer_layout)   DrawerLayout drawer;
    @BindView(R.id.left_drawer)     ListView     drawerlist; // FIXME: 21-5-2016 recview

    @Override
    int getLayoutResource() {
        return R.layout.activity_task;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        context = getApplicationContext();
        presenter = new TaskPresenterImpl(context);
        tasks = presenter.viewTasks();

        adapter = new TaskAdapter(tasks, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        list.setLayoutManager(layoutManager);
        list.setAdapter(adapter);
    }

    @OnClick(R.id.AddTaskButton) void addtask(View v){
        //EditText cat = (EditText) findViewById(R.id.AddTaskCategory); // TODO: 29-4-2016
        EditText desc = (EditText) findViewById(R.id.AddTaskDescription);
        Task     task = new Task(desc.getText().toString());
        presenter.addTask(task);
        adapter.addItem(adapter.getItemCount(), task);
        //adapter.notifyDataSetChanged();

        desc.setText("");
        desc.clearFocus();
    }
}
