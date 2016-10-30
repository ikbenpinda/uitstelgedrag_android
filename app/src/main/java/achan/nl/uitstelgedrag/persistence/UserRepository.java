package achan.nl.uitstelgedrag.persistence;

import achan.nl.uitstelgedrag.domain.models.Profile;

/**
 * User repository interface.
 *
 * Created by Etienne on 14-8-2016.
 */
public interface UserRepository {

    /**
     * Saves the profile, overwriting if one already exists.
     * @param profile
     */
    void saveProfile(Profile profile);

    /**
     * Loads the last saved profile, or null if not available.
     * @return
     */
    Profile loadProfile();
}
