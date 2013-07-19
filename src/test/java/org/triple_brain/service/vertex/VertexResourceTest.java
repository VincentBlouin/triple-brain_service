package org.triple_brain.service.vertex;

import com.sun.jersey.api.client.ClientResponse;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;
import org.triple_brain.module.common_utils.Uris;
import org.triple_brain.module.model.UserUris;
import org.triple_brain.module.model.json.graph.EdgeJsonFields;
import org.triple_brain.module.model.json.graph.VertexJsonFields;
import org.triple_brain.service.utils.GraphManipulationRestTest;

import javax.ws.rs.core.Response;

import static junit.framework.Assert.assertFalse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertTrue;
import static org.triple_brain.module.model.json.StatementJsonFields.*;

/**
 * Copyright Mozilla Public License 1.1
 */

public class VertexResourceTest extends GraphManipulationRestTest {

    @Test
    public void adding_a_vertex_returns_correct_status() throws Exception {
        ClientResponse response = vertexUtils().addAVertexToVertexAWithUri(
                vertexAUri()
        );
        assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
    }

    @Test
    public void can_add_a_vertex() throws Exception {
        int numberOfConnectedEdges = vertexUtils().connectedEdgesOfVertexWithURI(
                vertexAUri()
        ).length();
        vertexUtils().addAVertexToVertexAWithUri(vertexAUri());
        int updatedNumberOfConnectedEdges = vertexUtils().connectedEdgesOfVertexWithURI(
                vertexAUri()
        ).length();
        assertThat(updatedNumberOfConnectedEdges, is(numberOfConnectedEdges + 1));
    }

    @Test
    public void adding_a_vertex_returns_the_new_edge_and_vertex_id() throws Exception {
        ClientResponse response = vertexUtils().addAVertexToVertexAWithUri(vertexAUri());
        JSONObject createdStatement = response.getEntity(JSONObject.class);
        JSONObject subject = createdStatement.getJSONObject(SOURCE_VERTEX);
        assertThat(subject.getString(VertexJsonFields.ID), is(vertexAUri().toString()));
        JSONObject newEdge = edgeUtils().edgeWithUri(
                Uris.get(
                        createdStatement.getJSONObject(EDGE).getString(
                                EdgeJsonFields.ID
                        )
                )
        );
        JSONObject newVertex = vertexUtils().vertexWithUri(
                Uris.get(
                        createdStatement.getJSONObject(END_VERTEX).getString(
                                VertexJsonFields.ID
                        )
                )
        );
        JSONArray edgesOfVertexA = vertexUtils().connectedEdgesOfVertexWithURI(
                vertexAUri()
        );
        assertTrue(edgeUtils().edgeIsInEdges(newEdge, edgesOfVertexA));
        assertTrue(vertexUtils().vertexWithUriHasDestinationVertexWithUri(
                vertexAUri(),
                vertexUtils().uriOfVertex(newVertex)
        ));
    }

    @Test
    public void cannot_add_a_vertex_that_user_doesnt_own() throws Exception {
        authenticate(createAUser());
        ClientResponse response = resource
                .path(
                        new UserUris(
                                defaultAuthenticatedUser
                        ).defaultVertexUri().getPath()
                )
                .cookie(authCookie)
                .post(ClientResponse.class);
        assertThat(response.getStatus(), is(Response.Status.FORBIDDEN.getStatusCode()));
    }

    @Test
    public void can_remove_a_vertex() throws Exception {
        assertTrue(graphElementWithIdExistsInCurrentGraph(
                vertexBUri()
        ));
        removeVertexB();
        assertFalse(graphElementWithIdExistsInCurrentGraph(
                vertexBUri()
        ));
    }

    @Test
    public void removing_vertex_returns_correct_response_status() throws Exception {
        ClientResponse response = removeVertexB();
        assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
    }

    private ClientResponse removeVertexB() throws Exception {
        ClientResponse response = resource
                .path(vertexBUri().getPath())
                .cookie(authCookie)
                .delete(ClientResponse.class);
        return response;
    }

    @Test
    public void can_update_label() throws Exception {
        String vertexALabel = vertexA().getString(VertexJsonFields.LABEL);
        assertThat(vertexALabel, is(not("new vertex label")));
        updateVertexALabelUsingRest("new vertex label");
        vertexALabel = vertexA().getString(VertexJsonFields.LABEL);
        assertThat(vertexALabel, is("new vertex label"));
    }

    @Test
    public void can_update_note() throws Exception {
        String vertexANote = vertexA().getString(VertexJsonFields.NOTE);
        assertThat(vertexANote, is(not("some note")));
        vertexUtils().updateVertexANote("some note");
        vertexANote = vertexA().getString(VertexJsonFields.NOTE);
        assertThat(vertexANote, is("some note"));
    }

    @Test
    public void updating_label_returns_correct_status() throws Exception {
        ClientResponse response = updateVertexALabelUsingRest("new vertex label");
        assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
    }

    private ClientResponse updateVertexALabelUsingRest(String label) throws Exception {
        ClientResponse response = resource
                .path(vertexAUri().getPath())
                .path("label")
                .queryParam("label", label)
                .cookie(authCookie)
                .post(ClientResponse.class);
        return response;
    }
}
