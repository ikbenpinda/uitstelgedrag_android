package achan.nl.uitstelgedrag.ui.activities;

import android.os.Bundle;

import achan.nl.uitstelgedrag.R;
import butterknife.ButterKnife;

public class DayplannerActivity extends BaseActivity {

    //@BindView()

    @Override
    int getLayoutResource() {
        return R.layout.activity_day_planning;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }
}
