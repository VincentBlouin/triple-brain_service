/*
 * Copyright Vincent Blouin under the Mozilla Public License 1.1
 */

package org.triple_brain.service.vertex;

import com.sun.jersey.api.client.ClientResponse;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.triple_brain.module.model.UserUris;
import org.triple_brain.module.model.graph.GraphElement;
import org.triple_brain.module.model.graph.edge.Edge;
import org.triple_brain.module.model.graph.vertex.Vertex;
import org.triple_brain.module.model.json.LocalizedStringJson;
import org.triple_brain.module.model.json.graph.EdgeJson;
import org.triple_brain.module.model.json.graph.VertexInSubGraphJson;
import org.triple_brain.module.search.EdgeSearchResult;
import org.triple_brain.module.search.VertexSearchResult;
import org.triple_brain.service.utils.GraphManipulationRestTestUtils;

import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertFalse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertTrue;
import static org.triple_brain.module.model.json.StatementJsonFields.*;

public class VertexResourceTest extends GraphManipulationRestTestUtils {

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
        ).size();
        vertexUtils().addAVertexToVertexAWithUri(vertexAUri());
        int updatedNumberOfConnectedEdges = vertexUtils().connectedEdgesOfVertexWithURI(
                vertexAUri()
        ).size();
        assertThat(updatedNumberOfConnectedEdges, is(numberOfConnectedEdges + 1));
    }

    @Test
    public void adding_a_vertex_returns_the_new_edge_and_vertex_id() throws Exception {
        ClientResponse response = vertexUtils().addAVertexToVertexAWithUri(vertexAUri());
        JSONObject createdStatement = response.getEntity(JSONObject.class);
        Vertex subject = VertexInSubGraphJson.fromJson(
                createdStatement.getJSONObject(SOURCE_VERTEX)
        );
        assertThat(
                subject.uri().toString(),
                is(vertexAUri().toString())
        );
        Edge newEdge = EdgeJson.fromJson(
                createdStatement.getJSONObject(EDGE)
        );
        Vertex newVertex = VertexInSubGraphJson.fromJson(
                createdStatement.getJSONObject(END_VERTEX)
        );
        Set<Edge> edgesOfVertexA = vertexUtils().connectedEdgesOfVertexWithURI(
                vertexAUri()
        );
        assertTrue(
                edgesOfVertexA.contains(
                        newEdge
                )
        );
        assertTrue(
                vertexUtils().vertexWithUriHasDestinationVertexWithUri(
                        vertexAUri(),
                        newVertex.uri()
                )
        );
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
        vertexUtils().removeVertexB();
        assertFalse(graphElementWithIdExistsInCurrentGraph(
                vertexBUri()
        ));
    }

    @Test
    public void removing_vertex_returns_correct_response_status() throws Exception {
        ClientResponse response = vertexUtils().removeVertexB();
        assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
    }

    @Test
    public void can_update_label() throws Exception {
        String vertexALabel = vertexA().label();
        assertThat(vertexALabel, is(not("new vertex label")));
        vertexUtils().updateVertexLabelUsingRest(
                vertexAUri(),
                "new vertex label"
        );
        vertexALabel = vertexA().label();
        assertThat(vertexALabel, is("new vertex label"));
    }
    @Test
    public void label_can_have_special_characters() {
        String vertexALabel = vertexA().label();
        assertThat(vertexALabel, is(not("a(test*)")));
        vertexUtils().updateVertexLabelUsingRest(
                vertexAUri(),
                "a(test*)"
        );
        vertexALabel = vertexA().label();
        assertThat(vertexALabel, is("a(test*)"));
    }

    @Test
    public void can_update_note() throws Exception {
        String vertexANote = vertexA().comment();
        assertThat(vertexANote, is(not("some note")));
        vertexUtils().updateVertexANote("some note");
        vertexANote = vertexA().comment();
        assertThat(vertexANote, is("some note"));
    }

    @Test
    public void updating_label_returns_correct_status() throws Exception {
        ClientResponse response = vertexUtils().updateVertexLabelUsingRest(
                vertexAUri(),
                "new vertex label"
        );
        assertThat(response.getStatus(), is(Response.Status.NO_CONTENT.getStatusCode()));
    }

    @Test
    public void updating_note_updates_search() throws Exception {
        indexGraph();
        GraphElement resultsForA = searchUtils().autoCompletionResultsForCurrentUserVerticesOnly(
                vertexA().label()
        ).get(0).getGraphElement();
        assertThat(resultsForA.comment(), is(""));
        vertexUtils().updateVertexANote(
                "A description"
        );
        resultsForA = searchUtils().searchByUri(vertexAUri()).getGraphElement();
        assertThat(
                resultsForA.comment(), is("A description")
        );
    }

    @Test
    public void when_deleting_a_vertex_its_relations_are_also_removed_from_search() {
        indexGraph();
        List<EdgeSearchResult> relations = searchUtils().searchForRelations(
                "between",
                defaultAuthenticatedUserAsJson
        );
        assertThat(
                relations.size(),
                is(2)
        );
        vertexUtils().removeVertexB();
        relations = searchUtils().searchForRelations(
                "between",
                defaultAuthenticatedUserAsJson
        );
        assertThat(
                relations.size(),
                is(0)
        );
    }

    @Test
    public void making_vertex_public_re_indexes_it() throws Exception {
        indexGraph();
        JSONObject anotherUser = createAUser();
        authenticate(
                anotherUser
        );
        List<VertexSearchResult> results = searchUtils().autoCompletionResultsForPublicAndUserVertices(
                vertexA().label(),
                anotherUser
        );
        assertThat(
                results.size(), is(0)
        );
        authenticate(defaultAuthenticatedUser);
        vertexUtils().makePublicVertexWithUri(
                vertexAUri()
        );
        authenticate(anotherUser);
        results = searchUtils().autoCompletionResultsForPublicAndUserVertices(
                vertexA().label(),
                anotherUser
        );
        assertThat(
                results.size(),
                is(1)
        );
    }

    @Test
    public void making_vertex_private_re_indexes_it() throws Exception {
        vertexUtils().makePublicVertexWithUri(
                vertexAUri()
        );
        indexGraph();
        JSONObject anotherUser = createAUser();
        authenticate(anotherUser);
        JSONArray results = searchUtils().clientResponseOfAutoCompletionForPublicAndUserOwnedVertices(
                vertexA().label(),
                anotherUser
        ).getEntity(JSONArray.class);
        Assert.assertThat(results.length(), Is.is(greaterThan(0)));
        authenticate(defaultAuthenticatedUser);
        vertexUtils().makePrivateVertexWithUri(
                vertexAUri()
        );
        authenticate(anotherUser);
        results = searchUtils().clientResponseOfAutoCompletionForPublicAndUserOwnedVertices(
                vertexA().label(),
                anotherUser
        ).getEntity(JSONArray.class);
        Assert.assertThat(results.length(), Is.is(0));
    }

    @Test
    public void number_of_connected_vertices_are_included() throws Exception {
        assertThat(
                vertexB().getNumberOfConnectedEdges(),
                is(2)
        );
    }
}
