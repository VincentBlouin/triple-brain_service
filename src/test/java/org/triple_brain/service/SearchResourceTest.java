package org.triple_brain.service;

import com.sun.jersey.api.client.ClientResponse;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;
import org.triple_brain.module.common_utils.JsonUtils;
import org.triple_brain.module.model.User;
import org.triple_brain.module.model.json.UserJsonFields;
import org.triple_brain.module.search.json.SearchJsonConverter;
import org.triple_brain.service.utils.GraphManipulationRestTest;
import org.triple_brain.module.common_utils.Uris;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.triple_brain.module.model.json.graph.VertexJsonFields.LABEL;
import static org.triple_brain.module.model.json.graph.VertexJsonFields.NOTE;

/*
* Copyright Mozilla Public License 1.1
*/
public class SearchResourceTest extends GraphManipulationRestTest {

    @Test
    public void can_search_vertices_for_auto_complete()throws Exception{
        indexAllVertices();
        ClientResponse response = resource
                .path("service")
                .path("users")
                .path(defaultAuthenticatedUser.username())
                .path("search")
                .path("own_vertices")
                .path("auto_complete")
                .path(Uris.encodeURL("vert"))
                .cookie(authCookie)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .type(MediaType.TEXT_PLAIN)
                .get(ClientResponse.class);
        JSONArray searchResults = response.getEntity(JSONArray.class);
        assertThat(searchResults.length(), is(3));
        assertTrue(searchResults.getJSONObject(0).has(LABEL));
    }

    @Test
    public void search_for_auto_complete_can_have_spaces()throws Exception{
        indexAllVertices();
        ClientResponse response = searchOwnVerticesOnlyForAutoCompleteUsingRest(
                "vertex Azu"
        );
        JSONArray searchResults = response.getEntity(JSONArray.class);
        JSONObject firstResult = searchResults.getJSONObject(0);
        assertThat(firstResult.getString(LABEL), is("vertex Azure"));

        response = searchOwnVerticesOnlyForAutoCompleteUsingRest(
                "vertex Bar"
        );
        searchResults = response.getEntity(JSONArray.class);
        firstResult = searchResults.getJSONObject(0);
        assertThat(firstResult.getString(LABEL), is("vertex Bareau"));
    }

    @Test
    public void updating_note_updates_search()throws Exception{
        indexAllVertices();
        JSONObject resultsForA = searchOwnVerticesOnlyForAutoCompleteUsingRest(
                vertexA().getString(LABEL)
        ).getEntity(JSONArray.class).getJSONObject(0);
        assertThat(resultsForA.getString(NOTE), is(""));
        vertexUtils().updateVertexANote(
                "A description"
        );
        resultsForA = searchOwnVerticesOnlyForAutoCompleteUsingRest(
                vertexA().getString(LABEL)
        ).getEntity(JSONArray.class).getJSONObject(0);
        assertThat(resultsForA.getString(NOTE), is("A description"));
    }

    @Test
    public void updating_edge_labels_reflects_in_search_for_connected_vertices()throws Exception{
        indexAllVertices();
        JSONArray resultsForA = searchOwnVerticesOnlyForAutoCompleteUsingRest(
                vertexA().getString(LABEL)
        ).getEntity(JSONArray.class).getJSONObject(0).getJSONArray(
                SearchJsonConverter.RELATIONS_NAME
        );
        assertTrue(JsonUtils.containsString(
                resultsForA,
                "between vertex A and vertex B"
        ));
        JSONArray resultsForB = searchOwnVerticesOnlyForAutoCompleteUsingRest(
                vertexB().getString(LABEL)
        ).getEntity(JSONArray.class).getJSONObject(0).getJSONArray(
                SearchJsonConverter.RELATIONS_NAME
        );
        assertTrue(JsonUtils.containsString(
                resultsForB,
                "between vertex A and vertex B"
        ));
        edgeUtils().updateEdgeLabel(
                "new edge text !",
                edgeUtils().edgeBetweenAAndB()
        );
        resultsForA = searchOwnVerticesOnlyForAutoCompleteUsingRest(
                vertexA().getString(LABEL)
        ).getEntity(JSONArray.class).getJSONObject(0).getJSONArray(
                SearchJsonConverter.RELATIONS_NAME
        );
        assertFalse(JsonUtils.containsString(
                resultsForA,
                "between vertex A and vertex B"
        ));
        assertTrue(JsonUtils.containsString(
                resultsForA,
                "new edge text !"
        ));
        resultsForB = searchOwnVerticesOnlyForAutoCompleteUsingRest(
                vertexB().getString(LABEL)
        ).getEntity(JSONArray.class).getJSONObject(0).getJSONArray(
                SearchJsonConverter.RELATIONS_NAME
        );
        assertFalse(JsonUtils.containsString(
                resultsForB,
                "between vertex A and vertex B"
        ));
        assertTrue(JsonUtils.containsString(
                resultsForB,
                "new edge text !"
        ));
    }

