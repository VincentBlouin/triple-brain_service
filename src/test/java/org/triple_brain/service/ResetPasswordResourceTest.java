/*
 * Copyright Vincent Blouin under the Mozilla Public License 1.1
 */

package org.triple_brain.service;

import com.sun.jersey.api.client.ClientResponse;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;
import org.triple_brain.service.utils.GraphManipulationRestTestUtils;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ResetPasswordResourceTest extends GraphManipulationRestTestUtils {

    @Test
    public void returns_correct_status() throws Exception{
        logoutUsingCookie(authCookie);
        JSONObject emailWrap = new JSONObject().put(
                "email",
                defaultAuthenticatedUser.email()
        );
        ClientResponse response = resource
                .path("service")
                .path("reset-password")
                .type(MediaType.APPLICATION_JSON_TYPE)
                .post(ClientResponse.class, emailWrap);
        assertThat(
                response.getStatus(),
                is(Response.Status.NO_CONTENT.getStatusCode())
        );
    }

    @Test
    public void using_inexistent_email_returns_bad_request() throws Exception{
        JSONObject emailWrap = new JSONObject().put(
                "email",
                "inexitent_email@example.org"
        );
        ClientResponse response = resource
                .path("service")
                .path("reset-password")
                .type(MediaType.APPLICATION_JSON_TYPE)
                .post(ClientResponse.class, emailWrap);
        assertThat(
                response.getStatus(),
                is(Response.Status.BAD_REQUEST.getStatusCode())
        );
    }
}
