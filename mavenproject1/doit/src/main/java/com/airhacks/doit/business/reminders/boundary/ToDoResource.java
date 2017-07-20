package com.airhacks.doit.business.reminders.boundary;

import com.airhacks.doit.business.reminders.entity.ToDo;
import javax.json.JsonObject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

public class ToDoResource {
    long id;
    
    ToDoManager manager;

    public ToDoResource(long id, ToDoManager manager) {
        this.id = id;
        this.manager = manager;
    }
    
    @GET
    public ToDo find() {
        return manager.findById(id);
    }
    
    @PUT
    public void update(ToDo todo) {
        todo.setId(id);
        manager.save(todo);
    }

    @DELETE
    public void delete() {
        try {
            manager.delete(id);
        } catch (EntityNotFoundException enfex) {
            // ignore, entity was already deleted
            System.out.println("caught " + enfex);
        }
    }
    
    @PUT
    @Path("/status")
    public Response statusUpdate(JsonObject statusUpdate) {
        if (!statusUpdate.containsKey("done")) {
            return Response.status(Response.Status.BAD_REQUEST).header("reason", "JSON should contain field 'done'!").build();
        }

        boolean done = statusUpdate.getBoolean("done");
        ToDo todo = manager.updateStatus(id, done);
        if (todo == null) {
            return Response.status(Response.Status.BAD_REQUEST).header("reason", "todo with id " + id + "not found!").build();
        } else {
            return Response.ok(todo).build();
        }
    }
}
