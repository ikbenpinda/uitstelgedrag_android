package achan.nl.uitstelgedrag.ui.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import achan.nl.uitstelgedrag.R;
import achan.nl.uitstelgedrag.domain.models.Label;
import butterknife.internal.Utils;

/**
 * Created by Etienne on 17-10-2017.
 */

public class LabelSpinnerAdapter extends ArrayAdapter<Label> {

    List<Label> labels;

    public LabelSpinnerAdapter(@NonNull Context context, @LayoutRes int resource, List<Label> labels) {
        super(context, resource);
        this.labels = labels;
    }

    @Override
    public void insert(Label label, int index) {
        labels.add(index, label);
        Log.i("LabelAdapter", "Item added: " + label.title);
        notifyDataSetChanged();
    }

    @Override
    public void clear() {
        labels.clear();
        Log.i("LabelAdapter", "Cleared items.");
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return labels.size();
    }

    @Nullable
    @Override
    public Label getItem(int position) {
        return labels.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (position > labels.size())
            return null;

        Label label = labels.get(position);


        //fixme - layout for performance?

        View labelView = convertView != null?
                convertView :
                LayoutInflater.from(getContext()).inflate(R.layout.spinner_dropdown_label_item, null);

        TextView labelTitle = (TextView) labelView.findViewById(R.id.spinner_dropdown_label_item_title);

        labelTitle.setText(label.title);
//        labelTitle.setTextColor(Color.WHITE);
//        labelTitle.setBackgroundColor(getContext().getResources().getColor(R.color.base16_blue));

//        Note - Legacy code.
//        TextView textView = new TextView(getContext());
//        int padding = com.mikepenz.iconics.utils.Utils.convertDpToPx(getContext(), 16);
//        textView.setPadding(padding, padding, padding, padding);
//
//        SpannableString spannable = new SpannableString(label.title);
//
//        Resources resources = getContext().getResources();
//        Resources.Theme theme = getContext().getTheme();
//
//        int backgroundColor = Utils.getColor(resources, theme, R.color.base16_eighties_red);// todo - set background to auto, random, or user-defined color.
//        int foregroundColor = Utils.getColor(resources, theme, R.color.primary); // todo - set foreground to black if necessary
//
//        BackgroundColorSpan background = new BackgroundColorSpan(backgroundColor);
//        ForegroundColorSpan foreground = new ForegroundColorSpan(foregroundColor);
//
//
//        spannable.setSpan(background, 0, label.title.length(), 0);
//        spannable.setSpan(foreground, 0, label.title.length(), 0);
//
//        labelTitle.setText(spannable);
        // set background to label-specific color


        // set foreground to white or black depending on theme or visibility.

        /*
//        ImageView labelIcon = (ImageView) labelView.findViewById(R.id.label_icon);
        TextView labelTitle = (TextView) labelView.findViewById(R.id.label_title);
        TextView labelDescription = (TextView) labelView.findViewById(R.id.label_description);

//        labelIcon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_local_offer_black_24dp));
        labelTitle.setText(label.title);
        labelDescription.setText("");

        if (label.location != null) {
            // fixme - Only for API 19! Deprecated since 22.
//            labelIcon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_place_black_24dp));
            // todo - more human-readable display.
            labelTitle.setText(label.title);
            if (label.description != null)
                labelDescription.setText(label.description);
        }
*/
        return labelView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (position > labels.size())
            return null;

        Label label = labels.get(position);

        View labelView = convertView != null?
                convertView :
                LayoutInflater.from(getContext()).inflate(R.layout.spinner_dropdown_label_item, null);

        TextView labelTitle = (TextView) labelView.findViewById(R.id.spinner_dropdown_label_item_title);

        int defaultTextColor = getContext().getResources().getColor(R.color.accent);
        int textColor = label.color != null ? Integer.parseInt(label.color): defaultTextColor;

        labelTitle.setText(label.title);
        labelTitle.setTextColor(textColor);

        return labelView;
    }
}
