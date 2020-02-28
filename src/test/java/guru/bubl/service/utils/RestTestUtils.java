/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.utils;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import guru.bubl.module.model.User;
import guru.bubl.module.model.json.UserJson;
import guru.bubl.service.Launcher;
import guru.bubl.service.SessionHandler;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;


public abstract class RestTestUtils {

    public static URI BASE_URI;
    public static WebResource resource;
    static public Launcher launcher;
    static public Client client;
    protected NewCookie authCookie;
    protected User currentAuthenticatedUser;
    protected String currentXsrfToken;
    public static final String DEFAULT_PASSWORD = "password";

    @BeforeClass
    static public void startServer() throws Exception {

    }

    @AfterClass
    static public void stopServer() throws Exception {

    }

    protected ClientResponse createUser(JSONObject userAsJson) {
        return createUserSkippingDefaultContentOrNot(userAsJson, true);
    }

    protected ClientResponse createUserSkippingDefaultContentOrNot(JSONObject userAsJson, Boolean skipDefaultContent) {
        currentXsrfToken = UUID.randomUUID().toString();
        return resource
                .path("service")
                .path("users")
                .queryParam("skipDefaultContent", skipDefaultContent.toString())
                .cookie(authCookie)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON)
                .header(SessionHandler.X_XSRF_TOKEN, currentXsrfToken)
                .post(ClientResponse.class, userAsJson);
    }

    protected User authenticate(User user) {
        String xsrfToken = UUID.randomUUID().toString();
        try {
            JSONObject loginInfo = new JSONObject()
                    .put(
                            UserJson.EMAIL,
                            user.email()
                    )
                    .put(UserJson.PASSWORD, DEFAULT_PASSWORD);
            ClientResponse response = resource
                    .path("service")
                    .path("users")
                    .path("session")
                    .accept(MediaType.APPLICATION_JSON_TYPE)
                    .header(SessionHandler.X_XSRF_TOKEN, xsrfToken)
                    .post(ClientResponse.class, loginInfo);
            assertThat(response.getStatus(), is(200));
            authCookie = response.getCookies().get(0);
            currentAuthenticatedUser = user;
            currentXsrfToken = xsrfToken;
            return user;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    protected ClientResponse authenticateWithPersistentSessionCookie(JSONObject user, NewCookie persistentSessionCookie) {
        return authenticateWithParams(
                user,
                persistentSessionCookie,
                null
        );
    }

    protected ClientResponse authenticate(JSONObject user) {
        return authenticateWithParams(
                user,
                null,
                null
        );
    }

    protected ClientResponse authenticateWithXsrfToken(JSONObject user, String xsrfToken) {
        return authenticateWithParams(
                user,
                null,
                xsrfToken
        );
    }

    private ClientResponse authenticateWithParams(JSONObject user, NewCookie persistentSessionCookie, String xsrfToken) {
        if (xsrfToken == null) {
            xsrfToken = UUID.randomUUID().toString();
        }
        try {
            JSONObject loginInfo = new JSONObject()
                    .put(
                            UserJson.EMAIL,
                            user.getString(UserJson.EMAIL)
                    )
                    .put(UserJson.PASSWORD, DEFAULT_PASSWORD)
                    .put("staySignedIn", user.optBoolean("staySignedIn"));
            WebResource resourceInTheBuilding = resource
                    .path("service")
                    .path("users")
                    .path("session");
            WebResource.Builder builder = resourceInTheBuilding.getRequestBuilder();
            builder.header(SessionHandler.X_XSRF_TOKEN, xsrfToken);
            if (null != persistentSessionCookie) {
                builder.cookie(persistentSessionCookie);
            }
            ClientResponse response = builder.accept(
                    MediaType.APPLICATION_JSON_TYPE
            ).post(ClientResponse.class, loginInfo);
            assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
            authCookie = getSessionCookie(response.getCookies());
            currentAuthenticatedUser = User.withEmailAndUsername(
                    user.optString(UserJson.EMAIL, ""),
                    user.optString(UserJson.USER_NAME, "")
            );
            currentXsrfToken = xsrfToken;
            return response;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    protected NewCookie getSessionCookie(List<NewCookie> cookies) {
        for (NewCookie cookie : cookies) {
            if (cookie.getName().equals("JSESSIONID")) {
                return cookie;
            }
        }
        return null;
    }

    protected NewCookie getPersistentSessionCookie(List<NewCookie> cookies) {
        for (NewCookie cookie : cookies) {
            if (cookie.getName().equals(SessionHandler.PERSISTENT_SESSION)) {
                return cookie;
            }
        }
        return null;
    }

    protected ClientResponse logoutUsingCookies(NewCookie... cookies) {
        WebResource resourceInTheBuilding = resource
                .path("service")
                .path("users")
                .path("session");
        WebResource.Builder builder = resourceInTheBuilding.getRequestBuilder();
        for (NewCookie cookie : cookies) {
            builder.cookie(cookie);
        }
        builder.header(SessionHandler.X_XSRF_TOKEN, currentXsrfToken);
        ClientResponse response = builder.delete(ClientResponse.class);
        assertThat(
                response.getStatus(),
                is(Response.Status.OK.getStatusCode())
        );
        return response;
    }

    protected Boolean isUserAuthenticated(NewCookie cookie) {
        ClientResponse response = resource
                .path("service")
                .path("users")
                .path("is_authenticated")
                .cookie(cookie)
                .get(ClientResponse.class);
        JSONObject jsonResponse = response.getEntity(JSONObject.class);
        try {
            return jsonResponse.getBoolean("is_authenticated");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    protected ClientResponse createUserUsingJson(JSONObject json) {
        ClientResponse clientResponse = createUser(json);
        assertThat(
                clientResponse.getStatus(),
                is(
                        Response.Status.CREATED.getStatusCode()
                )
        );
        return clientResponse;
    }
}