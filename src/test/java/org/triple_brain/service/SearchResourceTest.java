package org.triple_brain.service;

import com.sun.jersey.api.client.ClientResponse;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;
import org.triple_brain.module.common_utils.JsonUtils;
import org.triple_brain.module.model.json.FriendlyResourceJson;
import org.triple_brain.module.model.json.UserJsonFields;
import org.triple_brain.module.solr_search.json.SearchJsonConverter;
import org.triple_brain.service.utils.GraphManipulationRestTest;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.triple_brain.module.model.json.graph.VertexJson.LABEL;

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
        JSONArray searchResults = response.getEntity(JSONArray.class);
        assertThat(searchResults.length(), is(3));
        assertTrue(searchResults.getJSONObject(0).has(LABEL));
    }

    @Test
    public void search_for_auto_complete_can_have_spaces() throws Exception {
        indexGraph();
        ClientResponse response = searchUtils().searchOwnVerticesOnlyForAutoCompleteUsingRest(
                "vertex Azu"
        );
        JSONArray searchResults = response.getEntity(JSONArray.class);
        JSONObject firstResult = searchResults.getJSONObject(0);
        assertThat(firstResult.getString(LABEL), is("vertex Azure"));

        response = searchUtils().searchOwnVerticesOnlyForAutoCompleteUsingRest(
                "vertex Bar"
        );
        searchResults = response.getEntity(JSONArray.class);
        firstResult = searchResults.getJSONObject(0);
        assertThat(firstResult.getString(LABEL), is("vertex Bareau"));
    }

    @Test
    public void can_search_for_only_own_vertices() throws Exception {
        vertexUtils().makePublicVertexWithUri(
                vertexAUri()
        );
        indexGraph();
        JSONObject anotherUser = createAUser();
        authenticate(anotherUser);
        JSONArray results = searchUtils().searchOwnVerticesAndPublicOnesForAutoCompleteUsingRestAndUser(
                vertexA().getString(LABEL),
                anotherUser
        ).getEntity(JSONArray.class);
        assertThat(results.length(), is(greaterThan(0)));
        results = searchUtils().searchOnlyOwnVerticesForAutoCompleteUsingRestAndUser(
                vertexA().getString(LABEL),
                anotherUser
        ).getEntity(JSONArray.class);
        assertThat(results.length(), is(0));
    }

    @Test
    public void can_search_relations() {
        indexGraph();
        JSONArray relations = searchUtils().searchForRelations(
                "between",
                defaultAuthenticatedUserAsJson
        ).getEntity(JSONArray.class);
        assertThat(relations.length(), is(2));
    }



    @Test
    public void removing_vertex_removes_relations_name_of_edge_of_connected_vertices()throws Exception{
        indexGraph();
        JSONArray resultsForA = searchUtils().searchOwnVerticesOnlyForAutoCompleteUsingRest(
                vertexA().getString(LABEL)
        ).getEntity(JSONArray.class).getJSONObject(0).getJSONArray(
                SearchJsonConverter.RELATIONS_NAME
        );
        assertTrue(JsonUtils.containsString(
                resultsForA,
                "between vertex A and vertex B"
        ));
        vertexUtils().removeVertexB();
        resultsForA = searchUtils().searchOwnVerticesOnlyForAutoCompleteUsingRest(
                vertexA().getString(LABEL)
        ).getEntity(JSONArray.class).getJSONObject(0).getJSONArray(
                SearchJsonConverter.RELATIONS_NAME
        );
        assertFalse(JsonUtils.containsString(
                resultsForA,
                "between vertex A and vertex B"
        ));
    }

    @Test
    public void getting_search_result_by_uri_returns_correct_status() {
        indexGraph();
        ClientResponse clientResponse = getByUri(vertexAUri());
        assertThat(
                clientResponse.getStatus(),
                is(Response.Status.OK.getStatusCode())
        );
    }

    @Test
    public void can_get_search_result_by_uri() {
        indexGraph();
        JSONObject result = getByUri(
                vertexAUri()
        ).getEntity(JSONObject.class);
        assertThat(
                result.optString(FriendlyResourceJson.LABEL),
                is("vertex Azure")
        );
    }


    private ClientResponse getByUri(URI uri){
        ClientResponse clientResponse = resource
                .path("service")
                .path("users")
                .path(defaultAuthenticatedUserAsJson.optString(UserJsonFields.USER_NAME))
                .path("search")
                .path("uri")
                .queryParam("uri", uri.toString())
                .cookie(authCookie)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get(ClientResponse.class);
        return clientResponse;
    }

}