    @Test
    public void deleting_edge_removes_relations_name_of_connected_vertices_in_search()throws Exception{
        indexAllVertices();
        JSONArray resultsForA = searchOwnVerticesOnlyForAutoCompleteUsingRest(
                vertexA().getString(LABEL)
        ).getEntity(JSONArray.class).getJSONObject(0).getJSONArray(
                SearchJsonConverter.RELATIONS_NAME
        );
        assertTrue(JsonUtils.containsString(
                resultsForA,
                "between vertex A and vertex B"
        ));
        edgeUtils().removeEdgeBetweenVertexAAndB();
        resultsForA = searchOwnVerticesOnlyForAutoCompleteUsingRest(
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
    public void making_vertex_public_re_indexes_it()throws Exception{
        indexAllVertices();
        JSONObject anotherUserAsJson = userUtils().validForCreation();
        createAUser(anotherUserAsJson);
        User anotherUser = User.withUsernameAndEmail(
                anotherUserAsJson.getString(UserJsonFields.USER_NAME),
                anotherUserAsJson.getString(UserJsonFields.EMAIL)
        );
        anotherUser.password(DEFAULT_PASSWORD);
        authenticate(anotherUser);
        JSONArray results = searchOwnVerticesAndPublicOnesForAutoCompleteUsingRestAndUser(
                vertexA().getString(LABEL),
                anotherUser
        ).getEntity(JSONArray.class);
        assertThat(results.length(), is(0));
        authenticate(defaultAuthenticatedUser);
        vertexUtils().makePublicVertexWithUri(
                vertexAUri()
        );
        authenticate(anotherUser);
        results = searchOwnVerticesAndPublicOnesForAutoCompleteUsingRestAndUser(
                vertexA().getString(LABEL),
                anotherUser
        ).getEntity(JSONArray.class);
        assertThat(results.length(), is(1));
    }

    @Test
    public void making_vertex_private_re_indexes_it()throws Exception{
        vertexUtils().makePublicVertexWithUri(
                vertexAUri()
        );
        indexAllVertices();
        JSONObject anotherUserAsJson = userUtils().validForCreation();
        createAUser(anotherUserAsJson);
        User anotherUser = User.withUsernameAndEmail(
                anotherUserAsJson.getString(UserJsonFields.USER_NAME),
                anotherUserAsJson.getString(UserJsonFields.EMAIL)
        );
        anotherUser.password(DEFAULT_PASSWORD);
        authenticate(anotherUser);
        JSONArray results = searchOwnVerticesAndPublicOnesForAutoCompleteUsingRestAndUser(
                vertexA().getString(LABEL),
                anotherUser
        ).getEntity(JSONArray.class);
        assertThat(results.length(), is(greaterThan(0)));
        authenticate(defaultAuthenticatedUser);
        vertexUtils().makePrivateVertexWithUri(
                vertexAUri()
        );
        authenticate(anotherUser);
        results = searchOwnVerticesAndPublicOnesForAutoCompleteUsingRestAndUser(
                vertexA().getString(LABEL),
                anotherUser
        ).getEntity(JSONArray.class);
        assertThat(results.length(), is(0));
    }

    @Test
    public void can_search_for_only_own_vertices()throws Exception{
        vertexUtils().makePublicVertexWithUri(
                vertexAUri()
        );
        indexAllVertices();
        JSONObject anotherUserAsJson = userUtils().validForCreation();
        createAUser(anotherUserAsJson);
        User anotherUser = User.withUsernameAndEmail(
                anotherUserAsJson.getString(UserJsonFields.USER_NAME),
                anotherUserAsJson.getString(UserJsonFields.EMAIL)
        );
        anotherUser.password(DEFAULT_PASSWORD);
        authenticate(anotherUser);
        JSONArray results = searchOwnVerticesAndPublicOnesForAutoCompleteUsingRestAndUser(
                vertexA().getString(LABEL),
                anotherUser
        ).getEntity(JSONArray.class);
        assertThat(results.length(), is(greaterThan(0)));
        results = searchOnlyOwnVerticesForAutoCompleteUsingRestAndUser(
                vertexA().getString(LABEL),
                anotherUser
        ).getEntity(JSONArray.class);
        assertThat(results.length(), is(0));
    }

    private ClientResponse searchOwnVerticesAndPublicOnesForAutoCompleteUsingRestAndUser(String textToSearchWith, User user){
        return searchVerticesForAutoCompleteUsingRestAndUser(
                textToSearchWith,
                user,
                false
        );
    }

    private ClientResponse searchOnlyOwnVerticesForAutoCompleteUsingRestAndUser(String textToSearchWith, User user){
        return searchVerticesForAutoCompleteUsingRestAndUser(
                textToSearchWith,
                user,
                true
        );
    }

    private ClientResponse searchOwnVerticesOnlyForAutoCompleteUsingRest(String textToSearchWith){
        return searchOwnVerticesAndPublicOnesForAutoCompleteUsingRestAndUser(
                textToSearchWith,
                defaultAuthenticatedUser
        );
    }

    private ClientResponse searchVerticesForAutoCompleteUsingRestAndUser(String textToSearchWith, User user, Boolean onlyOwnVertices){
        ClientResponse clientResponse = resource
                .path("service")
                .path("users")
                .path(user.username())
                .path("search")
                .path(onlyOwnVertices ? "own_vertices" : "vertices")
                .path("auto_complete")
                .path(Uris.encodeURL(textToSearchWith))
                .cookie(authCookie)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get(ClientResponse.class);
        assertThat(
                clientResponse.getStatus(),
                is(Response.Status.OK.getStatusCode())
        );

        return clientResponse;
    }
}
