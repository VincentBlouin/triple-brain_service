/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources;

import guru.bubl.module.model.User;
import guru.bubl.module.model.json.UserJson;
import guru.bubl.module.repository.user.NonExistingUserException;
import guru.bubl.module.repository.user.UserRepository;
import guru.bubl.service.SecurityInterceptor;
import guru.bubl.service.SessionHandler;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserSessionResource {

    @Inject
    UserRepository userRepository;

    @Inject
    SessionHandler sessionHandler;

    @GET
    @Path("/")
    public Response get(
            @Context HttpServletRequest request,
            @CookieParam(SessionHandler.PERSISTENT_SESSION) String persistentSessionId
    ) {
        if (!sessionHandler.isUserInSession(request.getSession(), persistentSessionId)) {
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        }
        return Response.ok(
                UserJson.toJson(
                        sessionHandler.userFromSession(request.getSession())
                )
        ).build();
    }

    @POST
    @Produces(MediaType.WILDCARD)
    @Path("/")
    public Response authenticate(
            JSONObject loginInfo,
            @Context HttpServletRequest request,
            @CookieParam(SessionHandler.PERSISTENT_SESSION) String persistentSessionId
    ) {
        if (sessionHandler.isUserInSession(request.getSession(), persistentSessionId)) {
            return Response.status(
                    Response.Status.CONFLICT.getStatusCode()
            ).build();
        }
        try {
            User user = userRepository.findByEmail(
                    loginInfo.getString(UserJson.EMAIL).toLowerCase().trim()
            );
            if (user.hasPassword(
                    loginInfo.getString(UserJson.PASSWORD)
            )) {
                authenticateUserInSession(user, request.getSession());
                Response.ResponseBuilder response = Response.ok(
                        UserJson.toJson(user)
                );
                if (loginInfo.optBoolean("staySignedIn")) {
                    response.cookie(sessionHandler.persistSessionForUser(
                            request.getSession(),
                            user
                    ));
                } else {
                    if (sessionHandler.isUserInSession(request.getSession(), persistentSessionId)) {
                        sessionHandler.removePersistentSession(
                                persistentSessionId
                        );
                    }

                }
                return response.build();
            }
        } catch (NonExistingUserException e) {
            return Response.status(Response.Status.UNAUTHORIZED.getStatusCode()).build();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return Response.status(Response.Status.UNAUTHORIZED.getStatusCode()).build();
    }

    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/")
    public Response logout(
            @CookieParam(SessionHandler.PERSISTENT_SESSION) String persistentSessionId,
            @Context HttpServletRequest request
    ) {
        sessionHandler.removePersistentSession(persistentSessionId);
        request.getSession().setAttribute(SecurityInterceptor.AUTHENTICATION_ATTRIBUTE_KEY, false);
        request.getSession().setAttribute(SecurityInterceptor.AUTHENTICATED_USER_KEY, null);
        return Response.ok().build();
    }

    public static void authenticateUserInSession(User user, HttpSession session) {
        session.setAttribute(SecurityInterceptor.AUTHENTICATION_ATTRIBUTE_KEY, true);
        session.setAttribute(SecurityInterceptor.AUTHENTICATED_USER_KEY, user);
    }
}
