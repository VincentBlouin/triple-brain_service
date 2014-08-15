package org.triple_brain.service.vertex;

import com.sun.jersey.api.client.ClientResponse;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;
import org.triple_brain.module.model.UserUris;
import org.triple_brain.module.model.graph.SubGraph;
import org.triple_brain.module.model.graph.vertex.Vertex;
import org.triple_brain.module.model.json.graph.SubGraphJson;
import org.triple_brain.service.utils.GraphManipulationRestTest;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/*
* Copyright Mozilla Public License 1.1
*/
public class VertexNonOwnedSurroundGraphResourceTest extends GraphManipulationRestTest {

    @Test
    public void getting_graph_of_another_user_returns_correct_status() {
        vertexUtils().makePublicVertexWithUri(
                vertexAUri()
        );
        JSONObject anotherUser = createAUser();
        authenticate(anotherUser);
        assertThat(
                getNonOwnedGraphOfCentralVertex(vertexA()).getStatus(),
                is(
                        Response.Status.OK.getStatusCode()
                )
        );
    }

    @Test
    public void getting_graph_of_another_user_private_center_vertex_returns_forbidden_status() {
        vertexUtils().makePrivateVertexWithUri(
                vertexAUri()
        );
        JSONObject anotherUser = createAUser();
        authenticate(anotherUser);
        assertThat(
                getNonOwnedGraphOfCentralVertex(vertexA()).getStatus(),
                is(
                        Response.Status.FORBIDDEN.getStatusCode()
                )
        );
    }

    @Test
    public void can_get_graph_of_another_user() {
        vertexUtils().makePublicVertexWithUri(
                vertexAUri()
        );
        JSONObject anotherUser = createAUser();
        authenticate(anotherUser);
        SubGraph subGraph = SubGraphJson.fromJson(
                getNonOwnedGraphOfCentralVertex(vertexA()).getEntity(JSONObject.class)
        );
        assertTrue(
                subGraph.vertices().containsKey(vertexAUri())
        );
    }

    @Test
    public void surround_vertices_have_to_public_to_be_included() {
        vertexUtils().makePrivateVertexWithUri(
                vertexAUri()
        );
        vertexUtils().makePublicVertexWithUri(
                vertexBUri()
        );
        vertexUtils().makePrivateVertexWithUri(
                vertexCUri()
        );
        JSONObject anotherUser = createAUser();
        authenticate(anotherUser);
        SubGraph subGraph = SubGraphJson.fromJson(
                getNonOwnedGraphOfCentralVertex(vertexB()).getEntity(JSONObject.class)
        );
        assertThat(
                subGraph.vertices().size(),
                is(1)
        );
    }

    @Test
    public void surround_public_vertices_are_accessible() {
        vertexUtils().makePublicVertexWithUri(
                vertexAUri()
        );
        vertexUtils().makePublicVertexWithUri(
                vertexBUri()
        );
        vertexUtils().makePublicVertexWithUri(
                vertexCUri()
        );
        JSONObject anotherUser = createAUser();
        authenticate(anotherUser);
        SubGraph subGraph = SubGraphJson.fromJson(
                getNonOwnedGraphOfCentralVertex(vertexB()).getEntity(JSONObject.class)
        );
        assertThat(
                subGraph.vertices().size(),
                is(3)
        );
    }

    @Test
    public void owner_can_access_all_even_if_private() {
        vertexUtils().makePrivateVertexWithUri(
                vertexAUri()
        );
        vertexUtils().makePrivateVertexWithUri(
                vertexBUri()
        );
        vertexUtils().makePrivateVertexWithUri(
                vertexCUri()
        );
        SubGraph subGraph = SubGraphJson.fromJson(
                getNonOwnedGraphOfCentralVertex(vertexB()).getEntity(JSONObject.class)
        );
        assertThat(
                subGraph.vertices().size(),
                is(3)
        );
    }

    private ClientResponse getNonOwnedGraphOfCentralVertex(Vertex vertex) {
        String shortId = UserUris.graphElementShortId(
                vertex.uri()
        );
        return resource
                .path(getUsersBaseUri(vertex.ownerUsername()))
                .path("non_owned")
                .path("vertex")
                .path(shortId)
                .path("surround_graph")
                .cookie(authCookie)
                .accept(MediaType.WILDCARD)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .get(ClientResponse.class);
    }
}
