package achan.nl.uitstelgedrag.domain.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Dayplan model for the dayplanner.
 *
 * Created by Etienne on 16-8-2016.
 */
public class Dayplan implements Serializable, Comparable<Dayplan> { // TODO backlogging of passed dates.

    public long id = -1;

    /**
     * A yyyy-mm-dd timestamp. This is used for ordering of the days.
     */
    public Date       day;
    public List<Task> tasks = new ArrayList<>();

    /**
     * Lazy initialization constructor, don't use this outside of the ORM.
     */
    public Dayplan(){}

    public Dayplan(Date day, List<Task> tasks) {
        this.day = day;
        this.tasks = tasks;
    }

    public Dayplan(Date day, Task... tasks) {
        this.day = day;
        this.tasks = Arrays.asList(tasks);
    }

    @Override
    public int compareTo(Dayplan another) {
        return this.day.compareTo(another.day);
    }

    @Override
    public String toString() {
        return "Dayplan{" +
                "day=" + day +
                ", tasks=" + tasks.size() +
                '}';
    }
}
