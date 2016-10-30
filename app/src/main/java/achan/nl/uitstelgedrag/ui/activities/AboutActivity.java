package achan.nl.uitstelgedrag.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import achan.nl.uitstelgedrag.BuildConfig;
import achan.nl.uitstelgedrag.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutActivity extends Base {

    @BindView(R.id.tvBuildNo) TextView tvBuildNo;
    @BindView(R.id.tvUpdateStatus) TextView tvUpdateStatus;
    @BindView(R.id.btGithubLink) Button   btGithubLink;
    @BindView(R.id.tvServerStateRemote) TextView tvServerStateRemote;
    @BindView(R.id.tvServerStateLocal) TextView tvServerStateLocal;

    @Override
    Activities getCurrentActivity() {
        return Activities.ABOUT;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        tvBuildNo.setText("Versie " + BuildConfig.VERSION_NAME);
        btGithubLink.setOnClickListener(v -> {
            String url = "https://github.com/ikbenpinda/uitstelgedrag_android";
            Intent i   = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });

        // TODO API uplink for server responses.
    }
}
