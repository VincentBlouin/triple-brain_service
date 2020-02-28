/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources;

import com.sun.jersey.api.client.ClientResponse;
import guru.bubl.service.utils.GraphManipulationRestTestUtils;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;

import javax.ws.rs.core.NewCookie;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UserSessionResourceTest extends GraphManipulationRestTestUtils{

    @Test
    public void creates_persistent_session_on_login_only_if_option_checked() throws Exception {
        JSONObject jsonUser = userUtils().validForCreation();
        jsonUser.put("staySignedIn", false);
        createUserUsingJson(jsonUser);
        jsonUser.put("staySignedIn", true);
        ClientResponse response = authenticate(jsonUser);
        NewCookie persistentSessionCookie = getPersistentSessionCookie(response.getCookies());
        assertFalse(
                persistentSessionsRestTestUtils().get().isEmpty()
        );
        logoutUsingCookies(authCookie, persistentSessionCookie);
        jsonUser.put("staySignedIn", false);
        authenticateWithPersistentSessionCookie(
                jsonUser,
                persistentSessionCookie
        );
        assertTrue(
                persistentSessionsRestTestUtils().get().isEmpty()
        );
    }

    @Test
    public void logging_out_removes_persistent_session() throws Exception {
        JSONObject jsonUser = userUtils().validForCreation();
        jsonUser.put("staySignedIn", true);
        ClientResponse response = createUserUsingJson(jsonUser);
        NewCookie persistentSessionCookie = getPersistentSessionCookie(response.getCookies());
        assertFalse(
                persistentSessionsRestTestUtils().get().isEmpty()
        );
        logoutUsingCookies(authCookie, persistentSessionCookie);
        assertTrue(
                persistentSessionsRestTestUtils().get().isEmpty()
        );
    }
}
