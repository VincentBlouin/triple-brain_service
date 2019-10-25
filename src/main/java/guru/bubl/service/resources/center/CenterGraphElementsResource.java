/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.center;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import guru.bubl.module.model.User;
import guru.bubl.module.model.center_graph_element.CenterGraphElementOperator;
import guru.bubl.module.model.center_graph_element.CenterGraphElementsOperatorFactory;
import guru.bubl.module.model.center_graph_element.CenteredGraphElementsOperator;
import guru.bubl.module.model.friend.FriendManager;
import guru.bubl.module.model.friend.FriendManagerFactory;
import guru.bubl.module.model.friend.FriendStatus;
import guru.bubl.module.model.json.CenterGraphElementsJson;
import guru.bubl.module.repository.user.UserRepository;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CenterGraphElementsResource {

    private User user;

    private CenterGraphElementsOperatorFactory centerGraphElementsOperatorFactory;
    private UserRepository userRepository;
    private FriendManagerFactory friendManagerFactory;

    @AssistedInject
    CenterGraphElementsResource(
            CenterGraphElementsOperatorFactory centerGraphElementsOperatorFactory,
            UserRepository userRepository,
            FriendManagerFactory friendManagerFactory,
            @Assisted User user
    ) {
        this.centerGraphElementsOperatorFactory = centerGraphElementsOperatorFactory;
        this.userRepository = userRepository;
        this.friendManagerFactory = friendManagerFactory;
        this.user = user;
    }

    @GET
    public Response getPublicAndPrivateForOwner() {
        return this.getPublicAndPrivateForOwnerAtSkip(null);
    }

    @GET
    @Path("/skip/{nbSkip}")
    public Response getPublicAndPrivateForOwnerAtSkip(@PathParam("nbSkip") Integer nbSkip) {
        return Response.ok().entity(
                CenterGraphElementsJson.toJson(
                        getFromNbSkip(nbSkip).getPublicAndPrivateForOwner(user)
                )
        ).build();
    }

    @GET
    @Path("/pattern")
    public Response getPatterns() {
        return this.getPatternsAtSkip(null);
    }

    @GET
    @Path("/pattern/skip/{nbSkip}")
    public Response getPatternsAtSkip(@PathParam("nbSkip") Integer nbSkip) {
        return Response.ok().entity(
                CenterGraphElementsJson.toJson(
                        getFromNbSkip(nbSkip).getAllPatterns()
                )
        ).build();
    }

    @GET
    @Path("/friend")
    public Response getOfFriends() {
        return this.getOfFriendsAtSkip(null);
    }

    @GET
    @Path("/friend/skip/{nbSkip}")
    public Response getOfFriendsAtSkip(@PathParam("nbSkip") Integer nbSkip) {
        return Response.ok().entity(
                CenterGraphElementsJson.toJson(
                        getFromNbSkip(nbSkip).getFriendsFeedForUser(user)
                )
        ).build();
    }

    @GET
    @Path("/friend/{friendUsername}")
    public Response getForSpecificFriend(@PathParam("friendUsername") String friendUsername) {
        return this.getForSpecificFriendAtSkip(friendUsername, null);
    }

    @GET
    @Path("/friend/{friendUsername}/skip/{nbSkip}")
    public Response getForSpecificFriendAtSkip(@PathParam("friendUsername") String friendUsername, @PathParam("nbSkip") Integer nbSkip) {
        User friend = User.withUsername(friendUsername);
        Boolean isFriend = FriendStatus.confirmed == friendManagerFactory.forUser(
                user
        ).getStatusWithUser(friend);
        if (!isFriend) {
            throw new WebApplicationException(
                    Response.Status.FORBIDDEN
            );
        }
        return Response.ok().entity(
                CenterGraphElementsJson.toJson(
                        getFromNbSkip(nbSkip).getForAFriend(friend)
                )
        ).build();
    }

    private CenteredGraphElementsOperator getFromNbSkip(Integer nbSkip) {
        return nbSkip == null ?
                centerGraphElementsOperatorFactory.usingDefaultLimits() :
                centerGraphElementsOperatorFactory.usingLimitAndSkip(16, 0);
    }
}
