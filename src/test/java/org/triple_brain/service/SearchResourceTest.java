package org.triple_brain.service;

import com.sun.jersey.api.client.ClientResponse;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;
import org.triple_brain.module.common_utils.JsonUtils;
import org.triple_brain.module.search.json.SearchJsonConverter;
import org.triple_brain.service.utils.GraphManipulationRestTest;
import org.triple_brain.module.common_utils.Uris;

import javax.ws.rs.core.MediaType;

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
                .path(authenticatedUser.username())
                .path("search")
                .path("vertices")
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
        ClientResponse response = searchForAutoCompleteUsingRest(
                "vertex Azu"
        );
        JSONArray searchResults = response.getEntity(JSONArray.class);
        JSONObject firstResult = searchResults.getJSONObject(0);
        assertThat(firstResult.getString(LABEL), is("vertex Azure"));

        response = searchForAutoCompleteUsingRest(
                "vertex Bar"
        );
        searchResults = response.getEntity(JSONArray.class);
        firstResult = searchResults.getJSONObject(0);
        assertThat(firstResult.getString(LABEL), is("vertex Bareau"));
    }

    @Test
    public void updating_note_updates_search()throws Exception{
        indexAllVertices();
        JSONObject resultsForA = searchForAutoCompleteUsingRest(
                vertexA().getString(LABEL)
        ).getEntity(JSONArray.class).getJSONObject(0);
        assertThat(resultsForA.getString(NOTE), is(""));
        vertexUtils.updateVertexANote(
                "A description"
        );
        resultsForA = searchForAutoCompleteUsingRest(
                vertexA().getString(LABEL)
        ).getEntity(JSONArray.class).getJSONObject(0);
        assertThat(resultsForA.getString(NOTE), is("A description"));
    }

    @Test
    public void updating_edge_labels_reflects_in_search_for_connected_vertices()throws Exception{
        indexAllVertices();
        JSONArray resultsForA = searchForAutoCompleteUsingRest(
                vertexA().getString(LABEL)
        ).getEntity(JSONArray.class).getJSONObject(0).getJSONArray(
                SearchJsonConverter.RELATIONS_NAME
        );
        assertTrue(JsonUtils.containsString(
                resultsForA,
                "between vertex A and vertex B"
        ));
        JSONArray resultsForB = searchForAutoCompleteUsingRest(
                vertexB().getString(LABEL)
        ).getEntity(JSONArray.class).getJSONObject(0).getJSONArray(
                SearchJsonConverter.RELATIONS_NAME
        );
        assertTrue(JsonUtils.containsString(
                resultsForB,
                "between vertex A and vertex B"
        ));
        edgeUtils.updateEdgeLabel(
                "new edge text !",
                edgeUtils.edgeBetweenAAndB()
        );
        resultsForA = searchForAutoCompleteUsingRest(
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
        resultsForB = searchForAutoCompleteUsingRest(
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
        JSONArray resultsForA = searchForAutoCompleteUsingRest(
                vertexA().getString(LABEL)
        ).getEntity(JSONArray.class).getJSONObject(0).getJSONArray(
                SearchJsonConverter.RELATIONS_NAME
        );
        assertTrue(JsonUtils.containsString(
                resultsForA,
                "between vertex A and vertex B"
        ));
        edgeUtils.removeEdgeBetweenVertexAAndB();
        resultsForA = searchForAutoCompleteUsingRest(
                vertexA().getString(LABEL)
        ).getEntity(JSONArray.class).getJSONObject(0).getJSONArray(
                SearchJsonConverter.RELATIONS_NAME
        );
        assertFalse(JsonUtils.containsString(
                resultsForA,
                "between vertex A and vertex B"
        ));

    }

    private ClientResponse searchForAutoCompleteUsingRest(String textToSearchWith)throws Exception{
        return resource
                .path("service")
                .path("users")
                .path(authenticatedUser.username())
                .path("search")
                .path("vertices")
                .path("auto_complete")
                .path(Uris.encodeURL(textToSearchWith))
                .cookie(authCookie)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get(ClientResponse.class);
    }
}
