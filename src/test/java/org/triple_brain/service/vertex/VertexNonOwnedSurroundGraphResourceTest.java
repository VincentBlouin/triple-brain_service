/*
 * Copyright Vincent Blouin under the Mozilla Public License 1.1
 */

package org.triple_brain.service.vertex;

import com.sun.jersey.api.client.ClientResponse;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;
import org.triple_brain.module.model.UserUris;
import org.triple_brain.module.model.graph.SubGraph;
import org.triple_brain.module.model.graph.edge.Edge;
import org.triple_brain.module.model.graph.vertex.Vertex;
import org.triple_brain.module.model.json.graph.SubGraphJson;
import org.triple_brain.service.utils.GraphManipulationRestTest;

import javax.ws.rs.core.Response;

import java.net.URI;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

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
    public void error_404_when_trying_to_access_deleted_bubble() {
        URI vertexBUri = vertexB().uri();
        vertexUtils().removeVertexB();
        assertThat(
                getNonOwnedGraphOfCentralVertexWithUri(vertexBUri).getStatus(),
                is(
                        Response.Status.NOT_FOUND.getStatusCode()
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

    @Test
    public void relations_related_to_private_vertices_are_absent() {
        vertexUtils().makePublicVerticesWithUri(
                vertexAUri(),
                vertexBUri(),
                vertexCUri()
        );
        Edge edgeBetweenAAndB = edgeUtils().edgeBetweenAAndB();
        SubGraph subGraph = SubGraphJson.fromJson(
                getNonOwnedGraphOfCentralVertex(vertexB()).getEntity(JSONObject.class)
        );
        assertTrue(
                subGraph.hasEdgeWithUri(
                        edgeBetweenAAndB.uri()
                )
        );
        vertexUtils().makePrivateVertexWithUri(vertexAUri());
        JSONObject anotherUser = createAUser();
        authenticate(anotherUser);
        subGraph = SubGraphJson.fromJson(
                getNonOwnedGraphOfCentralVertex(vertexB()).getEntity(JSONObject.class)
        );
        assertFalse(
                subGraph.hasEdgeWithUri(
                        edgeBetweenAAndB.uri()
                )
        );
    }

    @Test
    public void anonymous_users_can_get_it_too() {
        vertexUtils().makePublicVerticesWithUri(
                vertexAUri(),
                vertexBUri(),
                vertexCUri()
        );
        SubGraph subGraph = SubGraphJson.fromJson(
                getNonOwnedGraphOfCentralVertexNotAuthenticated(
                        vertexB()
                ).getEntity(JSONObject.class)
        );
        assertFalse(
                subGraph.vertices().isEmpty()
        );
    }

    @Test
    public void works_if_vertices_have_more_than_one_relation_to_each_other() {
        vertexUtils().makePublicVerticesWithUri(
                vertexBUri()
        );
        vertexUtils().makePrivateVerticesWithUri(
                vertexAUri(),
                vertexCUri()
        );
        edgeUtils().addRelationBetweenSourceAndDestinationVertexUri(
                vertexBUri(),
                vertexCUri()
        );
        assertThat(
                getNonOwnedGraphOfCentralVertexNotAuthenticated(
                        vertexB()
                ).getStatus(),
                is(
                        Response.Status.OK.getStatusCode()
                )
        );
    }

    @Test
    public void all_edges_are_removed_when_vertices_have_more_than_one_relation_to_each_other() {
        vertexUtils().makePublicVerticesWithUri(
                vertexBUri()
        );
        vertexUtils().makePrivateVerticesWithUri(
                vertexAUri(),
                vertexCUri()
        );
        edgeUtils().addRelationBetweenSourceAndDestinationVertexUri(
                vertexBUri(),
                vertexCUri()
        );
        SubGraph subGraph = SubGraphJson.fromJson(
                getNonOwnedGraphOfCentralVertexNotAuthenticated(
                        vertexB()
                ).getEntity(JSONObject.class)
        );
        assertThat(
                subGraph.vertices().size(),
                is(1)
        );
        assertThat(
                subGraph.edges().size(),
                is(0)
        );
    }

    @Test
    public void cannot_get_private_vertex_when_no_relations() {
        vertexUtils().removeVertexB();
        vertexUtils().makePrivateVertexWithUri(
                vertexAUri()
        );
        authenticate(createAUser());
        ClientResponse response = getNonOwnedGraphOfCentralVertex(vertexA());
        assertThat(
                response.getStatus(),
                is(Response.Status.FORBIDDEN.getStatusCode())
        );
    }

    private ClientResponse getNonOwnedGraphOfCentralVertex(Vertex vertex) {
        return getNonOwnedGraphOfCentralVertexWithUri(
                vertex.uri()
        );
    }

    private ClientResponse getNonOwnedGraphOfCentralVertexWithUri(URI vertexUri) {
        String shortId = UserUris.graphElementShortId(
                vertexUri
        );
        return resource
                .path(getUsersBaseUri(UserUris.ownerUserNameFromUri(vertexUri)))
                .path("non_owned")
                .path("vertex")
                .path(shortId)
                .path("surround_graph")
                .cookie(authCookie)
                .get(ClientResponse.class);
    }

    private ClientResponse getNonOwnedGraphOfCentralVertexNotAuthenticated(Vertex vertex) {
        String shortId = UserUris.graphElementShortId(
                vertex.uri()
        );
        return resource
                .path(getUsersBaseUri(vertex.getOwnerUsername()))
                .path("non_owned")
                .path("vertex")
                .path(shortId)
                .path("surround_graph")
                .get(ClientResponse.class);
    }
}
