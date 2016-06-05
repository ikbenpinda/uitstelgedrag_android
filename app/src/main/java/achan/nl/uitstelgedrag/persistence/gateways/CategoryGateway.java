package achan.nl.uitstelgedrag.persistence.gateways;

import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import achan.nl.uitstelgedrag.domain.models.Category;
import achan.nl.uitstelgedrag.persistence.CategoryRepository;

/**
 * Created by Etienne on 29-4-2016.
 */
public class CategoryGateway implements CategoryRepository {

    SQLiteDatabase database;

    public CategoryGateway(SQLiteDatabase database) {
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
    public Category insert(Category category) {
        return null;
    }

    @Override
    public boolean delete(Category category) {
        return false;
    }

    @Override
    public Category update(Category category) {
        return null;
    }
}
