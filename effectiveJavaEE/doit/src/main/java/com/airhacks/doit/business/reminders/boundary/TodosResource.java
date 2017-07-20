package com.airhacks.doit.business.reminders.boundary;

import com.airhacks.doit.business.reminders.entity.ToDo;
import java.net.URI;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author Koch
 */
@Stateless
@Path("todos")
public class TodosResource {

    @Inject
    ToDoManager manager;

    public TodosResource() {
    }

    @Path("{id}")
    public ToDoResource find(@PathParam("id") long id) {
        return new ToDoResource(id, manager);
    }

    @GET
    public List<ToDo> all() {
        return manager.findAll();
    }

    @POST
    public Response save(@Valid ToDo todo, @Context UriInfo info) {
//        try {
            ToDo saved = manager.save(todo);
            long id = saved.getId();
            URI uri = info.getAbsolutePathBuilder().path("/" + id).build();
            return Response.created(uri).build();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            return Response.serverError().build();
//        }
    }

}
