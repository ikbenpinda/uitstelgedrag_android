package achan.nl.uitstelgedrag.domain.models;

/**
 * Created by Etienne on 25-10-2016.
 */
public class Label {
    public int id = -1;
    public String title;
    public String description;

    // Note - Subclassing Label just makes things harder without any substantial gain.
    public android.location.Location location;
}
