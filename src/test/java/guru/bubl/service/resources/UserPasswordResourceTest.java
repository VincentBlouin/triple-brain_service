/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources;

import com.sun.jersey.api.client.ClientResponse;
import guru.bubl.module.model.forgot_password.UserForgotPasswordToken;
import guru.bubl.module.model.json.UserJson;
import guru.bubl.service.utils.GraphManipulationRestTestUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.joda.time.DateTime;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class UserPasswordResourceTest extends GraphManipulationRestTestUtils {

    @Test
    public void changing_password_returns_correct_status() {
        logoutUsingCookies(authCookie);
        userUtils().setUserForgetPasswordToken(
                defaultAuthenticatedUser,
                UserForgotPasswordToken.generate().setResetPasswordToken("token")
        );
        assertThat(
                changePassword(
                        defaultAuthenticatedUser.email(),
                        "new_password",
                        "token"
                ).getStatus(),
                is(
                        ClientResponse.Status.NO_CONTENT.getStatusCode()
                )
        );
    }

    @Test
    public void can_change_password() {
        logoutUsingCookies(authCookie);
        userUtils().setUserForgetPasswordToken(
                defaultAuthenticatedUser,
                UserForgotPasswordToken.generate().setResetPasswordToken("token")
        );
        assertThat(
                authenticateUsingEmailAndPassword(
                        defaultAuthenticatedUser.email(),
                        "new_password"
                ).getStatus(),
                is(Response.Status.UNAUTHORIZED.getStatusCode())
        );
        changePassword(
                defaultAuthenticatedUser.email(),
                "new_password",
                "token"
        );
        assertThat(
                authenticateUsingEmailAndPassword(
                        defaultAuthenticatedUser.email(),
                        "new_password"
                ).getStatus(),
                is(Response.Status.OK.getStatusCode())
        );
    }

    @Test
    public void forget_password_token_must_match_to_change_password() {
        logoutUsingCookies(authCookie);
        ClientResponse response = changePassword(
                defaultAuthenticatedUser.email(),
                "new_password",
                "token"
        );
        assertThat(
                response.getStatus(),
                is(Response.Status.BAD_REQUEST.getStatusCode())
        );
        UserForgotPasswordToken userForgotPasswordToken = UserForgotPasswordToken.generate();
        userForgotPasswordToken.setResetPasswordToken("token");
        userUtils().setUserForgetPasswordToken(
                defaultAuthenticatedUser,
                userForgotPasswordToken
        );
        response = changePassword(
                defaultAuthenticatedUser.email(),
                "new_password",
                "token"
        );
        assertThat(
                response.getStatus(),
                is(Response.Status.NO_CONTENT.getStatusCode())
        );
    }

    @Test
    public void cant_change_password_if_forget_password_token_is_expired() {
        logoutUsingCookies(authCookie);
        UserForgotPasswordToken userForgotPasswordToken = UserForgotPasswordToken.generate();
        userForgotPasswordToken.setResetPasswordExpirationDate(
                new DateTime().minusHours(1).toDate()
        );
        userUtils().setUserForgetPasswordToken(
                defaultAuthenticatedUser,
                userForgotPasswordToken
        );
        ClientResponse response = changePassword(
                defaultAuthenticatedUser.email(),
                "new_password",
                userForgotPasswordToken.getToken()
        );
        assertThat(
                response.getStatus(),
                is(Response.Status.BAD_REQUEST.getStatusCode())
        );
        userForgotPasswordToken = UserForgotPasswordToken.generate();
        userUtils().setUserForgetPasswordToken(
                defaultAuthenticatedUser,
                userForgotPasswordToken
        );
        response = changePassword(
                defaultAuthenticatedUser.email(),
                "new_password",
                userForgotPasswordToken.getToken()
        );
        assertThat(
                response.getStatus(),
                is(Response.Status.NO_CONTENT.getStatusCode())
        );
    }

    @Test
    public void password_cant_be_too_short() {
        logoutUsingCookies(authCookie);
        UserForgotPasswordToken userForgotPasswordToken = UserForgotPasswordToken.generate();
        userUtils().setUserForgetPasswordToken(
                defaultAuthenticatedUser,
                userForgotPasswordToken
        );
        ClientResponse response = changePassword(
                defaultAuthenticatedUser.email(),
                "short",
                userForgotPasswordToken.getToken()
        );
        assertThat(
                response.getStatus(),
                is(Response.Status.BAD_REQUEST.getStatusCode())
        );
    }

    private ClientResponse changePassword(String email, String password, String forgetPasswordToken) {
        try {
            JSONObject data = new JSONObject().put(
                    "email",
                    email
            ).put(
                    "password",
                    password
            ).put(
                    "token",
                    forgetPasswordToken
            );
            return resource
                    .path("service")
                    .path("users")
                    .path("password")
                    .post(ClientResponse.class, data);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private ClientResponse authenticateUsingEmailAndPassword(String email, String password) {
        try {
            JSONObject loginInfo = new JSONObject()
                    .put(
                            UserJson.EMAIL,
                            email
                    )
                    .put(UserJson.PASSWORD, password);
            return resource
                    .path("service")
                    .path("users")
                    .path("session")
                    .accept(MediaType.APPLICATION_JSON_TYPE)
                    .post(ClientResponse.class, loginInfo);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
