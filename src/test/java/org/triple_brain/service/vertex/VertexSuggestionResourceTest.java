/*
 * Copyright Vincent Blouin under the GPL License version 3
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
import java.util.Map;

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
    public void can_add_suggestions_to_vertex() {
        assertThat(
                vertexA().getSuggestions().size(),
                is(0)
        );
        addStartDateSuggestionToVertexA();
        assertThat(
                vertexA().getSuggestions().size(),
                is(greaterThan(0))
        );
    }

    @Test
    public void adding_suggestions_does_not_remove_the_previous_ones() {
        addStartDateSuggestionToVertexA();
        assertThat(
                vertexA().getSuggestions().size(),
                is(1)
        );
        addSuggestionsToVertexA(modelTestScenarios.nameSuggestionFromPersonIdentification(defaultAuthenticatedUser));
        assertThat(
                vertexA().getSuggestions().size(),
                is(2)
        );
    }

    private ClientResponse addStartDateSuggestionToVertexA() {
        return addSuggestionToVertexA(
                modelTestScenarios.startDateSuggestionFromEventIdentification(
                        defaultAuthenticatedUser
                )
        );
    }

    @Test
    public void can_get_suggestions_of_vertex() throws Exception {
        JSONObject suggestions = getSuggestionsOfVertex();
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
        addSuggestionToVertexA(
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
    public void deleting_suggestions_returns_ok_status() {
        SuggestionPojo startDateSuggestion = modelTestScenarios.startDateSuggestionFromEventIdentification(
                defaultAuthenticatedUser
        );

        SuggestionPojo nameSuggestion = modelTestScenarios.nameSuggestionFromPersonIdentification(
                defaultAuthenticatedUser
        );
        addSuggestionsToVertexA(
                startDateSuggestion,
                nameSuggestion
        );
        ClientResponse response = deleteSuggestions(
                startDateSuggestion,
                nameSuggestion
        );
        assertThat(
                response.getStatus(),
                is(Response.Status.OK.getStatusCode())
        );
    }

    @Test
    public void can_delete_one_suggestion() {
        SuggestionPojo startDateSuggestion = modelTestScenarios.startDateSuggestionFromEventIdentification(
                defaultAuthenticatedUser
        );

        SuggestionPojo nameSuggestion = modelTestScenarios.nameSuggestionFromPersonIdentification(
                defaultAuthenticatedUser
        );
        addSuggestionsToVertexA(
                startDateSuggestion,
                nameSuggestion
        );
        assertThat(
                vertexA().getSuggestions().size(),
                is(2)
        );
        deleteSuggestions(
                startDateSuggestion
        );
        assertThat(
                vertexA().getSuggestions().values().iterator().next().uri(),
                is(nameSuggestion.uri())
        );
    }

    @Test
    public void can_delete_multiple_suggestions() {
        SuggestionPojo startDateSuggestion = modelTestScenarios.startDateSuggestionFromEventIdentification(
                defaultAuthenticatedUser
        );

        SuggestionPojo nameSuggestion = modelTestScenarios.nameSuggestionFromPersonIdentification(
                defaultAuthenticatedUser
        );
        addSuggestionsToVertexA(
                startDateSuggestion,
                nameSuggestion
        );
        assertThat(
                vertexA().getSuggestions().size(),
                is(2)
        );
        deleteSuggestions(
                startDateSuggestion,
                nameSuggestion
        );
        assertThat(
                vertexA().getSuggestions().size(),
                is(0)
        );
    }

    private ClientResponse addSuggestionToVertexA(SuggestionPojo suggestionsPojo) {
        return addSuggestionsToVertexA(
                suggestionsPojo
        );
    }

    private ClientResponse addSuggestionsToVertexA(SuggestionPojo... suggestions) {
        return resource
                .path(vertexAUri().getPath())
                .path("suggestions")
                .cookie(authCookie)
                .post(
                        ClientResponse.class,
                        SuggestionJson.multipleToJson(
                                modelTestScenarios.suggestionsToMap(
                                        suggestions
                                )
                        )
                );
    }

    private JSONObject getSuggestionsOfVertex() {
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
        return response.getEntity(JSONObject.class);
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

    private ClientResponse deleteSuggestions(SuggestionPojo... suggestions) {
        JSONArray urisToDelete = new JSONArray();
        for (SuggestionPojo suggestion : suggestions) {
            urisToDelete.put(
                    suggestion.uri()
            );
        }
        return resource
                .path(vertexAUri().getPath())
                .path("suggestions")
                .path("delete")
                .cookie(authCookie)
                .post(
                        ClientResponse.class,
                        urisToDelete
                );
    }
}
