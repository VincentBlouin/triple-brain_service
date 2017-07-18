/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.test;

import com.google.gson.Gson;
import guru.bubl.module.common_utils.NoExRun;
import guru.bubl.module.model.User;
import guru.bubl.module.model.forgot_password.UserForgotPasswordToken;
import guru.bubl.module.model.graph.GraphTransactional;
import guru.bubl.module.model.json.UserJson;
import guru.bubl.module.repository.user.UserRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Connection;

@Path("test/users")
@Singleton
public class UserResourceTestUtils {

    @Inject
    protected Connection connection;

    @Inject
    UserRepository userRepository;

    private Gson gson = new Gson();

    @Path("{email}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response emailExists(@PathParam("email") String email) throws Exception {
        return Response.ok(
                userRepository.emailExists(email).toString()
        ).build();
    }

    @Path("/")
    @DELETE
    @GraphTransactional
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteAllUsers() throws Exception {
        NoExRun.wrap(() ->
                connection.createStatement().executeQuery(
                        "START n=node:node_auto_index('type:user') DELETE n"
                )).get();
        return Response.noContent().build();
    }


    @POST
    @Path("/vince")
    public Response createUserVince() throws Exception {
        User user = User.withEmailAndUsername(
                "vince_email@example.org",
                "vince"
        ).password("password");
        userRepository.createUser(user);
        return Response.ok().entity(
                UserJson.toJson(user)
        ).build();
    }

    @POST
    @Path("/Vince")
    public Response createUserVinceWithCapitalLetter() throws Exception {
        User user = User.withEmailAndUsername(
                "vince_capital_email@example.org",
                "Vince"
        ).password("password");
        userRepository.createUser(user);
        return Response.ok().entity(
                UserJson.toJson(user)
        ).build();
    }

    @Path("/{username}/forget-password-token")
    @GET
    public Response getForgetPasswordToken(@PathParam("username") String username) throws Exception {
        User user = userRepository.findByUsername(username);
        return Response.ok().entity(
                gson.toJson(
                        userRepository.getUserForgetPasswordToken(user)
                )
        ).build();
    }

    @Path("/{username}/forget-password-token")
    @POST
    public Response setForgetPasswordToken(@PathParam("username") String username, String userForgetPasswordTokenJson) throws Exception {
        UserForgotPasswordToken userForgotPasswordToken = gson.fromJson(
                userForgetPasswordTokenJson,
                UserForgotPasswordToken.class
        );
        userRepository.generateForgetPasswordToken(
                userRepository.findByUsername(username),
                userForgotPasswordToken
        );
        return Response.noContent().build();
    }

}
