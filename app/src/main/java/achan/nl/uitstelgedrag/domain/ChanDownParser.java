package achan.nl.uitstelgedrag.domain;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import achan.nl.uitstelgedrag.domain.models.Label;
import achan.nl.uitstelgedrag.domain.models.Task;

/**
 * Created by Etienne on 13-11-2017.
 */
public class ChanDownParser {

    public static final char LOCATION_SIGN = '@';
    public static final char LABEL_SIGN = '#';
    public static final String DELIMITER_SIGN = ","; // verify '\n' as alternative?
    public static final int MIN_INPUT = 1;

    public ChanDownParser() {
    }

    /**
     * Parses user input into separate tasks.
     * @param input a non-sanitized string.
     * @return an list of tasks, or an empty list
     * it nothing could be parsed.
     */
    public List<Task> parseTasks(String input){
        List<Task> tasks = new ArrayList<>();

        if (input == null || input.isEmpty())
            return tasks;

        Set<String> values = sanitize(input);

        for (String string: values) {
            Task task = new Task(string);

            // Note - 'poweruser mode' parsing goes here.

            tasks.add(task);
        }

        return tasks;
    }

    /**
     * Parses user input into separate labels.
     * @param input a non-sanitized string.
     * @param existing an optional list of labels to use instead
     *                 of creating new ones when there's a match.
     * @return a list with the parsed labels, or an empty list
     * if no labels could be added.
     */
    public List<Label> parseLabels(String input, List<Label> existing){

        List<Label> labels = new ArrayList<>();

        if (input == null || input.isEmpty())
            return labels;

        Set<String> values = sanitize(input);

        for (String value : values) {
            Label label = new Label();
            label.title = value;
            labels.add(label);
        }

        labels = replaceExistingInValues(labels, existing);

        return labels;
    }

    /**
     * Separates aggregated input into a set of separated values.
     * @param input
     * @return a Set with values delimited, in chronological order.
     */
    public Set<String> sanitize(String input){
        Set<String> values = new LinkedHashSet<>();

        String[] inputs = input.trim().split(DELIMITER_SIGN);
        for (String string : inputs) {
            values.add(string.trim());
        }

        return values;
    }


    /**
     * Replaces items in list values where they already exist in list existing.
     * Basically merges List B into A.
     * @param values the new values.
     * @param existing the existing values.
     * @param <T> the type of items compared.
     * @return a list with the new values where existing values
     * have replaced the new ones where applicable.
     */
    public <T> List<T> replaceExistingInValues(List<T> values, List<T> existing){

        List<T> results = new ArrayList<>();

        for (T intermediate : values) {

            if (existing != null && !existing.isEmpty()) {
                int index = existing.indexOf(intermediate);
                boolean alreadyExists = index > -1;

                intermediate = alreadyExists ? existing.get(index) : intermediate;
            }

            results.add(intermediate);
        }

        return results;
    }

    /**
     * Excludes items in list A that are included in list B.
     * @param a
     * @param b
     * @return
     */
    public <T> List<T> excludeBfromA(List<T> a, List<T> b){
        List<T> results = new ArrayList<>(a);

        for (T item : a) {
            if (b.contains(item))
                results.remove(item);
        }

        return results;
    }
}
