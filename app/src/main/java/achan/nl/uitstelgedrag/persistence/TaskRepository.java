package achan.nl.uitstelgedrag.persistence;

import java.util.List;

import achan.nl.uitstelgedrag.domain.models.Task;

/**
 * Created by Etienne on 30-5-2016.
 */
public interface TaskRepository {

    Task get(int id);
    List<Task> getAll();
    Task insert(Task task);
    boolean delete(Task task);
    void update(Task task);
}
