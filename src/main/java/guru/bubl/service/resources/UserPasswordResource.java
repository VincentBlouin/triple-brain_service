/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import guru.bubl.module.model.User;
import guru.bubl.module.model.forgot_password.UserForgotPasswordToken;
import guru.bubl.module.model.validator.UserValidator;
import guru.bubl.module.repository.user.UserRepository;

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
            if(!UserValidator.errorsForPassword(newPassword).isEmpty()){
                throw new WebApplicationException(
                        Response.Status.BAD_REQUEST
                );
            }
            if(!userRepository.emailExists(email)){
                throw new WebApplicationException(
                        Response.Status.UNAUTHORIZED
                );
            }
            User user = userRepository.findByEmail(email);
            UserForgotPasswordToken userForgotPasswordToken = userRepository.getUserForgetPasswordToken(user);
            Boolean isTokenValid = userForgotPasswordToken.hasToken(forgetPasswordToken) && !userForgotPasswordToken.isExpired();
            if(!isTokenValid){
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
