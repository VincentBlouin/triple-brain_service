package org.triple_brain.service.vertex;

import com.sun.jersey.api.client.ClientResponse;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;
import org.triple_brain.module.model.ModelTestScenarios;
import org.triple_brain.module.model.json.SuggestionJsonFields;
import org.triple_brain.module.model.json.graph.VertexJsonFields;
import org.triple_brain.module.model.suggestion.Suggestion;
import org.triple_brain.service.utils.GraphManipulationRestTest;

import javax.ws.rs.core.MediaType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

/*
* Copyright Mozilla Public License 1.1
*/
public class VertexSuggestionResourceTest extends GraphManipulationRestTest {

    @Test
    public void can_add_suggestions_to_vertex() throws Exception {
        JSONArray suggestions = vertexA().getJSONArray(VertexJsonFields.SUGGESTIONS);
        assertThat(
                suggestions.length(),
                is(0)
        );

        addSuggestionToVertex(
                ModelTestScenarios.startDateSuggestion()
        );
        suggestions = vertexA().getJSONArray(VertexJsonFields.SUGGESTIONS);
        assertThat(
                suggestions.length(),
                is(greaterThan(0))
        );
    }

    private ClientResponse addSuggestionToVertex(Suggestion suggestion) throws Exception {
        JSONObject suggestionAsJson = SuggestionJsonFields.toJson(suggestion);
        suggestionAsJson.put(
                SuggestionJsonFields.ORIGIN,
                suggestion.origins().iterator().next().toString()
        );
        return addSuggestionsToVertex(
                new JSONArray().put(
                        suggestionAsJson
                )
        );
    }

    private ClientResponse addSuggestionsToVertex(JSONArray suggestions) throws Exception {
        ClientResponse response = resource
                .path(vertexAUri().getPath())
                .path("suggestions")
                .cookie(authCookie)
                .type(MediaType.APPLICATION_JSON)
                .post(ClientResponse.class, suggestions);
        return response;
    }

    @Test
    public void can_get_suggestions_of_vertex() throws Exception {
        JSONArray suggestions = getSuggestionsOfVertex();
        assertThat(
                suggestions.length(),
                is(0)
        );
        addSuggestionToVertex(
                ModelTestScenarios.startDateSuggestion()
        );
        suggestions = getSuggestionsOfVertex();
        assertThat(
                suggestions.length(),
                is(greaterThan(0))
        );
    }

    private JSONArray getSuggestionsOfVertex() throws Exception {
        ClientResponse response = resource
                .path(vertexAUri().getPath())
                .path("suggestions")
                .cookie(authCookie)
                .type(MediaType.APPLICATION_JSON)
                .get(ClientResponse.class);
        return response.getEntity(JSONArray.class);
    }
}
