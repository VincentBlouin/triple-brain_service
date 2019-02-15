/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.friend;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.google.inject.name.Named;
import guru.bubl.module.common_utils.NoEx;
import guru.bubl.module.model.User;
import guru.bubl.module.model.friend.FriendManager;
import guru.bubl.module.model.friend.FriendManagerFactory;
import guru.bubl.module.model.friend.FriendStatus;
import guru.bubl.module.model.friend.friend_confirmation_email.FriendConfirmationEmail;
import guru.bubl.module.model.friend.friend_request_email.FriendRequestEmail;
import guru.bubl.module.model.graph.GraphTransactional;
import guru.bubl.module.model.json.JsonUtils;
import guru.bubl.module.repository.user.UserRepository;
import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FriendsResource {

    @Inject
    UserRepository userRepository;

    @Inject
    FriendRequestEmail friendRequestEmail;

    @Inject
    FriendConfirmationEmail friendConfirmationEmail;

    @javax.inject.Inject
    @Named("AppUrl")
    String appUrl;

    private User authenticatedUser;

    private FriendManager friendManager;

    @AssistedInject
    public FriendsResource(
            FriendManagerFactory friendManagerFactory,
            @Assisted User user
    ) {
        this.authenticatedUser = user;
        this.friendManager = friendManagerFactory.forUser(
                user
        );
    }

    @GET
    @Path("/")
    @GraphTransactional
    public Response list() {
        return Response.ok(
                JsonUtils.getGson().toJson(
                        friendManager.list()
                )
        ).build();
    }

    @POST
    @Path("/")
    @GraphTransactional
    public Response addFriend(JSONObject friendJson) {
        String friendUsername = friendJson.optString("friendUsername");
        User destinationUser = userRepository.findByUsername(friendUsername);
        FriendStatus friendStatusBefore = friendManager.getStatusWithUser(destinationUser);
        String confirmToken = friendManager.add(destinationUser);
        if (confirmToken != null) {
            String confirmUrl = appUrl + "/user/" + authenticatedUser.username() +
                    "/requestUser=" + authenticatedUser.username() +
                    "/destinationUser=" + friendUsername +
                    "/confirm-token=" + confirmToken;
            friendRequestEmail.sendToUserFromUser(destinationUser, authenticatedUser, confirmUrl);
        }
        FriendStatus friendStatusAfter = friendManager.getStatusWithUser(destinationUser);
        if (friendStatusBefore == FriendStatus.waitingForYourAnswer && friendStatusAfter == FriendStatus.confirmed) {
            User requestUser = destinationUser;
            friendConfirmationEmail.sendForUserToUser(
                    authenticatedUser,
                    requestUser,
                    appUrl + "/user/" + destinationUser.username()
            );
        }
        return NoEx.wrap(() -> Response.ok(
                new JSONObject().put(
                        "status",
                        friendStatusAfter.name()
                )
        ).build()).get();
    }

    @GET
    @Path("/{other-username}/status")
    @GraphTransactional
    public Response getStatusWithFriend(@PathParam("other-username") String otherUsername) {
        User otherUser = userRepository.findByUsername(otherUsername);
        return NoEx.wrap(() -> {
            JSONObject status = new JSONObject().put(
                    "status",
                    friendManager.getStatusWithUser(otherUser).name()
            );
            return Response.ok(status).build();
        }).get();
    }
}
