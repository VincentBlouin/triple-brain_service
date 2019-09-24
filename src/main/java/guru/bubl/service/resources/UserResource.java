/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources;

import com.google.inject.Injector;
import com.google.inject.name.Named;
import guru.bubl.module.common_utils.NoEx;
import guru.bubl.module.model.User;
import guru.bubl.module.model.UserUris;
import guru.bubl.module.model.friend.FriendManager;
import guru.bubl.module.model.friend.FriendManagerFactory;
import guru.bubl.module.model.friend.FriendStatus;
import guru.bubl.module.model.friend.friend_confirmation_email.FriendConfirmationEmail;
import guru.bubl.module.model.graph.GraphFactory;
import guru.bubl.module.model.graph.edge.Edge;
import guru.bubl.module.model.graph.edge.EdgeFactory;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.graph.vertex.Vertex;
import guru.bubl.module.model.graph.vertex.VertexFactory;
import guru.bubl.module.model.json.JsonUtils;
import guru.bubl.module.model.json.UserJson;
import guru.bubl.module.neo4j_graph_manipulator.graph.graph.UserGraphFactoryNeo4j;
import guru.bubl.module.repository.user.UserRepository;
import guru.bubl.service.SessionHandler;
import guru.bubl.service.resources.center.CenterGraphElementsResource;
import guru.bubl.service.resources.center.CenterGraphElementsResourceFactory;
import guru.bubl.service.resources.center.PublicCenterGraphElementsResource;
import guru.bubl.service.resources.center.PublicCenterGraphElementsResourceFactory;
import guru.bubl.service.resources.fork.ForkResource;
import guru.bubl.service.resources.fork.ForkResourceFactory;
import guru.bubl.service.resources.friend.FriendsResource;
import guru.bubl.service.resources.friend.FriendsResourceFactory;
import guru.bubl.service.resources.meta.UserMetasResource;
import guru.bubl.service.resources.meta.UserMetasResourceFactory;
import guru.bubl.service.resources.pattern.PatternConsumerResource;
import guru.bubl.service.resources.pattern.PatternConsumerResourceFactory;
import guru.bubl.service.resources.vertex.NotOwnedSurroundGraphResource;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import java.util.Map;

import static guru.bubl.module.model.json.UserJson.*;
import static guru.bubl.module.model.validator.UserValidator.*;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Singleton
public class UserResource {

    @Inject
    UserRepository userRepository;

    @Inject
    protected UserGraphFactoryNeo4j neo4jUserGraphFactory;

    @Inject
    SessionHandler sessionHandler;

    @Inject
    private GraphFactory graphFactory;

    @Inject
    GraphResourceFactory graphResourceFactory;

    @Inject
    PatternConsumerResourceFactory patternConsumerResourceFactory;

    @Inject
    SearchResourceFactory searchResourceFactory;

    @Inject
    CenterGraphElementsResourceFactory centerGraphElementsResourceFactory;

    @Inject
    PublicCenterGraphElementsResourceFactory publicCenterGraphElementsResourceFactory;

    @Inject
    private Injector injector;

    @Inject
    ForkResourceFactory forkResourceFactory;

    @Context
    HttpServletRequest request;

    @Inject
    VertexFactory vertexFactory;

    @Inject
    EdgeFactory edgeFactory;

    @Inject
    UserMetasResourceFactory userMetasResourceFactory;

    @Inject
    FriendsResourceFactory friendsResourceFactory;

    @Inject
    FriendManagerFactory friendManagerFactory;

    @Inject
    FriendConfirmationEmail friendConfirmationEmail;

    @Inject
    @Named("AppUrl")
    String appUrl;

    @POST
    @Path("{username}/search-users")
    public Response searchUsers(
            JSONObject search,
            @PathParam("username") String username,
            @CookieParam(SessionHandler.PERSISTENT_SESSION) String persistentSessionId
    ) {
        if (isUserNameTheOneInSession(username, persistentSessionId)) {
            List<User> searchResults = userRepository.searchUsers(
                    search.optString("searchText"),
                    sessionHandler.userFromSession(request.getSession())
            );
            return Response.ok(
                    JsonUtils.getGson().toJson(searchResults)
            ).build();
        }
        throw new WebApplicationException(Response.Status.FORBIDDEN);
    }

