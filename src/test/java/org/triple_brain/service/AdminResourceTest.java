/*
 * Copyright Vincent Blouin under the Mozilla Public License 1.1
 */

package org.triple_brain.service;

import com.sun.jersey.api.client.ClientResponse;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;
import org.triple_brain.module.model.json.UserJson;
import org.triple_brain.service.utils.GraphManipulationRestTestUtils;

import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class AdminResourceTest extends GraphManipulationRestTestUtils {

    @Test
    public void indexing_whole_graph_returns_no_content_status() {
        assertThat(
                reindexAll(
                        loginAsVince()
                ).getStatus(),
                is(
                        Response.Status.NO_CONTENT.getStatusCode()
                )
        );
    }

    @Test
    public void can_refresh_number_of_connected_edges() throws Exception {
        setNumberOfConnectedOfAllVerticesToZero();
        assertThat(
                vertexB().getNumberOfConnectedEdges(),
                is(0)
        );
        refreshNumberOfConnectedEdges(
                loginAsVince()
        );
        authenticate(defaultAuthenticatedUserAsJson);
        assertThat(
                vertexB().getNumberOfConnectedEdges(),
                is(2)
        );
    }


    private ClientResponse setNumberOfConnectedOfAllVerticesToZero() {
        return resource
                .path("service")
                .path("test")
                .path("graph")
                .path("set_all_number_of_connected_edges_to_zero")
                .cookie(authCookie)
                .post(ClientResponse.class);
    }

    private ClientResponse refreshNumberOfConnectedEdges(NewCookie vinceCookie) {
        return resource
                .path("service")
                .path("users")
                .path("vince")
                .path("admin")
                .path("refresh_number_of_connected_edges")
                .cookie(vinceCookie)
                .post(ClientResponse.class);
    }

    private ClientResponse reindexAll(NewCookie vinceCookie) {
        return resource
                .path("service")
                .path("users")
                .path("vince")
                .path("admin")
                .path("reindex")
                .cookie(vinceCookie)
                .post(ClientResponse.class);
    }

    private NewCookie loginAsVince() {
        JSONObject vince = userVince();
        createUser(
                vince
        );
        return authenticate(
                vince
        ).getCookies().get(0);
    }

    private JSONObject userVince() {
        try {
            return userUtils().validForCreation().put(
                    UserJson.USER_NAME,
                    "vince"
            );
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
