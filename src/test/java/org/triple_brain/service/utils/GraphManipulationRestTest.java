package org.triple_brain.service.utils;

import com.google.gson.Gson;
import com.sun.jersey.api.client.ClientResponse;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Before;
import org.triple_brain.module.common_utils.Uris;
import org.triple_brain.module.model.User;
import org.triple_brain.module.model.graph.vertex.VertexInSubGraph;
import org.triple_brain.module.model.json.UserJson;

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
        defaultAuthenticatedUser = User.withUsernameEmailAndLocales(
                userAsJson.getString(UserJson.USER_NAME),
                userAsJson.getString(UserJson.EMAIL),
                "[fr]"
        );
        defaultAuthenticatedUser.password(DEFAULT_PASSWORD);
        deleteAllDocumentsFromSearch();
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

    protected SearchRestTestUtils searchUtils() {
        return SearchRestTestUtils.withWebResourceAndAuthCookie(
                resource,
                authCookie,
                defaultAuthenticatedUserAsJson
        );
    }

    protected JSONObject createAUser() {
        JSONObject newUser = userUtils().validForCreation();
        createUser(
                newUser
        );
        return newUser;
    }

    private void deleteAllDocumentsFromSearch() {
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

    protected void indexGraph() {
        ClientResponse response = resource
                .path("service")
                .path("users")
                .path("test")
                .path("search")
                .path("index_graph")
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

    protected VertexInSubGraph vertexA() {
        return graphUtils().vertexA();
    }

    protected VertexInSubGraph vertexB() {
        return graphUtils().vertexB();
    }

    protected VertexInSubGraph vertexC() {
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
