/*
 * Copyright Vincent Blouin under the Mozilla Public License 1.1
 */

package org.triple_brain.service;

import com.sun.jersey.api.client.ClientResponse;
import org.codehaus.jettison.json.JSONException;
import org.junit.Test;
import org.triple_brain.module.model.graph.edge.Edge;
import org.triple_brain.service.utils.GraphManipulationRestTestUtils;

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
                graphUtils().graphWithCenterVertexUri(vertexAUri()).edges()
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
}