    @Path("{username}/graph")
    public GraphResource graphResource(
            @PathParam("username") String username,
            @CookieParam(SessionHandler.PERSISTENT_SESSION) String persistentSessionId
    ) {
        if (isUserNameTheOneInSession(username, persistentSessionId)) {
            return graphResourceFactory.withUser(
                    sessionHandler.userFromSession(request.getSession())
            );
        }
        throw new WebApplicationException(Response.Status.FORBIDDEN);
    }

    @Path("{username}/patterns")
    public PatternConsumerResource getPatternConsumerResource(
            @PathParam("username") String username,
            @CookieParam(SessionHandler.PERSISTENT_SESSION) String persistentSessionId
    ) {
        if (isUserNameTheOneInSession(username, persistentSessionId)) {
            return patternConsumerResourceFactory.withUser(
                    sessionHandler.userFromSession(request.getSession())
            );
        }
        throw new WebApplicationException(Response.Status.FORBIDDEN);
    }


    @Path("{username}/non_owned/vertex/{shortId}/surround_graph")
    public NotOwnedSurroundGraphResource surroundGraphResource(
            @PathParam("username") String username,
            @PathParam("shortId") String shortId,
            @CookieParam(SessionHandler.PERSISTENT_SESSION) String persistentSessionId
    ) {
        return getVertexSurroundGraphResource(
                vertexFactory.withUri(
                        new UserUris(
                                username
                        ).vertexUriFromShortId(shortId)
                ),
                persistentSessionId
        );
    }

    @Path("{username}/non_owned/edge/{shortId}/surround_graph")
    public NotOwnedSurroundGraphResource surroundEdgeGraphResource(
            @PathParam("username") String username,
            @PathParam("shortId") String shortId,
            @CookieParam(SessionHandler.PERSISTENT_SESSION) String persistentSessionId
    ) {
        Edge edge = edgeFactory.withUri(
                new UserUris(
                        username
                ).edgeUriFromShortId(
                        shortId
                )
        );
        return getVertexSurroundGraphResource(
                edge.sourceVertex(),
                persistentSessionId
        );

    }

    @POST
    @Path("/confirm-friendship-with-token")
    public Response confirmFriendship(
            JSONObject confirmation,
            @Context HttpServletRequest request,
            @CookieParam(SessionHandler.PERSISTENT_SESSION) String persistentSessionId
    ) {
        String requestUsername = confirmation.optString("requestUsername");
        String destinationUsername = confirmation.optString("destinationUsername");
        User destinationUser = userRepository.findByUsername(destinationUsername);
        User requestUser = userRepository.findByUsername(requestUsername);
        FriendManager friendManager = friendManagerFactory.forUser(destinationUser);
        FriendStatus friendStatus = friendManager.getStatusWithUser(requestUser);
        if (friendStatus != FriendStatus.waitingForYourAnswer) {
            return Response.status(Response.Status.NO_CONTENT).build();
        }
        String confirmToken = confirmation.optString("confirmToken");
        Boolean hasConfirmed = friendManager.confirmWithToken(requestUser, confirmToken);
        if (!hasConfirmed) {
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        }
        if (!sessionHandler.isUserInSession(request.getSession(), persistentSessionId)) {
            UserSessionResource.authenticateUserInSession(
                    destinationUser,
                    request.getSession()
            );
        }
        friendConfirmationEmail.sendForUserToUser(
                destinationUser,
                requestUser,
                appUrl + "/user/" + destinationUser.username()
        );
        return Response.ok(
                UserJson.toJson(destinationUser)
        ).build();
    }

