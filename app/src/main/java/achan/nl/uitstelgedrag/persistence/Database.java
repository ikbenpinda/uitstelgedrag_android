package achan.nl.uitstelgedrag.persistence;

/**
 * Created by Etienne on 30-5-2016.
 */
public interface Database {

    String getDatabaseName();
    int getSchemaVersion();
}
