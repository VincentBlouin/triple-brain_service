
/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources;

import com.sun.jersey.api.client.ClientResponse;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Ignore;
import org.junit.Test;
import guru.bubl.module.model.User;
import guru.bubl.module.model.json.UserJson;
import guru.bubl.service.utils.GraphManipulationRestTestUtils;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static guru.bubl.module.model.validator.UserValidator.ALREADY_REGISTERED_EMAIL;
import static guru.bubl.module.model.validator.UserValidator.USER_NAME_ALREADY_REGISTERED;
import static guru.bubl.module.model.json.UserJson.*;

public class UserResourceTest extends GraphManipulationRestTestUtils {

    @Test
    public void can_authenticate_user() throws Exception {
        User rogerLamothe = User.withEmailAndUsername(
                "roger.lamothe@example.org",
                "roger_lamothe"
        );
        JSONObject rogerLamotheAsJson = UserJson.toJson(
                rogerLamothe
        );
        rogerLamotheAsJson.put(
                UserJson.PASSWORD,
                DEFAULT_PASSWORD
        );
        rogerLamotheAsJson.put(
                UserJson.PREFERRED_LOCALES,
                new JSONArray().put("fr")
        );
        createUser(rogerLamotheAsJson);
        JSONObject loginInfo = new JSONObject()
                .put(UserJson.EMAIL, "roger.lamothe@example.org")
                .put(UserJson.PASSWORD, "password");
        ClientResponse response = resource
                .path("service")
                .path("users")
                .path("session")
                .accept(MediaType.WILDCARD)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .post(ClientResponse.class, loginInfo);
        assertThat(response.getStatus(), is(200));
    }

    @Test
    public void can_logout() throws Exception {
        assertTrue(isUserAuthenticated(
                authCookie
        ));
        logoutUsingCookies(authCookie);
        assertFalse(isUserAuthenticated(
                authCookie
        ));
    }

    @Test
    public void authentication_returns_user_as_json() throws Exception {
        JSONObject user = userUtils().validForCreation();
        createUser(user);
        JSONObject loginInfo = new JSONObject()
                .put(
                        UserJson.EMAIL,
                        user.getString(UserJson.EMAIL)
                )
                .put(UserJson.PASSWORD, DEFAULT_PASSWORD);
        ClientResponse response = resource
                .path("service")
                .path("users")
                .path("session")
                .accept(MediaType.WILDCARD)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .post(ClientResponse.class, loginInfo);
        JSONObject userFromResponse = response.getEntity(JSONObject.class);
        String originalEmail = user.getString(UserJson.EMAIL);
        assertThat(
                userFromResponse.getString(EMAIL),
                is(originalEmail)
        );
    }

    @Test
    public void can_create_user() throws Exception {
        JSONObject user = userUtils().validForCreation();
        assertFalse(userUtils().emailExists(
                user.getString(EMAIL)
        ));
        createUser(
                user
        );
        assertTrue(
                userUtils().emailExists(
                        user.getString(EMAIL)
                )
        );
    }

    @Test
    public void checks_if_username_exists_only_if_valid_user_name() throws Exception {
        JSONObject user = userUtils().validForCreation();
        user.put(USER_NAME, "name with space");
        assertFalse(userUtils().emailExists(
                user.getString(EMAIL)
        ));
        ClientResponse response = createUser(
                user
        );
        assertThat(
                response.getStatus(),
                is(BAD_REQUEST.getStatusCode())
        );
    }

    @Test
    public void user_is_authenticated_after_creation() throws JSONException {
        logoutUsingCookies(authCookie);
        assertFalse(isUserAuthenticated(
                authCookie
        ));
        JSONObject validForCreation = userUtils().validForCreation();
        createUser(validForCreation);
        assertTrue(isUserAuthenticated(
                authCookie
        ));
    }

    @Test
    public void creating_a_user_returns_corrects_response() throws Exception {
        JSONObject jsonUser = userUtils().validForCreation();
        ClientResponse response = createUser(
                jsonUser
        );
        assertThat(
                response.getStatus(),
                is(Response.Status.CREATED.getStatusCode())
        );
        jsonUser = authenticate(jsonUser).getEntity(JSONObject.class);
        String username = jsonUser.getString(UserJson.USER_NAME);
        assertThat(
                response.getHeaders().get("Location").get(0),
                is(BASE_URI + "/service/users/" + username)
        );
    }