    @Path("{username}/friends")
    public FriendsResource friendsResource(
            @PathParam("username") String username,
            @CookieParam(SessionHandler.PERSISTENT_SESSION) String persistentSessionId
    ) {
        if (isUserNameTheOneInSession(username, persistentSessionId)) {
            return friendsResourceFactory.forUser(
                    sessionHandler.userFromSession(request.getSession())
            );
        }
        throw new WebApplicationException(Response.Status.FORBIDDEN);
    }

    private NotOwnedSurroundGraphResource getVertexSurroundGraphResource(
            Vertex centerVertex, String persistentSessionId
    ) {
        User owner = userRepository.findByUsername(centerVertex.getOwnerUsername());
        UserGraph userGraph = graphFactory.loadForUser(
                owner
        );
        if (!userGraph.haveElementWithId(centerVertex.uri())) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        Boolean skipVerification = false;
        Boolean isFriend = false;
        if (sessionHandler.isUserInSession(request.getSession(), persistentSessionId)) {
            User userInSession = sessionHandler.userFromSession(request.getSession());
            skipVerification = userInSession.username().equals(
                    centerVertex.getOwnerUsername()
            );
            isFriend = FriendStatus.confirmed == friendManagerFactory.forUser(
                    userInSession
            ).getStatusWithUser(owner);
        }
        return new NotOwnedSurroundGraphResource(
                userGraph,
                centerVertex,
                skipVerification,
                isFriend
        );
    }

//    @Path("{username}/non_owned/schema")
//    public SchemaNonOwnedResource schemaNonOwnedResource(
//            @PathParam("username") String username
//    ) {
//        return schemaNonOwnedResourceFactory.fromUserGraph(
//                graphFactory.loadForUser(
//                        userRepository.findByUsername(username)
//                )
//        );
//    }

    @Path("{username}/search")
    public SearchResource searchResource(
            @PathParam("username") String username,
            @CookieParam(SessionHandler.PERSISTENT_SESSION) String persistentSessionId
    ) {
        if (isUserNameTheOneInSession(username, persistentSessionId)) {
            return searchResourceFactory.withUser(
                    sessionHandler.userFromSession(request.getSession())
            );
        }
        throw new WebApplicationException(Response.Status.FORBIDDEN);
    }


    @Path("{username}/admin")
    public AdminResource adminResource(
            @PathParam("username") String username,
            @CookieParam(SessionHandler.PERSISTENT_SESSION) String persistentSessionId
    ) {
        if (isUserNameTheOneInSession(username, persistentSessionId) && username.equals("vince")) {
            return injector.getInstance(
                    AdminResource.class
            );
        }
        throw new WebApplicationException(
                Response.Status.FORBIDDEN
        );
    }

    @Path("{username}/center-elements")
    public CenterGraphElementsResource getCenterGraphElementsResource(
            @PathParam("username") String username,
            @CookieParam(SessionHandler.PERSISTENT_SESSION) String persistentSessionId
    ) {
        if (!isUserNameTheOneInSession(username, persistentSessionId)) {
            throw new WebApplicationException(
                    Response.Status.FORBIDDEN
            );
        }
        return centerGraphElementsResourceFactory.forUser(
                sessionHandler.userFromSession(request.getSession())
        );
    }

    @Path("{username}/fork")
    public ForkResource getForkResource(
            @PathParam("username") String username,
            @CookieParam(SessionHandler.PERSISTENT_SESSION) String persistentSessionId
    ) {
        if (!isUserNameTheOneInSession(username, persistentSessionId)) {
            throw new WebApplicationException(
                    Response.Status.FORBIDDEN
            );
        }
        return forkResourceFactory.forUser(
                sessionHandler.userFromSession(request.getSession())
        );
    }

    @Path("{username}/center-elements/public")
    public PublicCenterGraphElementsResource getPublicCenterGraphElementsResource(
            @PathParam("username") String username
    ) {
        return publicCenterGraphElementsResourceFactory.forUser(
                User.withUsername(username)
        );
    }

    @Path("session")
    public UserSessionResource sessionResource() {
        return injector.getInstance(
                UserSessionResource.class
        );
    }

