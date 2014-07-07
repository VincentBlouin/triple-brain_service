package org.triple_brain.service.vertex;

import com.sun.jersey.api.client.ClientResponse;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;
import org.triple_brain.module.model.json.SuggestionJson;
import org.triple_brain.module.model.suggestion.Suggestion;
import org.triple_brain.module.model.suggestion.SuggestionPojo;
import org.triple_brain.service.utils.GraphManipulationRestTest;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

/*
* Copyright Mozilla Public License 1.1
*/
public class VertexSuggestionResourceTest extends GraphManipulationRestTest {

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
        Set<?extends Suggestion> suggestions = vertexA().getSuggestions();
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
                SuggestionPojo.fromSameAsAndDomainUriLabelAndOrigin(
                        "http://rdf.freebase.com/rdf/time/event/start_date",
                        "http://rdf.freebase.com/rdf/type/datetime",
                        "Start date",
                        "http://rdf.freebase.com/rdf/time/event",
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

    private ClientResponse addSuggestionToVertex(SuggestionPojo suggestionsPojo) {
        JSONArray suggestionsArray = new JSONArray();
        suggestionsArray.put(
                SuggestionJson.toJson(
                        suggestionsPojo
                )
        );
        ClientResponse response = resource
                .path(vertexAUri().getPath())
                .path("suggestions")
                .cookie(authCookie)
                .post(
                        ClientResponse.class,
                        suggestionsArray
                );
        return response;
    }

    private JSONArray getSuggestionsOfVertex() throws Exception {
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
}
