/*
 * Copyright Vincent Blouin under the Mozilla Public License 1.1
 */

package org.triple_brain.service.resources;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.triple_brain.module.model.User;
import org.triple_brain.module.model.forget_password.UserForgetPasswordToken;
import org.triple_brain.module.repository.user.UserRepository;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserPasswordResource {

    @Inject
    UserRepository userRepository;

    @Path("/")
    @POST
    public Response changePassword(JSONObject changePasswordInfo) {
        try {
            String email = changePasswordInfo.getString("email"),
                    newPassword = changePasswordInfo.getString("password"),
                    forgetPasswordToken = changePasswordInfo.getString("token");
            User user = userRepository.findByEmail(email);
            UserForgetPasswordToken userForgetPasswordToken = userRepository.getUserForgetPasswordToken(user);
            if(!userForgetPasswordToken.hasToken(forgetPasswordToken)){
                throw new WebApplicationException(
                        Response.Status.UNAUTHORIZED
                );
            }
            user.password(newPassword);
            userRepository.changePassword(user);
            return Response.noContent().build();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