    @Path("password")
    public UserPasswordResource getPasswordResource() {
        return injector.getInstance(
                UserPasswordResource.class
        );
    }

    @POST
    @Produces(MediaType.WILDCARD)
    @Path("/")
    public Response createUser(JSONObject jsonUser) throws JSONException {
        User user = User.withEmailAndUsername(
                jsonUser.optString(EMAIL, ""),
                jsonUser.optString(USER_NAME, "")
        ).password(
                jsonUser.optString(PASSWORD, "")
        );

        JSONArray jsonMessages = new JSONArray();
        Map<String, String> errors = errorsForUserAsJson(jsonUser);
        if (errors.isEmpty()) {
            if (userRepository.emailExists(user.email())) {
                errors.put(EMAIL, ALREADY_REGISTERED_EMAIL);
            }
            if (userRepository.usernameExists(user.username())) {
                errors.put(USER_NAME, USER_NAME_ALREADY_REGISTERED);
            }
        }
        if (!errors.isEmpty()) {
            for (Map.Entry<String, String> entry : errors.entrySet()) {
                try {
                    jsonMessages.put(new JSONObject().put(
                            "field", entry.getKey()
                    ).put(
                            "reason", entry.getValue()
                    ));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            throw new WebApplicationException(Response
                    .status(BAD_REQUEST)
                    .entity(jsonMessages)
                    .build()
            );
        }
        if (jsonUser.has(UserJson.PREFERRED_LOCALES)) {
            user.setPreferredLocales(
                    jsonUser.getString(UserJson.PREFERRED_LOCALES)
            );
        }
        user = userRepository.createUser(user);
        UserSessionResource.authenticateUserInSession(
                user, request.getSession()
        );
        Response.ResponseBuilder responseBuilder = Response.created(URI.create(
                user.username()
        ));
        if (jsonUser.optBoolean("staySignedIn")) {
            responseBuilder.cookie(
                    sessionHandler.persistSessionForUser(
                            request.getSession(),
                            user
                    )
            );
        }
        return responseBuilder.entity(UserJson.toJson(user)).build();
    }

    @GET
    @Path("/is_authenticated")
    public Response isAuthenticated(
            @Context HttpServletRequest request,
            @CookieParam(SessionHandler.PERSISTENT_SESSION) String persistentSessionId
    ) {
        return NoEx.wrap(() ->
                Response.ok(new JSONObject()
                        .put("is_authenticated", sessionHandler.isUserInSession(request.getSession(), persistentSessionId))
                ).build()
        ).get();
    }

    @Path("{username}/metas")
    public UserMetasResource getUserMetas(
            @PathParam("username") String username,
            @CookieParam(SessionHandler.PERSISTENT_SESSION) String persistentSessionId,
            @Context HttpServletRequest request
    ) {
        if (!isUserNameTheOneInSession(username, persistentSessionId)) {
            throw new WebApplicationException(
                    Response.Status.FORBIDDEN
            );
        }
        return userMetasResourceFactory.forUser(
                sessionHandler.userFromSession(request.getSession())
        );
    }

    @PUT
    @Path("{username}/locale")
    public Response updateLocale(
            @PathParam("username") String username,
            String locales,
            @CookieParam(SessionHandler.PERSISTENT_SESSION) String persistentSessionId
    ) throws JSONException {
        if (!isUserNameTheOneInSession(username, persistentSessionId)) {
            throw new WebApplicationException(
                    Response.Status.FORBIDDEN
            );
        }
        User authenticatedUser = sessionHandler.userFromSession(request.getSession());
        authenticatedUser.setPreferredLocales(locales);
        userRepository.updatePreferredLocales(
                authenticatedUser
        );
        return Response.noContent().build();
    }

    private Boolean isUserNameTheOneInSession(String userName, String persistentSessionId) {
        if (!sessionHandler.isUserInSession(request.getSession(), persistentSessionId)) {
            return false;
        }
        User authenticatedUser = sessionHandler.userFromSession(request.getSession());
        return authenticatedUser.username().equals(userName);
    }

}
