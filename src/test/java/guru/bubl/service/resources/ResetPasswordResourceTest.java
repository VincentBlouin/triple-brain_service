/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources;

import com.sun.jersey.api.client.ClientResponse;
import guru.bubl.service.utils.GraphManipulationRestTestUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ResetPasswordResourceTest extends GraphManipulationRestTestUtils {

    @Test
    public void returns_correct_status() throws Exception{
        logoutUsingCookies(authCookie);
        ClientResponse response = reset(
                defaultAuthenticatedUser.email()
        );
        assertThat(
                response.getStatus(),
                is(Response.Status.NO_CONTENT.getStatusCode())
        );
    }

    @Test
    public void using_inexistent_email_returns_bad_request() throws Exception{
        logoutUsingCookies(authCookie);
        ClientResponse response = reset(
                "inexistent_email@example.org"
        );
        assertThat(
                response.getStatus(),
                is(Response.Status.BAD_REQUEST.getStatusCode())
        );
    }

    private ClientResponse reset(String email){
        try {
            JSONObject emailWrap = new JSONObject().put(
                    "email",
                    email
            );
            return resource
                    .path("service")
                    .path("reset-password")
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .post(ClientResponse.class, emailWrap);
        }catch(JSONException e){
            throw new RuntimeException(e);
        }
    }
}
