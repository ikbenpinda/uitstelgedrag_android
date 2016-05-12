package achan.nl.uitstelgedrag.persistence.datasources;

import java.util.List;

/**
 * Generic interface for DataSources.
 * a DataSource typically knows about all the queries related to a specific object.
 * These queries are then executed on the database.
 *
 * Whether the DataSource knows about the database; do what works for you.
 *
 * Created by Etienne on 17-4-2016.
 */
public interface DataSource<T> {

    /**
     * Returns a single row with given id.
     */
    T get(int id);

    /**
     * Returns all data/all rows.
     */
    List<T> getAll();

    /**
     * Inserts a given object into the database, and after insertion
     * returns it with the auto-generated primary key.
     * @return The same object, but now with the primary key as defined by the database.
     */
    T insert(T object);

    /**
     * Updates a given row.
     */
    void update(T row);

    /**
     * Deletes a row with given id.
     */
    boolean delete(int id);
}
