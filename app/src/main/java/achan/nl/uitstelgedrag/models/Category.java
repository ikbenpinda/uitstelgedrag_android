package achan.nl.uitstelgedrag.models;

import java.util.List;

/**
 * Created by Etienne on 26-3-2016.
 */
public class Category {

    public int id = -1;
    public String category;
    public List<Task> tasks;

    public Category(String category, List<Task> tasks) {
        this.category = category;
        this.tasks = tasks;
    }
}
