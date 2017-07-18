/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources;

import com.sun.jersey.api.client.ClientResponse;
import guru.bubl.module.model.graph.GraphElement;
import guru.bubl.module.model.search.GraphElementSearchResult;
import guru.bubl.service.utils.GraphManipulationRestTestUtils;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SearchResourceTest extends GraphManipulationRestTestUtils {

    @Test
    public void can_search_vertices_for_auto_complete() throws Exception {
        searchUtils().indexAll();
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
        List<GraphElementSearchResult> results = searchUtils().vertexSearchResultsFromResponse(
                response
        );
        assertThat(results.size(), is(3));
    }


    @Test
    public void search_for_auto_complete_can_have_spaces() throws Exception {
        searchUtils().indexAll();
        List<GraphElementSearchResult> searchResults = searchUtils().autoCompletionResultsForCurrentUserVerticesOnly(
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
        searchUtils().indexAll();
        JSONObject anotherUser = createAUser();
        authenticate(anotherUser);
        List<GraphElementSearchResult> results = searchUtils().autoCompletionResultsForPublicAndUserVertices(
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
    public void searching_for_only_owned_schemas_or_vertices_returns_correct_status() throws Exception {
        assertThat(
                searchUtils().autoCompletionResultsForUserVerticesOnly(
                        defaultAuthenticatedUserAsJson,
                        "test"
                ).getStatus(),
                is(Response.Status.OK.getStatusCode())
        );
    }

    @Test
    public void can_search_relations() {
        searchUtils().indexAll();
        List<GraphElementSearchResult> relations = searchUtils().searchForRelations(
                "between",
                defaultAuthenticatedUserAsJson
        );
        assertThat(relations.size(), is(2));
    }

    @Test
    public void can_get_search_details_by_uri() {
        searchUtils().indexAll();
        GraphElement result = searchUtils().searchDetailsByUri(
                vertexAUri()
        ).getGraphElement();
        assertThat(
                result.label(),
                is("vertex Azure")
        );
    }

    @Test
    public void can_index_and_search_bubbles_having_special_characters() throws Exception {
        vertexUtils().updateVertexLabelUsingRest(
                vertexAUri(),
                "z(arg"
        );
        searchUtils().indexAll();
        List<GraphElementSearchResult> results = searchUtils().autoCompletionResultsForCurrentUserVerticesOnly(
                "z(arg"
        );
        assertThat(results.size(), is(1));
    }
}
