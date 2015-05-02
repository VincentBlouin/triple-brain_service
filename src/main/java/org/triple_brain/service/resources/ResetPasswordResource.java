/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package org.triple_brain.service.resources;

import com.google.inject.name.Named;
import com.sun.jersey.api.core.HttpContext;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.triple_brain.module.model.User;
import org.triple_brain.module.model.forgot_password.UserForgotPasswordToken;
import org.triple_brain.module.model.forgot_password.email.ForgotPasswordEmail;
import org.triple_brain.module.repository.user.UserRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/reset-password")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Singleton
public class ResetPasswordResource {

    @Inject
    UserRepository userRepository;

    @Inject
    ForgotPasswordEmail forgotPasswordEmail;

    @Inject
    @Named("AppUrl")
    String appUrl;

    @Path("/")
    @POST
    public Response reset(JSONObject emailWrap, @Context HttpContext context){
        try {
            String email = emailWrap.getString("email");
            if(!userRepository.emailExists(email)){
                throw new WebApplicationException(Response.Status.BAD_REQUEST);
            }
            User user = userRepository.findByEmail(email);
            UserForgotPasswordToken userForgotPasswordToken = UserForgotPasswordToken.generate();
            userRepository.generateForgetPasswordToken(
                    user,
                    userForgotPasswordToken
            );
            forgotPasswordEmail.send(
                    user,
                    appUrl + "?email="+user.email().replace("@", "%40")+"&reset-token=" + userForgotPasswordToken.getToken()
            );
            return Response.noContent().build();
        }catch(JSONException e){
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
    }
}
