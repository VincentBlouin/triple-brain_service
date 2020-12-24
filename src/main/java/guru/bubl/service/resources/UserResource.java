/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources;

import com.google.inject.Injector;
import com.google.inject.name.Named;
import guru.bubl.module.common_utils.NoEx;
import guru.bubl.module.model.User;
import guru.bubl.module.model.UserUris;
import guru.bubl.module.model.friend.FriendManagerFactory;
import guru.bubl.module.model.friend.FriendStatus;
import guru.bubl.module.model.graph.GraphFactory;
import guru.bubl.module.model.graph.relation.RelationFactory;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.json.JsonUtils;
import guru.bubl.module.model.json.UserJson;
import guru.bubl.module.repository.user.UserRepository;
import guru.bubl.service.SessionHandler;
import guru.bubl.service.recaptcha.Recaptcha;
import guru.bubl.service.recaptcha.RecaptchaResult;
import guru.bubl.service.resources.center.CenterGraphElementsResource;
import guru.bubl.service.resources.center.CenterGraphElementsResourceFactory;
import guru.bubl.service.resources.friend.FriendsResource;
import guru.bubl.service.resources.friend.FriendsResourceFactory;
import guru.bubl.service.resources.notification.NotificationResource;
import guru.bubl.service.resources.notification.NotificationResourceFactory;
import guru.bubl.service.resources.pattern.PatternConsumerResource;
import guru.bubl.service.resources.pattern.PatternConsumerResourceFactory;
import guru.bubl.service.resources.tree_copier.TreeCopierResource;
import guru.bubl.service.resources.tree_copier.TreeCopierResourceFactory;
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
    private Injector injector;

    @Context
    HttpServletRequest request;

    @Inject
    RelationFactory relationFactory;

    @Inject
    FriendsResourceFactory friendsResourceFactory;

    @Inject
    FriendManagerFactory friendManagerFactory;

    @Inject
    @Named("AppUrl")
    String appUrl;

    @Inject
    Recaptcha recaptcha;

    @Inject
    NotificationResourceFactory notificationResourceFactory;

    @Inject
    private TreeCopierResourceFactory treeCopierResourceFactory;

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


    @Path("{username}/non_owned/{type}/{shortId}/surround_graph")
    public NotOwnedSurroundGraphResource surroundGraphResource(
            @PathParam("username") String username,
            @PathParam("type") String type,
            @PathParam("shortId") String shortId,
            @CookieParam(SessionHandler.PERSISTENT_SESSION) String persistentSessionId
    ) {
        URI centerUri = type.toLowerCase().equals("edge") ? relationFactory.withUri(
                new UserUris(
                        username
                ).edgeUriFromShortId(
                        shortId
                )
        ).sourceUri() : new UserUris(
                username
        ).uriFromTypeStringAndShortId(type, shortId);
        return getVertexSurroundGraphResource(
                centerUri,
                persistentSessionId
        );
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

    @Path("{username}/tree_copy")
    public TreeCopierResource copierResource(
            @PathParam("username") String username,
            @CookieParam(SessionHandler.PERSISTENT_SESSION) String persistentSessionId
    ) {
        if (isUserNameTheOneInSession(username, persistentSessionId)) {
            return treeCopierResourceFactory.forCopier(
                    sessionHandler.userFromSession(request.getSession())
            );
        }
        throw new WebApplicationException(Response.Status.FORBIDDEN);
    }

    private NotOwnedSurroundGraphResource getVertexSurroundGraphResource(
            URI uri, String persistentSessionId
    ) {

        String ownerUsername = UserUris.ownerUserNameFromUri(uri);
        User owner = User.withUsername(ownerUsername);
        UserGraph userGraph = graphFactory.loadForUser(
                owner
        );
        Boolean skipVerification = false;
        Boolean isFriend = false;
        if (sessionHandler.isUserInSession(request.getSession(), persistentSessionId)) {
            User userInSession = sessionHandler.userFromSession(request.getSession());
            skipVerification = userInSession.username().equals(
                    ownerUsername
            ) && UserSessionResource.isRightXsrfToken(request);
            isFriend = FriendStatus.confirmed == friendManagerFactory.forUser(
                    userInSession
            ).getStatusWithUser(owner);
        }
        return new NotOwnedSurroundGraphResource(
                userGraph,
                uri,
                skipVerification,
                isFriend
        );
    }

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

    @Path("{username}/notification")
    public NotificationResource getNotificationResource(
            @PathParam("username") String username,
            @CookieParam(SessionHandler.PERSISTENT_SESSION) String persistentSessionId
    ) {
        if (!isUserNameTheOneInSession(username, persistentSessionId)) {
            throw new WebApplicationException(
                    Response.Status.FORBIDDEN
            );
        }
        return notificationResourceFactory.ofUser(
                sessionHandler.userFromSession(request.getSession())
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
        RecaptchaResult recaptchaResult = recaptcha.getResult(
                jsonUser
        );
        if (!recaptchaResult.isOk()) {
            return Response.status(
                    Response.Status.UNAUTHORIZED.getStatusCode()
            ).entity(new JSONObject().put(
                    "reason",
                    (recaptchaResult.isSuccess() ? "recaptcha score" : "problem connecting with recaptcha")
            )).build();
        }
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
                user,
                request.getSession(),
                request.getHeader(SessionHandler.X_XSRF_TOKEN)
        );
        Response.ResponseBuilder responseBuilder = Response.created(URI.create(
                user.username()
        ));
        if (jsonUser.optBoolean("staySignedIn")) {
            responseBuilder.cookie(
                    sessionHandler.persistSessionForUser(
                            request.getSession(),
                            user,
                            request.getHeader(SessionHandler.X_XSRF_TOKEN)
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

    @PUT
    @Path("{username}/locale")
    public Response updateLocale(
            @PathParam("username") String username,
            String locales,
            @CookieParam(SessionHandler.PERSISTENT_SESSION) String persistentSessionId
    ) {
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
        return authenticatedUser.username().equals(userName) && UserSessionResource.isRightXsrfToken(request);
    }

}
