package achan.nl.uitstelgedrag.ui.activities;

import android.os.Bundle;
import android.webkit.WebView;

import achan.nl.uitstelgedrag.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class HelpActivity extends Base {

    @BindView(R.id.webView) WebView webView;

    @Override
    Activities getCurrentActivity() {
        return Activities.HELP;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        webView.loadUrl("a-chan.nl/uitstelgedrag/help");
    }
}
