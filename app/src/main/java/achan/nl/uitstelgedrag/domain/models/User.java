package achan.nl.uitstelgedrag.domain.models;

import java.util.List;

/**
 * Created by Etienne on 18-7-2016.
 */
public class User {

    String id;

    List<Task> tasks;
    List<Timestamp> attendances;
    // List<Dayplan> dayplans;
    // List<Mood> moods;
}
