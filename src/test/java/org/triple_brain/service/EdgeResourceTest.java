package org.triple_brain.service;

import com.sun.jersey.api.client.ClientResponse;
import org.codehaus.jettison.json.JSONException;
import org.junit.Test;
import org.triple_brain.module.model.UserUris;
import org.triple_brain.module.model.graph.edge.Edge;
import org.triple_brain.module.search.VertexSearchResult;
import org.triple_brain.service.utils.GraphManipulationRestTest;

import javax.ws.rs.core.Response;

import static junit.framework.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertFalse;
import static org.triple_brain.module.common_utils.Uris.encodeURL;

/**
 * Copyright Mozilla Public License 1.1
 */
public class EdgeResourceTest extends GraphManipulationRestTest {

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
                graphUtils().wholeGraph().edges()
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
                graphUtils().wholeGraph().containsEdge(
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
    public void updating_edge_labels_reflects_in_search_for_connected_vertices() throws Exception {
        indexGraph();
        VertexSearchResult searchResultA = searchUtils().autoCompletionResultsForCurrentUserVerticesOnly(
                vertexA().label()
        ).get(0);
        assertTrue(searchResultA.getRelationsName().contains(
                "between vertex A and vertex B"
        ));
        VertexSearchResult searchResultB = searchUtils().autoCompletionResultsForCurrentUserVerticesOnly(
                vertexB().label()
        ).get(0);
        assertTrue(searchResultB.getRelationsName().contains(
                "between vertex A and vertex B"
        ));
        edgeUtils().updateEdgeLabel(
                "new edge text !",
                edgeUtils().edgeBetweenAAndB()
        );
        searchResultA = searchUtils().autoCompletionResultsForCurrentUserVerticesOnly(
                vertexA().label()
        ).get(0);
        assertFalse(searchResultA.getRelationsName().contains(
                "between vertex A and vertex B"
        ));
        assertTrue(searchResultA.getRelationsName().contains(
                "new edge text !"
        ));
        searchResultB = searchUtils().autoCompletionResultsForCurrentUserVerticesOnly(
                vertexB().label()
        ).get(0);
        assertFalse(searchResultB.getRelationsName().contains(
                "between vertex A and vertex B"
        ));
        assertTrue(searchResultB.getRelationsName().contains(
                "new edge text !"
        ));
    }

    @Test
    public void deleting_edge_removes_relations_name_of_connected_vertices_in_search() throws Exception {
        indexGraph();
        VertexSearchResult searchResultA = searchUtils().autoCompletionResultsForCurrentUserVerticesOnly(
                vertexA().label()
        ).get(0);
        assertTrue(
                searchResultA.hasRelations()
        );
        edgeUtils().removeEdgeBetweenVertexAAndB();
        searchResultA = searchUtils().autoCompletionResultsForCurrentUserVerticesOnly(
                vertexA().label()
        ).get(0);
        assertFalse(
                searchResultA.hasRelations()
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
                graphUtils().wholeGraph().edges()
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
                graphUtils().wholeGraph().edges()
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
                graphUtils().wholeGraph().edges()
        );
        ClientResponse response = resource
                .path(edgeBetweenAAndC.uri().toString())
                .path("inverse")
                .cookie(authCookie)
                .put(ClientResponse.class);
        return response;
    }
}
