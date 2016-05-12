package achan.nl.uitstelgedrag.location;

import android.location.Location;

/**
 *
 * The Location API android deserves, but not the one it needs right now.
 *
 * Created by Etienne on 9-5-2016.
 */
public interface LocationHandler {
    Location getLocation();
    String getAddress(Location location);
}
