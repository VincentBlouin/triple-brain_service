/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources;

import guru.bubl.service.SecurityInterceptor;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import guru.bubl.module.model.User;
import guru.bubl.module.model.graph.GraphTransactional;
import guru.bubl.module.model.json.UserJson;
import guru.bubl.module.repository.user.NonExistingUserException;
import guru.bubl.module.repository.user.UserRepository;

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

    @GET
    @Path("/")
    public Response get(
        @Context HttpServletRequest request
    ) {
        if(!GraphManipulatorResourceUtils.isUserInSession(request.getSession())){
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        }
        return Response.ok(
                UserJson.toJson(
                        GraphManipulatorResourceUtils.userFromSession(request.getSession())
                )
        ).build();
    }

    @POST
    @Produces(MediaType.WILDCARD)
    @GraphTransactional
    @Path("/")
    public Response authenticate(
            JSONObject loginInfo,
            @Context HttpServletRequest request
    ){
        String email = "";
        try {
            email = loginInfo.getString(UserJson.EMAIL);
            User user = userRepository.findByEmail(
                    loginInfo.getString(UserJson.EMAIL)
            );
            if (user.hasPassword(
                    loginInfo.getString(UserJson.PASSWORD)
            )) {
                authenticateUserInSession(user, request.getSession());
                return Response.ok(
                        UserJson.toJson(user)
                ).build();
            }
        } catch (NonExistingUserException e) {
            return Response.status(401).build();
        } catch(JSONException e){
            throw new RuntimeException(e);
        }
        return Response.status(401).build();
    }

    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/")
    public Response logout(@Context HttpServletRequest request){
        request.getSession().setAttribute(SecurityInterceptor.AUTHENTICATION_ATTRIBUTE_KEY, false);
        request.getSession().setAttribute(SecurityInterceptor.AUTHENTICATED_USER_KEY, null);
        return Response.ok().build();
    }

    public static void authenticateUserInSession(User user, HttpSession session){
        session.setAttribute(SecurityInterceptor.AUTHENTICATION_ATTRIBUTE_KEY, true);
        session.setAttribute(SecurityInterceptor.AUTHENTICATED_USER_KEY, user);
    }
}