    @Test
    @Ignore("adapt test")
    public void when_creating_a_user_a_mind_map_is_created_for_him() throws Exception {
        JSONObject validUser = userUtils().validForCreation();
        User user = User.withEmailAndUsername(
                validUser.getString(UserJson.EMAIL),
                validUser.getString(USER_NAME)
        );
//        assertFalse(
//                graphElementWithIdExistsInCurrentGraph(
//                        new UserUris(user).defaultVertexUri()
//                )
//        );
//        createUser(validUser);
//        assertTrue(
//                graphElementWithIdExistsInCurrentGraph(
//                        new UserUris(user).defaultVertexUri()
//                )
//        );
    }

    @Test
    public void can_get_current_authenticated_user() throws Exception {
        JSONObject user = createUserWithEmail("roger_lamothe@example.org");
        authenticate(user);
        ClientResponse response = resource
                .path("service")
                .path("users")
                .path("session")
                .cookie(authCookie)
                .accept(MediaType.APPLICATION_JSON)
                .get(ClientResponse.class);
        JSONObject userFromResponse = response.getEntity(JSONObject.class);
        assertThat(userFromResponse.getString(EMAIL), is("roger_lamothe@example.org"));
    }

    @Test
    public void getting_current_authenticated_user_without_being_authenticated_returns_the_forbidden_status() throws Exception {
        ClientResponse response = resource
                .path("service")
                .path("users")
                .path("session")
                .get(ClientResponse.class);
        assertThat(response.getStatus(), is(Response.Status.FORBIDDEN.getStatusCode()));
    }


    @Test
    public void cant_register_same_email_twice() throws Exception {
        createUserWithEmail("roger.lamothe@example.org");
        JSONObject jsonUser = userUtils().validForCreation().put(EMAIL, "roger.lamothe@example.org");
        ClientResponse response = resource
                .path("service")
                .path("users")
                .accept(MediaType.WILDCARD)
                .type(MediaType.APPLICATION_JSON)
                .post(ClientResponse.class, jsonUser);
        assertThat(response.getStatus(), is(400));
        JSONArray errors = response.getEntity(JSONArray.class);
        assertThat(errors.length(), greaterThan(0));
        assertThat(errors.getJSONObject(0).get("field").toString(), is(EMAIL));
        assertThat(errors.getJSONObject(0).get("reason").toString(), is(ALREADY_REGISTERED_EMAIL));
    }

    @Test
    public void cant_register_same_user_name_twice() throws Exception {
        createUserWithUsername("roger_lamothe");
        JSONObject jsonUser = userUtils().validForCreation().put(USER_NAME, "roger_lamothe");
        ClientResponse response = resource
                .path("service")
                .path("users")
                .accept(MediaType.WILDCARD)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .post(ClientResponse.class, jsonUser);
        assertThat(response.getStatus(), is(400));
        JSONArray errors = response.getEntity(JSONArray.class);
        assertThat(errors.length(), greaterThan(0));
        assertThat(errors.getJSONObject(0).get("field").toString(), is(USER_NAME));
        assertThat(errors.getJSONObject(0).get("reason").toString(), is(USER_NAME_ALREADY_REGISTERED));
    }

    @Test
    public void returned_user_creation_error_messages_are_in_the_right_order() throws Exception {
        JSONObject jsonUser = new JSONObject();
        jsonUser.put(EMAIL, "");
        jsonUser.put(USER_NAME, "");
        jsonUser.put(PASSWORD, "pass");
        ClientResponse response = resource
                .path("service")
                .path("users")
                .accept(MediaType.WILDCARD)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .cookie(authCookie)
                .post(ClientResponse.class, jsonUser);
        JSONArray errors = response.getEntity(JSONArray.class);
        assertThat(errors.getJSONObject(0).get("field").toString(), is(EMAIL));
        assertThat(errors.getJSONObject(1).get("field").toString(), is(USER_NAME));
        assertThat(errors.getJSONObject(2).get("field").toString(), is(PASSWORD));
    }

    @Test
    public void creates_persistent_session_on_signup_only_if_option_checked() throws Exception {
        JSONObject jsonUser = userUtils().validForCreation();
        jsonUser.put("staySignedIn", false);
        createUserUsingJson(jsonUser);
        assertTrue(
                persistentSessionsRestTestUtils().get().isEmpty()
        );
        JSONObject jsonUserKeepSignIn = userUtils().validForCreation();
        jsonUserKeepSignIn.put("staySignedIn", true);
        createUserUsingJson(jsonUserKeepSignIn);
        assertFalse(
                persistentSessionsRestTestUtils().get().isEmpty()
        );
    }

    private JSONObject createUserWithEmail(String email) throws Exception {
        JSONObject user = userUtils().validForCreation();
        user.put(UserJson.EMAIL, email);
        createUser(user);
        return user;
    }

    private JSONObject createUserWithUsername(String username) throws Exception {
        JSONObject user = userUtils().validForCreation();
        user.put(UserJson.USER_NAME, username);
        createUser(user);
        return user;
    }
}

