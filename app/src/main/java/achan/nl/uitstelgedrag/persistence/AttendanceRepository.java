package achan.nl.uitstelgedrag.persistence;

import java.util.List;

import achan.nl.uitstelgedrag.domain.models.Timestamp;

/**
 * Created by Etienne on 30-5-2016.
 */
public interface AttendanceRepository {
    Timestamp get(int id);
    List<Timestamp> getAll();
    Timestamp insert(Timestamp timestamp);
    boolean delete(Timestamp timestamp);
    Timestamp update(Timestamp timestamp);
}
