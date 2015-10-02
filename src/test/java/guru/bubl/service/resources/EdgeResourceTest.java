/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources;

import com.sun.jersey.api.client.ClientResponse;
import guru.bubl.module.model.UserUris;
import guru.bubl.module.model.graph.SubGraph;
import guru.bubl.module.model.graph.vertex.Vertex;
import guru.bubl.service.utils.GraphManipulationRestTestUtils;
import org.codehaus.jettison.json.JSONException;
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
    public void can_add_a_relation() throws Exception {
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
    public void adding_a_relation_returns_correct_response_status() throws Exception {
        ClientResponse response = edgeUtils().addRelationBetweenVertexAAndC();
        assertThat(
                response.getStatus(), is(
                        Response.Status.CREATED.getStatusCode()
                ));
    }

    @Test
    public void adding_a_relation_returns_correct_headers() throws JSONException {
        ClientResponse response = edgeUtils().addRelationBetweenVertexAAndC();
        Edge edgeBetweenAAndC = edgeUtils().edgeBetweenTwoVerticesUriGivenEdges(
                vertexAUri(),
                vertexCUri(),
                graphUtils().graphWithCenterVertexUri(vertexAUri()).edges()
        );
        assertThat(
                response.getHeaders().get("Location").get(0),
                is(
                        BASE_URI + edgeBetweenAAndC.uri().toString()
                )
        );
    }


    @Test
    public void can_remove_a_relation() throws Exception {
        Edge edgeBetweenAAndB = edgeUtils().edgeBetweenAAndB();
        edgeUtils().removeEdgeBetweenVertexAAndB();
        assertFalse(
                graphUtils().graphWithCenterVertexUri(vertexAUri()).containsEdge(
                        edgeBetweenAAndB
                )
        );
    }

    @Test
    public void removing_a_relation_returns_correct_status() throws Exception {
        ClientResponse response = edgeUtils().removeEdgeBetweenVertexAAndB();
        assertThat(
                response.getStatus(), is(
                        Response.Status.OK.getStatusCode()
                ));
    }

    @Test
    public void can_update_label() throws Exception {
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
    public void updating_label_returns_correct_status() throws Exception {
        ClientResponse response = updateEdgeLabelBetweenAAndB("new edge label");
        assertThat(
                response.getStatus(), is(
                        Response.Status.OK.getStatusCode()
                ));
    }

    private ClientResponse updateEdgeLabelBetweenAAndB(String label) throws Exception {
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
        Edge edgeBetweenAAndC = edgeUtils().edgeBetweenTwoVerticesUriGivenEdges(
                vertexAUri(),
                vertexBUri(),
                graphUtils().graphWithCenterVertexUri(vertexAUri()).edges()
        );
        assertThat(
                edgeBetweenAAndC.sourceVertex().uri().toString(), is(
                        vertexAUri().toString()
                ));
        assertThat(
                edgeBetweenAAndC.destinationVertex().uri().toString(), is(
                        vertexBUri().toString()
                ));
        inverseRelationBetweenAAndB();
        edgeBetweenAAndC = edgeUtils().edgeBetweenTwoVerticesUriGivenEdges(
                vertexAUri(),
                vertexBUri(),
                graphUtils().graphWithCenterVertexUri(vertexBUri()).edges()
        );
        assertThat(
                edgeBetweenAAndC.sourceVertex().uri().toString(), is(
                        vertexBUri().toString()
                ));
        assertThat(
                edgeBetweenAAndC.destinationVertex().uri().toString(), is(
                        vertexAUri().toString()
                ));
    }

    @Test
    public void updating_note_returns_correct_status() throws Exception {
        Edge edgeBetweenAAndC = edgeUtils().edgeBetweenTwoVerticesUriGivenEdges(
                vertexAUri(),
                vertexBUri(),
                graphUtils().graphWithCenterVertexUri(vertexAUri()).edges()
        );
        ClientResponse response = edgeUtils().updateNoteOfEdge(
                "some note",
                edgeBetweenAAndC
        );
        assertThat(
                response.getStatus(),
                is(Response.Status.NO_CONTENT.getStatusCode())
        );
    }

    @Test
    public void can_update_note() throws Exception {
        Edge edgeBetweenAAndC = edgeUtils().edgeBetweenTwoVerticesUriGivenEdges(
                vertexAUri(),
                vertexBUri(),
                graphUtils().graphWithCenterVertexUri(vertexAUri()).edges()
        );
        assertThat(
                edgeBetweenAAndC.comment(),
                is(
                        not("some note")
                )
        );
        edgeUtils().updateNoteOfEdge("some note", edgeBetweenAAndC);
        edgeBetweenAAndC = edgeUtils().edgeBetweenTwoVerticesUriGivenEdges(
                vertexAUri(),
                vertexBUri(),
                graphUtils().graphWithCenterVertexUri(vertexAUri()).edges()
        );
        assertThat(
                edgeBetweenAAndC.comment(),
                is(
                        "some note"
                )
        );
    }

    @Test
    public void changing_source_vertex_returns_correct_status() throws Exception {
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
    public void can_change_source_vertex() throws Exception {
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

    private ClientResponse inverseRelationBetweenAAndB() {
        Edge edgeBetweenAAndC = edgeUtils().edgeBetweenTwoVerticesUriGivenEdges(
                vertexAUri(),
                vertexBUri(),
                graphUtils().graphWithCenterVertexUri(vertexAUri()).edges()
        );
        return resource
                .path(edgeBetweenAAndC.uri().toString())
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
}
