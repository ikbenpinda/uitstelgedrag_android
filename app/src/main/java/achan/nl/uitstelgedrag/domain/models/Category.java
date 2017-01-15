package achan.nl.uitstelgedrag.domain.models;

/**
 * Created by Etienne on 26-3-2016.
 */
public class Category {

    public long id = -1;
    public String category;
    public Location location;

    public Category(String category) {
        this.category = category;
    }
}
