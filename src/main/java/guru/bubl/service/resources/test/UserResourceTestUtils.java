/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.test;

import com.google.gson.Gson;
import guru.bubl.module.model.User;
import guru.bubl.module.model.forgot_password.UserForgotPasswordToken;
import guru.bubl.module.model.json.JsonUtils;
import guru.bubl.module.model.json.UserJson;
import guru.bubl.module.repository.user.UserRepository;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;

@Path("test/users")
@Singleton
public class UserResourceTestUtils {

    @Inject
    protected Driver driver;

    @Inject
    @Named("session")
    HashMap sessionsInTests;

    @Inject
    UserRepository userRepository;

    private Gson gson = new Gson();

    @Path("{email}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response emailExists(@PathParam("email") String email) {
        return Response.ok(
                userRepository.emailExists(email).toString()
        ).build();
    }

    @Path("/{username}/locale")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserLocale(@PathParam("username") String username) {
        return Response.ok(JsonUtils.getGson().toJson(
                userRepository.findByUsername(username).getPreferredLocales()
        )).build();
    }

    @Path("/")
    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteAllUsers() {
        sessionsInTests.clear();
        try (Session session = driver.session()) {
            session.run(
                    "MATCH(n:User) DETACH DELETE n"
            );
            return Response.noContent().build();
        }
    }


    @POST
    @Path("/vince")
    public Response createUserVince() {
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
    public Response createUserVinceWithCapitalLetter() {
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
    public Response getForgetPasswordToken(@PathParam("username") String username) {
        User user = userRepository.findByUsername(username);
        return Response.ok().entity(
                gson.toJson(
                        userRepository.getUserForgetPasswordToken(user)
                )
        ).build();
    }

    @Path("/{username}/forget-password-token")
    @POST
    public Response setForgetPasswordToken(@PathParam("username") String username, String userForgetPasswordTokenJson) {
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
