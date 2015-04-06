/*
 * Copyright Vincent Blouin under the Mozilla Public License 1.1
 */

package org.triple_brain.service.resources;

import com.google.inject.name.Named;
import com.sun.jersey.api.core.HttpContext;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.triple_brain.module.model.User;
import org.triple_brain.module.model.forget_password.UserForgetPasswordToken;
import org.triple_brain.module.model.forget_password.email.ForgetPasswordEmail;
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
    ForgetPasswordEmail forgetPasswordEmail;

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
            UserForgetPasswordToken userForgetPasswordToken = UserForgetPasswordToken.generate();
            userRepository.generateForgetPasswordToken(
                    user,
                    userForgetPasswordToken
            );
            forgetPasswordEmail.send(
                    user,
                    appUrl + "?reset-token=" + userForgetPasswordToken + "&user=" + user.username()
            );
            return Response.noContent().build();
        }catch(JSONException e){
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
    }
}
