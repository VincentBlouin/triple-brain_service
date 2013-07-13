package org.triple_brain.service;

import com.sun.jersey.api.client.ClientResponse;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;
import org.triple_brain.service.utils.GraphManipulationRestTest;
import org.triple_brain.module.model.UserUris;
import org.triple_brain.module.model.json.graph.EdgeJsonFields;
import org.triple_brain.module.model.json.graph.GraphJSONFields;

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
        addRelationBetweenVertexAAndC();
        assertTrue(vertexUtils().vertexWithUriHasDestinationVertexWithUri(
                vertexAUri(),
                vertexCUri()
        ));
    }

    @Test
    public void adding_a_relation_returns_correct_response_status() throws Exception{
        ClientResponse response = addRelationBetweenVertexAAndC();
        assertThat(response.getStatus(), is(201));
    }

    @Test
    public void adding_a_relation_returns_correct_headers()throws JSONException{
        ClientResponse response = addRelationBetweenVertexAAndC();
        JSONArray allEdges = graphUtils().wholeGraph().getJSONArray(
                GraphJSONFields.EDGES
        );
        JSONObject edgeBetweenAAndC = edgeUtils().edgeBetweenTwoVerticesUriGivenEdges(
                vertexAUri(),
                vertexCUri(),
                allEdges
        );
        assertThat(
            response.getHeaders().get("Location").get(0),
            is(
                BASE_URI + edgeBetweenAAndC.getString(
                        EdgeJsonFields.ID
                )
            )
        );
    }

    private ClientResponse addRelationBetweenVertexAAndC(){
        ClientResponse response = resource
                .path(new UserUris(defaultAuthenticatedUser).baseEdgeUri().getPath())
                .queryParam("sourceVertexId", encodeURL(vertexAUri().toString()))
                .queryParam("destinationVertexId", encodeURL(vertexCUri().toString()))
                .cookie(authCookie)
                .post(ClientResponse.class);
        return response;
    }

    @Test
    public void can_remove_a_relation() throws Exception {
        JSONObject edgeBetweenAAndB = edgeUtils().edgeBetweenAAndB();
        edgeUtils().removeEdgeBetweenVertexAAndB();
        JSONArray allEdges = graphUtils().wholeGraph().getJSONArray(
                GraphJSONFields.EDGES
        );
        assertFalse(
                edgeUtils().edgeIsInEdges(
                        edgeBetweenAAndB, allEdges
                )
        );
    }

    @Test
    public void removing_a_relation_returns_correct_status() throws Exception{
        ClientResponse response = edgeUtils().removeEdgeBetweenVertexAAndB();
        assertThat(response.getStatus(), is(200));
    }

    @Test
    public void can_update_label() throws Exception {
        JSONObject edgeBetweenAAndB = edgeUtils().edgeBetweenAAndB();
        assertThat(
                edgeBetweenAAndB.getString(EdgeJsonFields.LABEL),
                is(not("new edge label"))
        );
        updateEdgeLabelBetweenAAndB("new edge label");
        edgeBetweenAAndB = edgeUtils().edgeBetweenAAndB();
        assertThat(
                edgeBetweenAAndB.getString(EdgeJsonFields.LABEL),
                is("new edge label")
        );
    }

    @Test
    public void updating_label_returns_correct_status() throws Exception {
        ClientResponse response = updateEdgeLabelBetweenAAndB("new edge label");
        assertThat(response.getStatus(), is(200));
    }

    private ClientResponse updateEdgeLabelBetweenAAndB(String label)throws Exception{
        return edgeUtils().updateEdgeLabel(
                label,
                edgeUtils().edgeBetweenAAndB()
        );
    }
}
