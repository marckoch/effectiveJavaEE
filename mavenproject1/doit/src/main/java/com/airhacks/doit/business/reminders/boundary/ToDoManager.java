package com.airhacks.doit.business.reminders.boundary;

import com.airhacks.doit.business.logging.boundary.BoundaryLogger;
import com.airhacks.doit.business.reminders.entity.ToDo;
import java.util.List;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

@Stateless
@Interceptors(BoundaryLogger.class)
public class ToDoManager {

    @PersistenceContext
    EntityManager em;

    public ToDoManager() {
    }

    public ToDo findById(long id) {
        return em.find(ToDo.class, id);
    }

    public void delete(long id) {
        try {
            ToDo reference = em.getReference(ToDo.class, id);
            em.remove(reference);
        } catch (EntityNotFoundException enfex) {
            // ignore, entity was already deleted
        }
    }

    public List<ToDo> findAll() {
        return em.createNamedQuery(ToDo.findAll, ToDo.class).getResultList();
    }

    public ToDo save(ToDo todo) {
        return em.merge(todo);
    }

    public ToDo updateStatus(long id, boolean done) {
        ToDo todo = this.findById(id);
        if (todo == null) {
            return null;
        }
        todo.setDone(done);
        return todo;
    }
}
