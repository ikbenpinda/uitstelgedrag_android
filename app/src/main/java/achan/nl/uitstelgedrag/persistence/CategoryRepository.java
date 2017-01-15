package achan.nl.uitstelgedrag.persistence;

import java.util.List;

import achan.nl.uitstelgedrag.domain.models.Category;

/**
 * Created by Etienne on 30-5-2016.
 */
public interface CategoryRepository {

    Category get(int id);
    List<Category> getAll();
    Category insert(Category category);
    boolean delete(Category category);
    Category update(Category category);
}
