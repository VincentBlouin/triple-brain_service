package org.triple_brain.service.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.triple_brain.module.model.json.UserJson;
import org.triple_brain.module.search.EdgeSearchResult;
import org.triple_brain.module.search.VertexSearchResult;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/*
* Copyright Mozilla Public License 1.1
*/
public class SearchRestTestUtils {
    private WebResource resource;
    private NewCookie authCookie;
    private JSONObject authenticatedUserAsJson;

    private Gson gson = new Gson();

    public static SearchRestTestUtils withWebResourceAndAuthCookie(WebResource resource, NewCookie authCookie, JSONObject authenticatedUser) {
        return new SearchRestTestUtils(resource, authCookie, authenticatedUser);
    }

    protected SearchRestTestUtils(WebResource resource, NewCookie authCookie, JSONObject authenticatedUserAsJson) {
        this.resource = resource;
        this.authCookie = authCookie;
        this.authenticatedUserAsJson = authenticatedUserAsJson;
    }

    public List<EdgeSearchResult> searchForRelations(String textToSearchWith, JSONObject user) {
        return gson.fromJson(
                searchForRelationsClientResponse(
                        textToSearchWith,
                        user
                ).getEntity(String.class),
                new TypeToken<List<EdgeSearchResult>>() {
                }.getType()
        );
    }

    public ClientResponse searchForRelationsClientResponse(String textToSearchWith, JSONObject user) {
        ClientResponse clientResponse = resource
                .path("service")
                .path("users")
                .path(user.optString(UserJson.USER_NAME))
                .path("search")
                .path("relations")
                .path("auto_complete")
                .queryParam("text", textToSearchWith)
                .cookie(authCookie)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get(ClientResponse.class);
        assertThat(
                clientResponse.getStatus(),
                is(Response.Status.OK.getStatusCode())
        );
        return clientResponse;
    }

    public List<VertexSearchResult> autoCompletionResultsForPublicAndUserVertices(
            String textToSearchWith, JSONObject user
    ) {
        return vertexSearchResultsFromResponse(
                clientResponseOfAutoCompletionForVerticesOfUser(
                        textToSearchWith,
                        user,
                        false
                )
        );
    }


    public List<VertexSearchResult> autoCompletionResultsForUserVerticesOnly(
            String textToSearchWith, JSONObject user
    ) {
        return vertexSearchResultsFromResponse(
                clientResponseOfAutoCompletionOfUserOwnedVertices(
                        textToSearchWith,
                        user
                )
        );
    }

    public List<VertexSearchResult> autoCompletionResultsForCurrentUserVerticesOnly(String textToSearchWith) {
        return vertexSearchResultsFromResponse(
                clientResponseOfAutoCompletionOfCurrentUserVerticesOnly(
                        textToSearchWith
                )
        );
    }

    public List<VertexSearchResult> vertexSearchResultsFromResponse(ClientResponse clientResponse) {
        return gson.fromJson(
                clientResponse.getEntity(JSONArray.class).toString(),
                new TypeToken<List<VertexSearchResult>>() {
                }.getType()
        );
    }

    public ClientResponse clientResponseOfAutoCompletionOfCurrentUserVerticesOnly(String textToSearchWith) {
        return clientResponseOfAutoCompletionForPublicAndUserOwnedVertices(
                textToSearchWith,
                authenticatedUserAsJson
        );
    }

    public ClientResponse clientResponseOfAutoCompletionForPublicAndUserOwnedVertices(String textToSearchWith, JSONObject user) {
        return clientResponseOfAutoCompletionForVerticesOfUser(
                textToSearchWith,
                user,
                false
        );
    }

    public ClientResponse clientResponseOfAutoCompletionOfUserOwnedVertices(String textToSearchWith, JSONObject user) {
        return clientResponseOfAutoCompletionForVerticesOfUser(
                textToSearchWith,
                user,
                true
        );
    }

    private ClientResponse clientResponseOfAutoCompletionForVerticesOfUser(String textToSearchWith, JSONObject user, Boolean onlyOwnVertices) {
        ClientResponse clientResponse = resource
                .path("service")
                .path("users")
                .path(user.optString(UserJson.USER_NAME))
                .path("search")
                .path(onlyOwnVertices ? "own_vertices" : "vertices")
                .path("auto_complete")
                .queryParam("text", textToSearchWith)
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
