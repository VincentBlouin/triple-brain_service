/*
 * Copyright Vincent Blouin under the Mozilla Public License 1.1
 */

package org.triple_brain.service.vertex;

import com.sun.jersey.api.client.ClientResponse;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;
import org.triple_brain.module.model.json.SuggestionJson;
import org.triple_brain.module.model.suggestion.Suggestion;
import org.triple_brain.module.model.suggestion.SuggestionPojo;
import org.triple_brain.module.search.EdgeSearchResult;
import org.triple_brain.service.utils.GraphManipulationRestTestUtils;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;

public class VertexSuggestionResourceTest extends GraphManipulationRestTestUtils {

    @Test
    public void status_code_is_ok_for_adding_suggestions() {
        ClientResponse response = addStartDateSuggestionToVertexA();
        assertThat(
                response.getStatus(),
                is(Response.Status.NO_CONTENT.getStatusCode())
        );
    }

    @Test
    public void can_add_suggestions_to_vertex() throws Exception {
        Set<? extends Suggestion> suggestions = vertexA().getSuggestions();
        assertThat(
                suggestions.size(),
                is(0)
        );
        addStartDateSuggestionToVertexA();
        suggestions = vertexA().getSuggestions();
        assertThat(
                suggestions.size(),
                is(greaterThan(0))
        );
    }

    private ClientResponse addStartDateSuggestionToVertexA() {
        return addSuggestionToVertex(
                modelTestScenarios.startDateSuggestionFromEventIdentification(
                        defaultAuthenticatedUser
                )
        );
    }

    @Test
    public void can_get_suggestions_of_vertex() throws Exception {
        JSONArray suggestions = getSuggestionsOfVertex();
        assertThat(
                suggestions.length(),
                is(0)
        );
        addStartDateSuggestionToVertexA();
        suggestions = getSuggestionsOfVertex();
        assertThat(
                suggestions.length(),
                is(greaterThan(0))
        );
    }

    @Test
    public void accepting_suggestions_returns_ok_status() {
        SuggestionPojo startDateSuggestion = modelTestScenarios.startDateSuggestionFromEventIdentification(
                defaultAuthenticatedUser
        );
        addSuggestionToVertex(
                startDateSuggestion
        );
        ClientResponse response = acceptSuggestion(
                startDateSuggestion,
                vertexAUri()
        );
        assertThat(
                response.getStatus(),
                is(
                        Response.Status.OK.getStatusCode()
                )
        );
    }

    @Test
    public void can_search_new_edge_created_from_accepting_suggestion() {
        SuggestionPojo startDateSuggestion = modelTestScenarios.startDateSuggestionFromEventIdentification(
                defaultAuthenticatedUser
        );
        List<EdgeSearchResult> relations = searchUtils().searchForRelations(
                startDateSuggestion.label(),
                defaultAuthenticatedUserAsJson
        );
        assertThat(relations.size(), is(0));
        acceptSuggestion(
                startDateSuggestion,
                vertexAUri()
        );
        relations = searchUtils().searchForRelations(
                startDateSuggestion.label(),
                defaultAuthenticatedUserAsJson
        );
        assertThat(relations.size(), is(1));
    }

    @Test
    public void accepting_suggestion_returns_edge_and_vertex_new_uri() {
        JSONObject newEdgeAndVertexUri = acceptSuggestion(
                modelTestScenarios.startDateSuggestionFromEventIdentification(
                        defaultAuthenticatedUser
                ),
                vertexAUri()
        ).getEntity(JSONObject.class);
        assertTrue(newEdgeAndVertexUri.has("edge_uri"));
        assertTrue(newEdgeAndVertexUri.has("vertex_uri"));
    }

    private ClientResponse addSuggestionToVertex(SuggestionPojo suggestionsPojo) {
        JSONArray suggestionsArray = new JSONArray();
        suggestionsArray.put(
                SuggestionJson.toJson(
                        suggestionsPojo
                )
        );
        return resource
                .path(vertexAUri().getPath())
                .path("suggestions")
                .cookie(authCookie)
                .post(
                        ClientResponse.class,
                        suggestionsArray
                );
    }

    private JSONArray getSuggestionsOfVertex() {
        ClientResponse response = resource
                .path(vertexAUri().getPath())
                .path("suggestions")
                .cookie(authCookie)
                .type(MediaType.APPLICATION_JSON)
                .get(ClientResponse.class);
        assertThat(
                response.getStatus(),
                is(Response.Status.OK.getStatusCode())
        );
        return response.getEntity(JSONArray.class);
    }

    private ClientResponse acceptSuggestion(SuggestionPojo suggestion, URI vertexUri) {
        return resource
                .path(vertexUri.getPath())
                .path("suggestions")
                .path("accept")
                .cookie(authCookie)
                .post(
                        ClientResponse.class,
                        SuggestionJson.toJson(suggestion)
                );
    }
}
