package org.triple_brain.service.utils;

import com.sun.jersey.api.client.ClientResponse;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Before;
import org.triple_brain.module.common_utils.Uris;
import org.triple_brain.module.model.User;
import org.triple_brain.module.model.json.UserJsonFields;

import java.net.URI;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/*
* Copyright Mozilla Public License 1.1
*/
public class GraphManipulationRestTest extends RestTest {

    public static final Integer DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES = 10;

    protected User defaultAuthenticatedUser;
    protected JSONObject defaultAuthenticatedUserAsJson;

    @Before
    public void before_graph_manipulator_rest_test() throws Exception {
        JSONObject userAsJson = userUtils().validForCreation();
        createUser(userAsJson);
        authenticate(userAsJson);
        defaultAuthenticatedUserAsJson = userAsJson;
        defaultAuthenticatedUser = User.withUsernameAndEmail(
                userAsJson.getString(UserJsonFields.USER_NAME),
                userAsJson.getString(UserJsonFields.EMAIL)
        );
        defaultAuthenticatedUser.password(DEFAULT_PASSWORD);
        deleteAllVerticesFromSearch();
        graphUtils().makeGraphHave3SerialVerticesWithLongLabels();
    }

    protected VertexRestTestUtils vertexUtils() {
        return VertexRestTestUtils.withWebResourceAndAuthCookie(
                resource,
                authCookie,
                defaultAuthenticatedUser
        );
    }

    protected EdgeRestTestUtils edgeUtils() {
        return EdgeRestTestUtils.withWebResourceAndAuthCookie(
                resource,
                authCookie,
                defaultAuthenticatedUser
        );
    }

    protected GraphRestTestUtils graphUtils() {
        return GraphRestTestUtils.withWebResourceAndAuthCookie(
                resource,
                authCookie,
                defaultAuthenticatedUser
        );
    }

    protected UserRestTestUtils userUtils() {
        return UserRestTestUtils.withWebResource(
                resource
        );
    }

    protected JSONObject createAUser() {
        JSONObject newUser = userUtils().validForCreation();
        createUser(
                newUser
        );
        return newUser;
    }

    private void deleteAllVerticesFromSearch() {
        ClientResponse response = resource
                .path("service")
                .path("users")
                .path("test")
                .path("search")
                .path("delete_all_documents")
                .cookie(authCookie)
                .get(ClientResponse.class);
        assertThat(response.getStatus(), is(200));
    }

    protected void indexAllVertices() {
        ClientResponse response = resource
                .path("service")
                .path("users")
                .path("test")
                .path("search")
                .path("index_all_vertices")
                .cookie(authCookie)
                .get(ClientResponse.class);
        assertThat(response.getStatus(), is(200));
    }


    public boolean graphElementWithIdExistsInCurrentGraph(URI graphElementId) {
        ClientResponse response = resource
                .path("service")
                .path("users")
                .path("test")
                .path("graph")
                .path("graph_element")
                .path(Uris.encodeURL(graphElementId.toString()))
                .path("exists")
                .cookie(authCookie)
                .get(ClientResponse.class);
        String boolStr = response.getEntity(String.class);
        return Boolean.valueOf(boolStr);
    }

    protected JSONObject vertexA() {
        return graphUtils().vertexA();
    }

    protected JSONObject vertexB() {
        return graphUtils().vertexB();
    }

    protected JSONObject vertexC() {
        return graphUtils().vertexC();
    }

    protected URI vertexAUri() {
        return graphUtils().vertexAUri();
    }

    protected URI vertexBUri() {
        return graphUtils().vertexBUri();
    }

    protected URI vertexCUri() {
        return graphUtils().vertexCUri();
    }
}
