package com.airhacks.doit.business.reminders.boundary;

import com.airhacks.rulz.jaxrsclient.JAXRSClientProvider;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

/**
 *
 * @author Koch
 */
public class TodoResourceIT {

    @Rule
    public JAXRSClientProvider provider = JAXRSClientProvider.buildWithURI("http://localhost:8080/doit/api/todos");

    @Test
    public void crud() {

        // POST new object
        JsonObjectBuilder todoBuilder = Json.createObjectBuilder();
        JsonObject todoToCreate = todoBuilder
                .add("caption", "implement me")
                .add("priority", 42)
                .add("description", "needed because priority is > 10")
                .build();
        Response postResp = this.provider.target().request().post(Entity.json(todoToCreate));
        Assert.assertThat(postResp.getStatus(), is(201));
        String location = postResp.getHeaderString("Location");
        System.out.println("location " + location);

        // GET with id
        JsonObject dedicatedTodo = this.provider.client()
                .target(location)
                .request(MediaType.APPLICATION_JSON)
                .get(JsonObject.class);
        System.out.println("initial json: " + dedicatedTodo);
        Assert.assertTrue(dedicatedTodo.getString("caption").contains("implement"));

        // update 
        JsonObjectBuilder updateTodoBuilder = Json.createObjectBuilder();
        JsonObject todoToUpdate = updateTodoBuilder
                .add("caption", "implemented was updated")
                //.add("version", dedicatedTodo.getInt("version"))
                .build();
        System.out.println("updateRequest " + todoToUpdate);

        Response updateResponse = this.provider.client()
                .target(location)
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(todoToUpdate));
        Assert.assertThat(updateResponse.getStatus(), is(204));

        JsonObject updatedTodo = this.provider.client().target(location)
                .request(MediaType.APPLICATION_JSON)
                .get(JsonObject.class);
        System.out.println("updated json: " + updatedTodo);
        Assert.assertTrue(updatedTodo.getString("caption").equals("implemented was updated"));

        // update again
        JsonObjectBuilder update2TodoBuilder = Json.createObjectBuilder();
        JsonObject todoToUpdate2 = update2TodoBuilder
                .add("caption", "implemented")
                .add("priority", "444")
                .build();
        System.out.println("update2Request " + todoToUpdate2);

        Response update2Response = this.provider.client()
                .target(location)
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(todoToUpdate2));
        Assert.assertThat(update2Response.getStatus(), is(409));
        String conflictInfo = update2Response.getHeaderString("cause");
        Assert.assertNotNull(conflictInfo);
        System.out.println("conflictInfo: " + conflictInfo);

        JsonObject updatedTodo2 = this.provider.client().target(location)
                .request(MediaType.APPLICATION_JSON)
                .get(JsonObject.class);
        System.out.println("updated json: " + updatedTodo2);
        Assert.assertTrue(updatedTodo2.getString("caption").equals("implemented was updated"));

        // status update
        JsonObjectBuilder statusBuilder = Json.createObjectBuilder();
        JsonObject statusUpdate = statusBuilder
                .add("done", true)
                .build();

        this.provider.client()
                .target(location)
                .path("status")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(statusUpdate));

        // verify status update
        JsonObject statusUpdatedTodo = this.provider.client()
                .target(location)
                .request(MediaType.APPLICATION_JSON)
                .get(JsonObject.class);
        System.out.println("status updated " + statusUpdatedTodo);
        Assert.assertTrue(statusUpdatedTodo.getBoolean("done"));

        // update not existing todo
        JsonObjectBuilder nonExistingStatusUpdateBuilder = Json.createObjectBuilder();
        JsonObject nonExistingStatusUpdate = nonExistingStatusUpdateBuilder
                .add("done", true)
                .build();

        Response nonExistingStatusUpdateResponse = this.provider.target()
                .path("-42")
                .path("status")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(nonExistingStatusUpdate));
        Assert.assertThat(nonExistingStatusUpdateResponse.getStatus(), is(400));
        Assert.assertFalse(nonExistingStatusUpdateResponse.getHeaderString("reason").isEmpty());

        // update malformed status
        JsonObjectBuilder malformedStatusUpdateBuilder = Json.createObjectBuilder();
        JsonObject malformedStatusUpdate = malformedStatusUpdateBuilder
                .add("something wrong", true)
                .build();

        Response malformedStatusUpdateResponse = this.provider.client()
                .target(location)
                .path("status")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(malformedStatusUpdate));
        Assert.assertThat(malformedStatusUpdateResponse.getStatus(), is(400));
        Assert.assertFalse(malformedStatusUpdateResponse.getHeaderString("reason").isEmpty());

        // GET all
        Response response = this.provider.target().request(MediaType.APPLICATION_JSON)
                .get();
        Assert.assertThat(response.getStatus(), is(200));
        JsonArray allTodos = response.readEntity(JsonArray.class);
        System.out.println("payload " + allTodos);
        Assert.assertFalse(allTodos.isEmpty());

        JsonObject todo = allTodos.getJsonObject(0);
        Assert.assertTrue(todo.getString("caption").startsWith("implement"));

        // DELETE non-existing
        Response deleteResponse = this.provider.target()
                .path("42")
                .request(MediaType.APPLICATION_JSON)
                .delete();
        Assert.assertThat(deleteResponse.getStatus(), is(204));
    }

    @Test
    public void createTodoWithoutCaption() {

        // POST new object with caption that is too short
        JsonObjectBuilder todoBuilder = Json.createObjectBuilder();
        JsonObject todoToCreate = todoBuilder
                .build();

        Response postResp = this.provider.target().request().post(Entity.json(todoToCreate));
        Assert.assertThat(postResp.getStatus(), is(400));
        postResp.getHeaders().entrySet().forEach(System.out::println);
    }

    @Test
    public void createValidTodo() {

        // POST new object with caption that is too short
        JsonObjectBuilder todoBuilder = Json.createObjectBuilder();
        JsonObject todoToCreate = todoBuilder
                .add("caption", "valid caption")
                .add("priority", 3)
                .build();

        Response postResp = this.provider.target().request().post(Entity.json(todoToCreate));
        postResp.getHeaders().entrySet().forEach(System.out::println);
        Assert.assertThat(postResp.getStatus(), is(201));
    }

    @Test
    public void createTodoWithHighPriorityWithoutDescription() {

        JsonObjectBuilder todoBuilder = Json.createObjectBuilder();
        JsonObject todoToCreate = todoBuilder
                .add("caption", "10")
                .add("priority", 12)
                .build();

        Response postResp = this.provider.target().request().post(Entity.json(todoToCreate));
        postResp.getHeaders().entrySet().forEach(System.out::println);
        
        Assert.assertThat(postResp.getStatus(), is(400));
    }
}
