package achan.nl.uitstelgedrag.domain.models;

/**
 * Created by Etienne on 26-3-2016.
 */
public class Task {

    public int id = -1;
    public Category category = null;
    public String description;

    public Task() {}

    public Task(String description){
        this.description = description;
    }
}
