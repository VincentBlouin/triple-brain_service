package guru.bubl.service.resources.friend;

import guru.bubl.module.model.User;
import guru.bubl.module.model.friend.FriendManagerFactory;
import guru.bubl.module.model.json.JsonUtils;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/friend-list")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FriendListResource {

    @Inject
    FriendManagerFactory friendManagerFactory;

    @GET
    @Path("/user/{username}")
    public Response listForUser(@PathParam("username") String username) {
        return Response.ok(
                JsonUtils.getGson().toJson(
                        friendManagerFactory.forUser(User.withUsername(
                                username
                        )).list()
                )
        ).build();
    }
}
