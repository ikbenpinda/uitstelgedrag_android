package achan.nl.uitstelgedrag.domain.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import achan.nl.uitstelgedrag.domain.TaskBuilder;

/**
 * Created by Etienne on 26-3-2016.
 */
public class Task implements Serializable, Comparable<Task>{

    public int          id = -1; // will be automatically set in the data layer.

    public Date         createdOn = null;
    public List<Label>  labels = new ArrayList<>();
    public String       description;
    public Date         deadline = null;

    public Task() {}

    /**
     * Creates a new task with the date set to today.
     * @param description the given description for this task.
     */
    public Task(String description){
        this.createdOn = new Date();
        this.description = description;
    }

    /**
     * Recommended approach for initializing/creating tasks.
     * The default way of constructing a task object remains working
     * for legacy reasons.
     *
     */
    public static TaskBuilder Builder(){
        return new TaskBuilder();
    }

    public void setDeadline(Date deadline){
        if (deadline.before(createdOn))
            throw new IllegalStateException("Deadline is set before creation date.");

        this.deadline = deadline;
    }

    public void setDescription(String description){
        if (description == null || description.isEmpty())
            throw new IllegalStateException("Description is not set.");;

        this.description = description;
    }

    public void setLabels(List<Label> labels){
        if (labels == null)
            throw new IllegalStateException("List is null.");

        this.labels = labels;
    }

    public void setCreatedOn(Date creationDate){
        if (creationDate == null)
            throw new IllegalStateException("CreationDate is null.");
        if (creationDate.after(new Date()))
            throw new IllegalStateException("Creation date is set in the future.");

        this.createdOn = creationDate;
    }

    public void setId(int id){
        if (id < -1)
            throw new IllegalStateException("ID not set. Needs to be either -1 or any positive integer.");

        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public List<Label> getLabels() {
        return labels;
    }

    public String getDescription() {
        return description;
    }

    public Date getDeadline() {
        return deadline;
    }

    @Override
    public int compareTo(Task another) { // FIXME/TODO Resolve binary dependency with Dayplan?
        return createdOn.compareTo(another.createdOn);
    }

    @Override
    public String toString() {
        return description;
    }

    //    public class compareByLabel implements Comparator<Task> {
//
//        @Override
//        public int compare(Task lhs, Task rhs) {
//            return lhs.category.category.compareTo(rhs.category.category);
//        }
//
//        @Override
//        public boolean equals(Object object) {
//            return false;
//        }
//    }
}
