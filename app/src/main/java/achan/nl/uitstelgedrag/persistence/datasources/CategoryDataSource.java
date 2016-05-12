package achan.nl.uitstelgedrag.persistence.datasources;

import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import achan.nl.uitstelgedrag.models.Category;
import achan.nl.uitstelgedrag.persistence.definitions.ColumnDefinition;
import achan.nl.uitstelgedrag.persistence.definitions.TableDefinition;

/**
 * Created by Etienne on 29-4-2016.
 */
public class CategoryDataSource implements DataSource<Category> {

    public static final ColumnDefinition CATEGORY_ID    = new ColumnDefinition("id", "INTEGER", "PRIMARY KEY AUTOINCREMENT");
    public static final ColumnDefinition CATEGORY_TITLE = new ColumnDefinition("title", "TEXT", "NOT NULL");
    public static final TableDefinition  CATEGORIES     = new TableDefinition("Categories", CATEGORY_ID, CATEGORY_TITLE);

    SQLiteDatabase database;

    public CategoryDataSource(SQLiteDatabase database) {
        this.database = database;
    }

    @Override
    public Category get(int id) {
        return null;
    }

    @Override
    public List<Category> getAll() {
        return null;
    }

    @Override
    public Category insert(Category object) {
        return null;
    }

    @Override
    public boolean delete(int id) {
        return false;
    }

    @Override
    public void update(Category row) {

    }
}
