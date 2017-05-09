/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources;

import com.sun.jersey.api.client.ClientResponse;
import guru.bubl.module.common_utils.Uris;
import guru.bubl.module.model.UserUris;
import guru.bubl.module.model.graph.subgraph.SubGraph;
import guru.bubl.module.model.graph.vertex.Vertex;
import guru.bubl.module.model.graph.SubGraphJson;
import guru.bubl.service.utils.GraphManipulationRestTestUtils;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;
import guru.bubl.module.model.graph.edge.Edge;

import javax.ws.rs.core.Response;

import static junit.framework.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertFalse;

public class EdgeResourceTest extends GraphManipulationRestTestUtils {

    @Test
    public void can_add_a_relation() {
        assertFalse(vertexUtils().vertexWithUriHasDestinationVertexWithUri(
                vertexAUri(),
                vertexCUri()
        ));
        edgeUtils().addRelationBetweenVertexAAndC();
        assertTrue(vertexUtils().vertexWithUriHasDestinationVertexWithUri(
                vertexAUri(),
                vertexCUri()
        ));
    }

    @Test
    public void adding_a_relation_returns_correct_response_status() {
        ClientResponse response = edgeUtils().addRelationBetweenVertexAAndC();
        assertThat(
                response.getStatus(), is(
                        Response.Status.CREATED.getStatusCode()
                ));
    }

