package achan.nl.uitstelgedrag.ui.activities;

import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import achan.nl.uitstelgedrag.R;
import achan.nl.uitstelgedrag.domain.models.Note;
import achan.nl.uitstelgedrag.domain.models.Timestamp;
import achan.nl.uitstelgedrag.persistence.gateways.NoteGateway;
import butterknife.BindView;
import butterknife.ButterKnife;

public class NoteDetailActivity extends Base {

    @BindView(R.id.note_detail_content_textview) TextView content;
    @BindView(R.id.note_detail_creation_date) TextView creation_date;

    @Override
    Activities getCurrentActivity() {
        return Activities.NOTE_DETAIL;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        Note note = new NoteGateway(this).get(getIntent().getIntExtra("note_id", -1));

        content.setText(note.text);
        creation_date.setText(Timestamp.formatDate(note.created));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "\"Edit note\"-view should appear", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());
    }

}
