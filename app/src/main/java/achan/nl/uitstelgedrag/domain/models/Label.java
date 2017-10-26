package achan.nl.uitstelgedrag.domain.models;

/**
 * Domain model for labels used for notes and tasks.
 *
 * Created by Etienne on 25-10-2016.
 */
public class Label {
    public int id = -1;
    public String title;
    public String color = null;
    public String description;

    // Note - Subclassing Label just makes things harder without any substantial gain.
    public Location location;

    @Override
    public String toString() {
        return title;
    }

    @Override
    public boolean equals(Object obj) {
        return ((Label) obj).title.equals(title);
    }
}