    @Test
    public void adding_a_relation_returns_correct_headers() {
        ClientResponse response = edgeUtils().addRelationBetweenVertexAAndC();
        Edge edgeBetweenAAndC = edgeUtils().edgeBetweenTwoVerticesUriGivenEdges(
                vertexAUri(),
                vertexCUri(),
                graphUtils().graphWithCenterVertexUri(vertexAUri()).edges()
        );
        try {
            assertThat(
                    Uris.decodeUrlSafe(
                            response.getHeaders().get("Location").get(0)
                    ),
                    is(
                            BASE_URI + edgeBetweenAAndC.uri().toString()
                    ));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    @Test
    public void can_remove_a_relation() {
        Edge edgeBetweenAAndB = edgeUtils().edgeBetweenAAndB();
        edgeUtils().removeEdgeBetweenVertexAAndB();
        assertFalse(
                graphUtils().graphWithCenterVertexUri(
                        vertexAUri()
                ).containsEdge(
                        edgeBetweenAAndB
                )
        );
    }

    @Test
    public void removing_a_relation_returns_correct_status() {
        ClientResponse response = edgeUtils().removeEdgeBetweenVertexAAndB();
        assertThat(
                response.getStatus(), is(
                        Response.Status.OK.getStatusCode()
                ));
    }

    @Test
    public void can_update_label() {
        Edge edgeBetweenAAndB = edgeUtils().edgeBetweenAAndB();
        assertThat(
                edgeBetweenAAndB.label(),
                is(not("new edge label"))
        );
        updateEdgeLabelBetweenAAndB("new edge label");
        edgeBetweenAAndB = edgeUtils().edgeBetweenAAndB();
        assertThat(
                edgeBetweenAAndB.label(),
                is("new edge label")
        );
    }

    @Test
    public void updating_label_returns_correct_status() {
        ClientResponse response = updateEdgeLabelBetweenAAndB("new edge label");
        assertThat(
                response.getStatus(), is(
                        Response.Status.OK.getStatusCode()
                ));
    }

    private ClientResponse updateEdgeLabelBetweenAAndB(String label) {
        return edgeUtils().updateEdgeLabel(
                label,
                edgeUtils().edgeBetweenAAndB()
        );
    }

    @Test
    public void inverseReturnsCorrectStatus() {
        assertThat(
                inverseRelationBetweenAAndB().getStatus(), is(
                        Response.Status.OK.getStatusCode()
                ));
    }

    @Test
    public void can_inverse() {
        Edge edgeBetweenAAndB = edgeUtils().edgeBetweenTwoVerticesUriGivenEdges(
                vertexAUri(),
                vertexBUri(),
                graphUtils().graphWithCenterVertexUri(vertexAUri()).edges()
        );
        assertThat(
                edgeBetweenAAndB.sourceVertex().uri().toString(), is(
                        vertexAUri().toString()
                ));
        assertThat(
                edgeBetweenAAndB.destinationVertex().uri().toString(), is(
                        vertexBUri().toString()
                ));
        inverseRelationBetweenAAndB();
        edgeBetweenAAndB = edgeUtils().edgeBetweenTwoVerticesUriGivenEdges(
                vertexAUri(),
                vertexBUri(),
                graphUtils().graphWithCenterVertexUri(vertexBUri()).edges()
        );
        assertThat(
                edgeBetweenAAndB.sourceVertex().uri().toString(), is(
                        vertexBUri().toString()
                ));
        assertThat(
                edgeBetweenAAndB.destinationVertex().uri().toString(), is(
                        vertexAUri().toString()
                ));
    }

    @Test
    public void updating_note_returns_correct_status() {
        Edge edgeBetweenAAndB = edgeUtils().edgeBetweenTwoVerticesUriGivenEdges(
                vertexAUri(),
                vertexBUri(),
                graphUtils().graphWithCenterVertexUri(vertexAUri()).edges()
        );
        ClientResponse response = edgeUtils().updateNoteOfEdge(
                "some note",
                edgeBetweenAAndB
        );
        assertThat(
                response.getStatus(),
                is(Response.Status.NO_CONTENT.getStatusCode())
        );
    }

    @Test
    public void can_update_note() {
        Edge edgeBetweenAAndB = edgeUtils().edgeBetweenTwoVerticesUriGivenEdges(
                vertexAUri(),
                vertexBUri(),
                graphUtils().graphWithCenterVertexUri(vertexAUri()).edges()
        );
        assertThat(
                edgeBetweenAAndB.comment(),
                is(
                        not("some note")
                )
        );
        edgeUtils().updateNoteOfEdge("some note", edgeBetweenAAndB);
        edgeBetweenAAndB = edgeUtils().edgeBetweenTwoVerticesUriGivenEdges(
                vertexAUri(),
                vertexBUri(),
                graphUtils().graphWithCenterVertexUri(vertexAUri()).edges()
        );
        assertThat(
                edgeBetweenAAndB.comment(),
                is(
                        "some note"
                )
        );
    }

    @Test
    public void changing_source_vertex_returns_correct_status() {
        Edge edgeBetweenBAndC = edgeUtils().edgeBetweenTwoVerticesUriGivenEdges(
                vertexBUri(),
                vertexCUri(),
                graphUtils().graphWithCenterVertexUri(vertexBUri()).edges()
        );
        ClientResponse response = changeSourceVertex(
                edgeBetweenBAndC,
                vertexA()
        );
        assertThat(
                response.getStatus(),
                is(
                        Response.Status.NO_CONTENT.getStatusCode()
                )
        );
    }

    @Test
    public void can_change_source_vertex() {
        Edge edge = edgeUtils().edgeBetweenTwoVerticesUriGivenEdges(
                vertexBUri(),
                vertexCUri(),
                graphUtils().graphWithCenterVertexUri(vertexBUri()).edges()

        );
        SubGraph graph = graphUtils().graphWithCenterVertexUri(
                vertexAUri()
        );
        assertFalse(
                graph.containsEdge(edge)
        );
        changeSourceVertex(
                edge,
                vertexA()
        );
        graph = graphUtils().graphWithCenterVertexUri(
                vertexAUri()
        );
        assertTrue(
                graph.containsEdge(edge)
        );
    }

    @Test
    public void changing_destination_vertex_returns_correct_status() {
        Edge edgeBetweenAAndB = edgeUtils().edgeBetweenTwoVerticesUriGivenEdges(
                vertexAUri(),
                vertexBUri(),
                graphUtils().graphWithCenterVertexUri(vertexAUri()).edges()
        );
        ClientResponse response = changeDestinationVertex(
                edgeBetweenAAndB,
                vertexC()
        );
        assertThat(
                response.getStatus(),
                is(
                        Response.Status.NO_CONTENT.getStatusCode()
                )
        );
    }

    @Test
    public void can_change_destination_vertex() {
        Edge edge = edgeUtils().edgeBetweenTwoVerticesUriGivenEdges(
                vertexAUri(),
                vertexBUri(),
                graphUtils().graphWithCenterVertexUri(vertexAUri()).edges()

        );
        SubGraph graph = graphUtils().graphWithCenterVertexUri(
                vertexCUri()
        );
        assertFalse(
                graph.containsEdge(edge)
        );
        changeDestinationVertex(
                edge,
                vertexC()
        );
        graph = graphUtils().graphWithCenterVertexUri(
                vertexCUri()
        );
        assertTrue(
                graph.containsEdge(edge)
        );
    }

    @Test
    public void getting_surround_graph_returns_ok_status() {
        assertThat(
                getSurroundGraphOfEdgeBetweenAAndB().getStatus(),
                is(Response.Status.OK.getStatusCode())
        );
    }

    @Test
    public void getting_surround_graph_of_edge_returns_surround_graph_of_source_vertex() {
        SubGraph subGraph = SubGraphJson.fromJson(
                getSurroundGraphOfEdgeBetweenAAndB().getEntity(
                        JSONObject.class
                )
        );
        assertTrue(
                subGraph.containsVertex(vertexA())
        );
        assertTrue(
                subGraph.containsVertex(vertexB())
        );
        assertFalse(
                subGraph.containsVertex(vertexC())
        );
    }

    private ClientResponse getSurroundGraphOfEdgeBetweenAAndB() {
        Edge edgeBetweenAAndB = edgeUtils().edgeBetweenTwoVerticesUriGivenEdges(
                vertexAUri(),
                vertexBUri(),
                graphUtils().graphWithCenterVertexUri(vertexAUri()).edges()
        );
        return resource
                .path(edgeBetweenAAndB.uri().toString())
                .path("surround_graph")
                .cookie(authCookie)
                .get(ClientResponse.class);
    }


    private ClientResponse inverseRelationBetweenAAndB() {
        Edge edgeBetweenAAndB = edgeUtils().edgeBetweenTwoVerticesUriGivenEdges(
                vertexAUri(),
                vertexBUri(),
                graphUtils().graphWithCenterVertexUri(vertexAUri()).edges()
        );
        return resource
                .path(edgeBetweenAAndB.uri().toString())
                .path("inverse")
                .cookie(authCookie)
                .put(ClientResponse.class);
    }

    private ClientResponse changeSourceVertex(Edge edge, Vertex newSourceVertex) {
        return resource
                .path(edge.uri().toString())
                .path("source-vertex")
                .path(UserUris.graphElementShortId(newSourceVertex.uri()))
                .cookie(authCookie)
                .put(ClientResponse.class);
    }

    private ClientResponse changeDestinationVertex(Edge edge, Vertex newDestinationVertex) {
        return resource
                .path(edge.uri().toString())
                .path("destination-vertex")
                .path(UserUris.graphElementShortId(newDestinationVertex.uri()))
                .cookie(authCookie)
                .put(ClientResponse.class);
    }
}
