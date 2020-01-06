package achan.nl.uitstelgedrag.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import achan.nl.uitstelgedrag.R;
import achan.nl.uitstelgedrag.domain.ChanDownParser;
import achan.nl.uitstelgedrag.domain.models.Label;
import achan.nl.uitstelgedrag.persistence.gateways.LabelGateway;

/**
 * Created by Etienne on 26-3-2016.
 */
public class LabelAdapter extends ArrayAdapter<Label> {

    List<Label> labels;
    LabelGateway labeldb;
    ChanDownParser parser;

    private Filter filter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<Label> allLabels = labeldb.getAll();

            if (constraint == null){
                results.values = allLabels;
                return results;
            }

            String sequence = constraint.toString();

//            results.values = parser.excludeBfromA(parser.parseLabels(sequence, allLabels), allLabels);
            boolean lastCharacterEqualsDelimiterSign = sequence.trim().substring(sequence.length() -2).equals(",");
            if (lastCharacterEqualsDelimiterSign) {
                results.values = parser.excludeBfromA(allLabels, parser.parseLabels(sequence, allLabels));
                return results;
            }

            // CommaTokenizer does not return the full input,
            //so this is used for delimiting instead.
            List<String> strings = new ArrayList<>(parser.sanitize(sequence));
            int indexLastItem = strings.size() -1;
            String tail = strings.get(indexLastItem);

            Log.i("Filtering", "performFiltering: [" + sequence + "](" + tail + ")");

            List<Label> suggestions = new LinkedList<>();

            for (Label label : allLabels) {
                boolean partOfString = tail.length() > 3 && label.title.toLowerCase().contains(tail.toLowerCase());
                boolean startsWith = label.title.toLowerCase().startsWith(tail.toLowerCase());
                if (startsWith || partOfString)
                    suggestions.add(label);
            }

            results.values = suggestions;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            if (results.values != null)
                addAll(((List<Label>) results.values));
            notifyDataSetChanged();
        }
    };

    public LabelAdapter(Context context, int resource, List<Label> labels) {
        super(context, resource);
        this.labels = labels;
        labeldb = new LabelGateway(context);
        parser = new ChanDownParser();
    }

    @Override
    public void insert(Label object, int index) {
        labels.add(index, object);
        Log.i("LabelAdapter", "Item added: " + object.title);
        notifyDataSetChanged();
    }

    @Override
    public void clear() {
        labels.clear();
        Log.i("LabelAdapter", "Cleared items.");
        notifyDataSetChanged();
    }

    @Override
    public void addAll(@NonNull Collection<? extends Label> collection) {
        labels.addAll(collection);
        Log.i("LabelAdapter", "Items added: " + collection.size());
        notifyDataSetChanged();
    }

    @Override
    public void addAll(Label... items) {
        labels.addAll(Arrays.asList(items));
        Log.i("LabelAdapter", "Items added: " + items.length);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (position > labels.size())
            return null;

        Label label = labels.get(position);

        View labelView = convertView != null?
                convertView :
                LayoutInflater.from(getContext()).inflate(R.layout.rowlayout_label, null);

        ImageView labelIcon = (ImageView) labelView.findViewById(R.id.label_icon);
        TextView labelTitle = (TextView) labelView.findViewById(R.id.label_title);
        TextView labelDescription = (TextView) labelView.findViewById(R.id.label_description);

        labelIcon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_local_offer_black_24dp));
        labelTitle.setText(label.title);

        int defaultTextColor = getContext().getResources().getColor(R.color.accent);
        int textColor = label.color != null ? Integer.parseInt(label.color): defaultTextColor;
        labelTitle.setTextColor(textColor);

        labelDescription.setText("");

        if (label.location != null) {
            // fixme - Only for API 19! Deprecated since 22.
            labelIcon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_place_black_24dp ));

            StringBuilder locationFormatter = new StringBuilder("");

            if (label.location.city != null)
                locationFormatter.append(label.location.city);
            if (label.location.address != null)
                locationFormatter.append(", ").append(label.location.address);
            if (label.location.postalCode != null)
                locationFormatter.append(", ").append(label.location.postalCode);

            labelDescription.setText(locationFormatter);
            labelDescription.setTextColor(Color.WHITE);
        }

        return labelView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return filter;
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
}
