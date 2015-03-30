/*
 * Copyright Vincent Blouin under the Mozilla Public License 1.1
 */

package org.triple_brain.service;

import com.sun.jersey.api.client.ClientResponse;
import org.junit.Test;
import org.triple_brain.module.search.GraphElementSearchResult;
import org.triple_brain.module.search.VertexSearchResult;
import org.triple_brain.service.utils.GraphManipulationRestTestUtils;

import javax.ws.rs.core.Response;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class PublicSearchResourceTest extends GraphManipulationRestTestUtils {

    @Test
    public void searching_as_anonymous_user_returns_correct_status() {
        assertThat(
                searchUtils().autoCompletionForPublicVertices("vertex").getStatus(),
                is(Response.Status.OK.getStatusCode())
        );
    }

    @Test
    public void can_search_as_anonymous_user() {
        assertTrue(isUserAuthenticated(
                authCookie
        ));
        vertexUtils().makePublicVertexWithUri(
                vertexBUri()
        );
        logoutUsingCookie(authCookie);
        assertFalse(isUserAuthenticated(
                authCookie
        ));
        List<VertexSearchResult> results = searchUtils().vertexSearchResultsFromResponse(
                searchUtils().autoCompletionForPublicVertices("vertex")
        );
        assertThat(
                results.size(),
                is(1)
        );
    }

    @Test
    public void getting_search_details_anonymously_returns_correct_status() {
        vertexUtils().makePublicVertexWithUri(
                vertexBUri()
        );
        logoutUsingCookie(authCookie);
        ClientResponse response = searchUtils().getSearchDetailsAnonymously(
                vertexBUri()
        );
        assertThat(
                response.getStatus(),
                is(Response.Status.OK.getStatusCode())
        );
    }

    @Test
    public void can_get_search_details_anonymously() {
        vertexUtils().updateVertexANote("some comment");
        vertexUtils().makePublicVertexWithUri(
                vertexAUri()
        );
        logoutUsingCookie(authCookie);
        GraphElementSearchResult searchResult = searchUtils().graphElementSearchResultFromClientResponse(
                searchUtils().getSearchDetailsAnonymously(
                        vertexAUri()
                )
        );
        assertThat(
                searchResult.getGraphElement().comment(),
                is("some comment")
        );
    }

    @Test
    public void cannot_get_search_details_anonymously_of_a_private_element() {
        vertexUtils().makePrivateVertexWithUri(
                vertexAUri()
        );
        logoutUsingCookie(authCookie);
        ClientResponse clientResponse = searchUtils().getSearchDetailsAnonymously(
                vertexAUri()
        );
        assertThat(
                clientResponse.getStatus(),
                is(Response.Status.UNAUTHORIZED.getStatusCode())
        );
    }
}
