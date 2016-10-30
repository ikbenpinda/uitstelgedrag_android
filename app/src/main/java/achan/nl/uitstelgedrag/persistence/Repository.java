package achan.nl.uitstelgedrag.persistence;

import java.util.List;

/**
 * Generic database interface for basic database operations.
 *
 * Created by Etienne on 9-10-2016.
 */
public interface Repository<T> {

    T get(int id);

    List<T> getAll();

    T insert(T row);

    boolean delete(T row);

    void update(T row);
}
