/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.client.apache.config.DefaultApacheHttpClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import guru.bubl.module.model.User;
import guru.bubl.module.model.json.UserJson;
import guru.bubl.module.repository.user.NonExistingUserException;
import guru.bubl.module.repository.user.UserRepository;
import guru.bubl.service.SecurityInterceptor;
import guru.bubl.service.SessionHandler;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserSessionResource {

    private final WebResource resource = Client.create(
            new DefaultApacheHttpClientConfig()).resource("https://www.google.com");

    @Inject
    UserRepository userRepository;

    @Inject
    SessionHandler sessionHandler;

    @Inject
    @Named("isTesting")
    Boolean isTesting;

    @Inject
    @Named("googleRecaptchaKey")
    String googleRecaptchaKey;

    @Inject
    @Named("skipRecaptcha")
    Boolean skipRecaptcha;

    @GET
    @Path("/")
    public Response get(
            @Context HttpServletRequest request,
            @CookieParam(SessionHandler.PERSISTENT_SESSION) String persistentSessionId
    ) {
        if (!sessionHandler.isUserInSession(request.getSession(), persistentSessionId) || !isRightXsrfToken(request)) {
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
        this._logout(request, persistentSessionId);
        try {
            if (!skipRecaptcha) {
                MultivaluedMap recaptchaFormData = new MultivaluedMapImpl();
                recaptchaFormData.add("secret", googleRecaptchaKey);
                recaptchaFormData.add("response", loginInfo.optString("recaptchaToken", ""));
                ClientResponse recaptchaResponse = resource.path("recaptcha").path("api").path("siteverify").post(
                        ClientResponse.class,
                        recaptchaFormData
                );
                JSONObject recaptchaResult = recaptchaResponse.getEntity(JSONObject.class);
                Boolean recaptchaSuccess = recaptchaResult.optBoolean("success", false);
                Double score = recaptchaResult.optDouble("score", 0);
                if (!recaptchaSuccess || score < 0.5) {
                    return Response.status(
                            Response.Status.UNAUTHORIZED.getStatusCode()
                    ).entity(new JSONObject().put(
                            "reason",
                            (recaptchaSuccess ? "recaptcha score" : "problem connecting with recaptcha")
                    )).build();
                }
            }
            User user = userRepository.findByEmail(
                    loginInfo.getString(UserJson.EMAIL).toLowerCase().trim()
            );
            if (user.hasPassword(
                    loginInfo.getString(UserJson.PASSWORD)
            )) {
                authenticateUserInSession(
                        user,
                        request.getSession(),
                        request.getHeader(SessionHandler.X_XSRF_TOKEN)
                );
                Response.ResponseBuilder response = Response.ok(
                        UserJson.toJson(user)
                );
                if (loginInfo.optBoolean("staySignedIn")) {
                    response.cookie(sessionHandler.persistSessionForUser(
                            request.getSession(),
                            user,
                            request.getHeader(SessionHandler.X_XSRF_TOKEN)
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
        if (!isRightXsrfToken(request)) {
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        }
        this._logout(request, persistentSessionId);
        return Response.ok().build();
    }

    public static void authenticateUserInSession(User user, HttpSession session, String xsrfToken) {
        session.setAttribute(SecurityInterceptor.AUTHENTICATION_ATTRIBUTE_KEY, true);
        session.setAttribute(SecurityInterceptor.AUTHENTICATED_USER_KEY, user);
        session.setAttribute(SessionHandler.X_XSRF_TOKEN, xsrfToken);
    }

    public static Boolean isRightXsrfToken(HttpServletRequest request) {
        Object xsrfTokenInSessionObject = request.getSession().getAttribute(SessionHandler.X_XSRF_TOKEN);
        if (xsrfTokenInSessionObject == null) {
            return false;
        }
        return xsrfTokenInSessionObject.toString().equals(
                request.getHeader(SessionHandler.X_XSRF_TOKEN)
        );
    }

    private void _logout(HttpServletRequest request, String persistentSessionId) {
        sessionHandler.removePersistentSession(persistentSessionId);
        request.getSession().setAttribute(SecurityInterceptor.AUTHENTICATION_ATTRIBUTE_KEY, false);
        request.getSession().setAttribute(SecurityInterceptor.AUTHENTICATED_USER_KEY, null);
        request.getSession().setAttribute(SessionHandler.X_XSRF_TOKEN, null);
    }
}
