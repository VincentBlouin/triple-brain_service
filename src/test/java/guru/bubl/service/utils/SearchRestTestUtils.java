/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import guru.bubl.module.model.json.UserJson;
import guru.bubl.module.search.EdgeSearchResult;
import guru.bubl.module.search.GraphElementSearchResult;
import guru.bubl.module.search.VertexSearchResult;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

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

    public ClientResponse autoCompletionForPublicVertices(
            String textToSearch
    ) {
        return resource
                .path("service")
                .path("search")
                .queryParam("text", textToSearch)
                .get(ClientResponse.class);
    }

    public ClientResponse getSearchDetailsAnonymously(
            URI uri
    ) {
        return resource
                .path("service")
                .path("search")
                .path("details")
                .queryParam("uri", uri.toString())
                .get(ClientResponse.class);
    }

    public GraphElementSearchResult graphElementSearchResultFromClientResponse(ClientResponse clientResponse) {
        JSONObject jsonObject = clientResponse.getEntity(JSONObject.class);
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

    public ClientResponse autoCompletionResultsForUserVerticesOnly(JSONObject user, String text) {
        return resource
                .path("service")
                .path("users")
                .path(user.optString(UserJson.USER_NAME))
                .path("search")
                .path("own_vertices_and_schemas")
                .path("auto_complete")
                .queryParam("text", text)
                .cookie(authCookie)
                .get(ClientResponse.class);
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

    public GraphElementSearchResult searchDetailsByUri(URI uri) {
        return graphElementSearchResultFromClientResponse(
                searchByUriClientResponse(
                        uri
                )
        );
    }

    public ClientResponse searchByUriClientResponse(URI uri) {
        return resource
                .path("service")
                .path("users")
                .path(authenticatedUserAsJson.optString(UserJson.USER_NAME))
                .path("search")
                .path("details")
                .queryParam("uri", uri.toString())
                .cookie(authCookie)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get(ClientResponse.class);
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
