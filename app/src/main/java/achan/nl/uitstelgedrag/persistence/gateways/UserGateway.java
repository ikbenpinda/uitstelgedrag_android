package achan.nl.uitstelgedrag.persistence.gateways;

import android.content.Context;
import android.util.Log;

import achan.nl.uitstelgedrag.domain.models.Profile;
import achan.nl.uitstelgedrag.persistence.UserRepository;

/**
 * User profile persistence handler class.
 *
 * Created by Etienne on 13-8-2016.
 */
public class UserGateway implements UserRepository { // TODO read Clean Architecture.

    private Context context;

    /**
     * Default filename to write the profile to.
     */
    public static final String filename = "user_profile";

    public UserGateway(Context context) {
        this.context = context;
    }

    /**
     * Saves the profile, overwriting if one already exists.
     * @param profile
     */
    public void saveProfile(Profile profile){

        new IOGateway(context).write(filename, profile);
        Log.i("UserRepository", "Saved profile to " + filename);
    }

    /**
     * Loads the last saved profile, or null if not available.
     * @return
     */
    public Profile loadProfile(){

        Profile profile =  (Profile) new IOGateway(context).read(filename);
        Log.i("UserRepository", "Loaded profile: " + profile);

        return profile;
    }
}
