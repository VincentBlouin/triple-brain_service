/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.center;

import com.sun.jersey.api.client.ClientResponse;
import guru.bubl.module.model.center_graph_element.CenterGraphElementPojo;
import guru.bubl.module.model.graph.ShareLevel;
import guru.bubl.service.SessionHandler;
import guru.bubl.service.utils.GraphManipulationRestTestUtils;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static guru.bubl.service.utils.GraphRestTestUtils.getCenterGraphElementsFromClientResponse;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

public class CenterGraphElementsResourceTest extends GraphManipulationRestTestUtils {

    @Test
    public void getting_center_graph_elements_returns_ok_status() {
        assertThat(
                graphUtils().getCenterGraphElementsResponse().getStatus(),
                is(Response.Status.OK.getStatusCode())
        );
    }

    @Test
    public void cannot_get_private_center_elements_of_another_user() {
        assertThat(
                graphUtils().getCenterGraphElementsResponseForGraphElementTypeAndUser(defaultAuthenticatedUser).getStatus(),
                is(
                        Response.Status.OK.getStatusCode()
                )
        );
        createAUser();
        assertThat(
                graphUtils().getCenterGraphElementsResponseForGraphElementTypeAndUser(defaultAuthenticatedUser).getStatus(),
                is(Response.Status.FORBIDDEN.getStatusCode())
        );
    }

    @Test
    public void returns_center_elements() {
        graphUtils().graphWithCenterVertexUri(vertexA().uri());

        assertFalse(
                graphUtils().getCenterGraphElements().isEmpty()
        );
    }

    @Test
    public void can_for_a_specific_friend() {
        vertexUtils().setShareLevel(
                vertexBUri(),
                ShareLevel.PUBLIC
        );
        graphUtils().graphWithCenterVertexUri(vertexBUri());
        JSONObject anotherUser = createAUser();
        String otherUsername = anotherUser.optString("user_name");
        authenticate(anotherUser);
        userUtils().addFriend(
                otherUsername,
                defaultAuthenticatedUser.username()
        );
        authenticate(defaultAuthenticatedUser);
        userUtils().addFriend(
                defaultAuthenticatedUser.username(),
                otherUsername
        );
        authenticate(anotherUser);
        ClientResponse response = getForASpecificFriendResponse(
                otherUsername,
                defaultAuthenticatedUser.username()
        );
        assertThat(
                response.getStatus(),
                is(Response.Status.OK.getStatusCode())
        );
        CenterGraphElementPojo centerGraphElement = getCenterGraphElementsFromClientResponse(
                response
        ).iterator().next();
        assertThat(
                centerGraphElement.getGraphElement().label(),
                is("vertex Bareau")
        );
    }

    @Test
    public void getting_from_specific_friend_throws_error_when_not_friends() {
        vertexUtils().setShareLevel(
                vertexBUri(),
                ShareLevel.PUBLIC
        );
        graphUtils().graphWithCenterVertexUri(vertexBUri());
        JSONObject anotherUser = createAUser();
        String otherUsername = anotherUser.optString("user_name");
        authenticate(anotherUser);
        userUtils().addFriend(
                otherUsername,
                defaultAuthenticatedUser.username()
        );
        ClientResponse response = getForASpecificFriendResponse(
                otherUsername,
                defaultAuthenticatedUser.username()
        );
        assertThat(
                response.getStatus(),
                is(Response.Status.FORBIDDEN.getStatusCode())
        );
    }

    private ClientResponse getForASpecificFriendResponse(String authenticatedUsername, String friendUsername) {
        return resource
                .path("service")
                .path("users")
                .path(authenticatedUsername)
                .path("center-elements")
                .path("friend")
                .path(friendUsername)
                .cookie(authCookie)
                .header(SessionHandler.X_XSRF_TOKEN, currentXsrfToken)
                .get(ClientResponse.class);
    }

}
