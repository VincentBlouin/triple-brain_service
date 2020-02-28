/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.utils;

import com.google.gson.Gson;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import guru.bubl.module.common_utils.NoEx;
import guru.bubl.module.model.User;
import guru.bubl.module.model.forgot_password.UserForgotPasswordToken;
import guru.bubl.service.SessionHandler;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.util.UUID;

import static guru.bubl.module.model.json.UserJson.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class UserRestTestUtils {

    private WebResource resource;
    private NewCookie authCookie;
    private String xsrfToken;

    public static UserRestTestUtils withWebResourceAndCookie(WebResource resource, NewCookie authCookie, String xsrfToken) {
        return new UserRestTestUtils(resource, authCookie, xsrfToken);
    }

    protected UserRestTestUtils(WebResource resource, NewCookie authCookie, String xsrfToken) {
        this.resource = resource;
        this.authCookie = authCookie;
        this.xsrfToken = xsrfToken;
    }

    public boolean emailExists(String email) {
        ClientResponse response = resource
                .path("service")
                .path("test")
                .path("users")
                .path(email)
                .accept(MediaType.TEXT_PLAIN)
                .get(ClientResponse.class);
        String emailExistsStr = response.getEntity(String.class);
        return Boolean.valueOf(emailExistsStr);
    }

    public void deleteAllUsers() {
        ClientResponse response = resource
                .path("service")
                .path("test")
                .path("users")
                .accept(MediaType.TEXT_PLAIN)
                .delete(ClientResponse.class);
        assertThat(
                response.getStatus(),
                is(Response.Status.NO_CONTENT.getStatusCode())
        );
    }

    public JSONArray getUserPreferredLocale(String username) {
        ClientResponse response = resource
                .path("service")
                .path("test")
                .path("users")
                .path(username)
                .path("locale")
                .accept(MediaType.APPLICATION_JSON)
                .header(SessionHandler.X_XSRF_TOKEN, xsrfToken)
                .get(ClientResponse.class);
        assertThat(
                response.getStatus(),
                is(Response.Status.OK.getStatusCode())
        );
        return response.getEntity(JSONArray.class);
    }

    public JSONObject validForCreation() {
        JSONObject user = new JSONObject();
        try {
            user.put(USER_NAME, randomUsername());
            user.put(EMAIL, randomEmail());
            user.put(PASSWORD, RestTestUtils.DEFAULT_PASSWORD);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    public UserForgotPasswordToken getUserForgetPasswordToken(User user) {
        ClientResponse response = resource
                .path("service")
                .path("test")
                .path("users")
                .path(user.username())
                .path("forget-password-token")
                .get(ClientResponse.class);
        assertThat(
                response.getStatus(),
                is(Response.Status.OK.getStatusCode())
        );
        try {
            return new Gson().fromJson(
                    response.getEntity(String.class),
                    UserForgotPasswordToken.class
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setUserForgetPasswordToken(User user, UserForgotPasswordToken userForgotPasswordToken) {
        ClientResponse response = resource
                .path("service")
                .path("test")
                .path("users")
                .path(user.username())
                .path("forget-password-token")
                .post(
                        ClientResponse.class,
                        new Gson().toJson(userForgotPasswordToken)
                );
        assertThat(
                response.getStatus(),
                is(Response.Status.NO_CONTENT.getStatusCode())
        );
    }

    public ClientResponse addFriend(String usernameA, String usernameB) {
        ClientResponse response = NoEx.wrap(() -> resource
                .path("service")
                .path("users")
                .path(usernameA)
                .path("friends")
                .cookie(authCookie)
                .header(SessionHandler.X_XSRF_TOKEN, xsrfToken)
                .post(
                        ClientResponse.class,
                        new JSONObject().put(
                                "friendUsername",
                                usernameB
                        )
                )
        ).get();
        assertThat(
                response.getStatus(),
                is(Response.Status.OK.getStatusCode())
        );
        return response;
    }

    private String randomEmail() {
        return UUID.randomUUID().toString() + "@example.org";
    }

    private String randomUsername() {
        return UUID.randomUUID().toString().substring(0, 15);
    }
}
