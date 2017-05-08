/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources;

import com.sun.jersey.api.client.ClientResponse;
import guru.bubl.module.model.graph.identification.Identifier;
import guru.bubl.module.model.graph.identification.IdentifierPojo;
import guru.bubl.module.model.search.VertexSearchResult;
import guru.bubl.service.utils.GraphManipulationRestTestUtils;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;

import javax.ws.rs.core.Response;

import java.util.Set;

import static guru.bubl.module.model.json.UserJson.USER_NAME;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ForkResourceTest extends GraphManipulationRestTestUtils {

    @Test
    public void fork_returns_ok_status() {
        ClientResponse response = forkVertexABCGraph();
        assertThat(
                response.getStatus(),
                is(Response.Status.OK.getStatusCode())
        );
    }

    @Test
    public void returns_forbidden_if_forking_for_another_user() {
        JSONObject graphAsJson = graphUtils().getNonOwnedGraphOfCentralVertex(
                vertexA()
        ).getEntity(JSONObject.class);
        JSONObject otherUser = userUtils().validForCreation();
        createUser(otherUser);
        authenticate(defaultAuthenticatedUserAsJson);
        ClientResponse response = resource
                .path(getUsersBaseUri(otherUser.optString(USER_NAME, "")))
                .path("fork")
                .cookie(authCookie)
                .post(ClientResponse.class, graphAsJson);
        assertThat(
                response.getStatus(),
                is(Response.Status.FORBIDDEN.getStatusCode())
        );
    }

    @Test
    public void can_fork() {
        Identifier vertexBAsIdentifier = new IdentifierPojo(
                vertexBUri()
        );
        Set<VertexSearchResult> relatedResources = identificationUtils().getRelatedResourcesForIdentification(
                vertexBAsIdentifier
        );
        assertTrue(
                relatedResources.isEmpty()
        );
        vertexUtils().makePublicVertexWithUri(vertexBUri());
        forkVertexABCGraph();
        relatedResources = identificationUtils().getRelatedResourcesForIdentification(
                vertexBAsIdentifier
        );
        assertFalse(
                relatedResources.isEmpty()
        );
    }

    public ClientResponse forkVertexABCGraph() {
        JSONObject graphAsJson = graphUtils().getNonOwnedGraphOfCentralVertex(
                vertexB()
        ).getEntity(JSONObject.class);
        JSONObject otherUser = userUtils().validForCreation();
        createUser(otherUser);
        authenticate(otherUser);
        return resource
                .path(getUsersBaseUri(otherUser.optString(USER_NAME, "")))
                .path("fork")
                .cookie(authCookie)
                .post(ClientResponse.class, graphAsJson);
    }
}

