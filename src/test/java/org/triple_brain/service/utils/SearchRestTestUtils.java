package org.triple_brain.service.utils;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.codehaus.jettison.json.JSONObject;
import org.triple_brain.module.model.json.UserJsonFields;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/*
* Copyright Mozilla Public License 1.1
*/
public class SearchRestTestUtils {
    private WebResource resource;
    private NewCookie authCookie;
    private JSONObject authenticatedUserAsJson;


    public static SearchRestTestUtils withWebResourceAndAuthCookie(WebResource resource, NewCookie authCookie, JSONObject authenticatedUser){
        return new SearchRestTestUtils(resource, authCookie, authenticatedUser);
    }
    protected SearchRestTestUtils(WebResource resource, NewCookie authCookie, JSONObject authenticatedUserAsJson){
        this.resource = resource;
        this.authCookie = authCookie;
        this.authenticatedUserAsJson = authenticatedUserAsJson;
    }

    public ClientResponse searchForRelations(String textToSearchWith, JSONObject user) {
        ClientResponse clientResponse = resource
                .path("service")
                .path("users")
                .path(user.optString(UserJsonFields.USER_NAME))
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

    public ClientResponse searchOwnVerticesAndPublicOnesForAutoCompleteUsingRestAndUser(String textToSearchWith, JSONObject user) {
        return searchVerticesForAutoCompleteUsingRestAndUser(
                textToSearchWith,
                user,
                false
        );
    }

    public ClientResponse searchOnlyOwnVerticesForAutoCompleteUsingRestAndUser(String textToSearchWith, JSONObject user) {
        return searchVerticesForAutoCompleteUsingRestAndUser(
                textToSearchWith,
                user,
                true
        );
    }

    public ClientResponse searchOwnVerticesOnlyForAutoCompleteUsingRest(String textToSearchWith) {
        return searchOwnVerticesAndPublicOnesForAutoCompleteUsingRestAndUser(
                textToSearchWith,
                authenticatedUserAsJson
        );
    }

    private ClientResponse searchVerticesForAutoCompleteUsingRestAndUser(String textToSearchWith, JSONObject user, Boolean onlyOwnVertices) {
        ClientResponse clientResponse = resource
                .path("service")
                .path("users")
                .path(user.optString(UserJsonFields.USER_NAME))
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
