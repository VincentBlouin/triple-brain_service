package org.triple_brain.service;

import com.google.gson.Gson;
import com.sun.jersey.api.client.ClientResponse;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;
import org.triple_brain.module.model.graph.GraphElement;
import org.triple_brain.module.model.json.UserJson;
import org.triple_brain.module.search.EdgeSearchResult;
import org.triple_brain.module.search.GraphElementSearchResult;
import org.triple_brain.module.search.VertexSearchResult;
import org.triple_brain.service.utils.GraphManipulationRestTest;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/*
* Copyright Mozilla Public License 1.1
*/
public class SearchResourceTest extends GraphManipulationRestTest {

    @Test
    public void can_search_vertices_for_auto_complete() throws Exception {
        indexGraph();
        ClientResponse response = resource
                .path("service")
                .path("users")
                .path(defaultAuthenticatedUser.username())
                .path("search")
                .path("own_vertices")
                .path("auto_complete")
                .queryParam("text", "vert")
                .cookie(authCookie)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get(ClientResponse.class);
        List<VertexSearchResult> results = searchUtils().vertexSearchResultsFromResponse(
                response
        );
        assertThat(results.size(), is(3));
    }

    @Test
    public void search_for_auto_complete_can_have_spaces() throws Exception {
        indexGraph();
        List<VertexSearchResult> searchResults = searchUtils().autoCompletionResultsForCurrentUserVerticesOnly(
                "vertex Azu"
        );
        GraphElement firstResult = searchResults.get(0).getGraphElement();
        assertThat(
                firstResult.label(),
                is("vertex Azure")
        );
        firstResult = searchUtils().autoCompletionResultsForCurrentUserVerticesOnly(
                "vertex Bar"
        ).get(0).getGraphElement();
        assertThat(
                firstResult.label(),
                is("vertex Bareau")
        );
    }

    @Test
    public void can_search_for_only_own_vertices() throws Exception {
        vertexUtils().makePublicVertexWithUri(
                vertexAUri()
        );
        indexGraph();
        JSONObject anotherUser = createAUser();
        authenticate(anotherUser);
        List<VertexSearchResult> results = searchUtils().autoCompletionResultsForPublicAndUserVertices(
                vertexA().label(),
                anotherUser
        );
        assertThat(results.size(), is(greaterThan(0)));
        results = searchUtils().autoCompletionResultsForUserVerticesOnly(
                vertexA().label(),
                anotherUser
        );
        assertThat(results.size(), is(0));
    }

    @Test
    public void can_search_relations() {
        indexGraph();
        List<EdgeSearchResult> relations = searchUtils().searchForRelations(
                "between",
                defaultAuthenticatedUserAsJson
        );
        assertThat(relations.size(), is(2));
    }


    @Test
    public void removing_vertex_removes_relations_name_of_edge_of_connected_vertices() throws Exception {
        indexGraph();
        VertexSearchResult searchResultA = searchUtils().autoCompletionResultsForCurrentUserVerticesOnly(
                vertexA().label()
        ).get(0);
        assertTrue(searchResultA.hasRelations());
        vertexUtils().removeVertexB();
        searchResultA = searchUtils().autoCompletionResultsForCurrentUserVerticesOnly(
                vertexA().label()
        ).get(0);
        assertFalse(searchResultA.hasRelations());
    }

    @Test
    public void getting_search_result_by_uri_returns_correct_status() {
        indexGraph();
        ClientResponse clientResponse = searchByUriClientResponse(vertexAUri());
        assertThat(
                clientResponse.getStatus(),
                is(Response.Status.OK.getStatusCode())
        );
    }

    @Test
    public void can_get_search_result_by_uri() {
        indexGraph();
        GraphElement result = searchByUri(
                vertexAUri()
        ).getGraphElement();
        assertThat(
                result.label(),
                is("vertex Azure")
        );
    }

    @Test
    public void search_by_uri_returns_vertex_specific_properties_if_vertex() {
        indexGraph();
        VertexSearchResult result = (VertexSearchResult) searchByUri(
                vertexA().uri()
        );
        assertTrue(result.getRelationsName().contains(
                "between vertex A and vertex B"
        ));
    }

    private GraphElementSearchResult searchByUri(URI uri) {
        JSONObject jsonObject = searchByUriClientResponse(
                uri
        ).getEntity(JSONObject.class);
        Gson gson = new Gson();
        return jsonObject.has("edge") ?
                gson.fromJson(
                        jsonObject.toString(),
                        EdgeSearchResult.class
                ) :
                gson.fromJson(
                        jsonObject.toString(),
                        VertexSearchResult.class
                );
    }

    private ClientResponse searchByUriClientResponse(URI uri) {
        ClientResponse clientResponse = resource
                .path("service")
                .path("users")
                .path(defaultAuthenticatedUserAsJson.optString(UserJson.USER_NAME))
                .path("search")
                .path("uri")
                .queryParam("uri", uri.toString())
                .cookie(authCookie)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get(ClientResponse.class);
        return clientResponse;
    }

}
