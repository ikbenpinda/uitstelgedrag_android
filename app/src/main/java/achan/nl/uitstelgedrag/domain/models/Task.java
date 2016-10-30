package achan.nl.uitstelgedrag.domain.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

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

    public Task(String description){
        this.createdOn = new Date();
        this.description = description;
    }

    @Override
    public int compareTo(Task another) { // FIXME/TODO Resolve binary dependency with Dayplan?
        return createdOn.compareTo(another.createdOn);
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
