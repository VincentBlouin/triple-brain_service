/*
 * Copyright Vincent Blouin under the Mozilla Public License 1.1
 */

package org.triple_brain.service.utils;

import com.google.gson.Gson;
import com.sun.jersey.api.client.ClientResponse;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Before;
import org.triple_brain.module.common_utils.Uris;
import org.triple_brain.module.model.User;
import org.triple_brain.module.model.UserUris;
import org.triple_brain.module.model.graph.ModelTestScenarios;
import org.triple_brain.module.model.graph.vertex.VertexInSubGraph;
import org.triple_brain.module.model.json.UserJson;

import java.net.URI;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class GraphManipulationRestTest extends RestTest {

    protected static ModelTestScenarios modelTestScenarios = new ModelTestScenarios();

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

    protected GraphElementRestTestUtils graphElementUtils() {
        return GraphElementRestTestUtils.withWebResourceAndAuthCookie(
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

    protected SchemaUtils schemaUtils() {
        return SchemaUtils.withWebResourceAndAuthCookie(
                resource,
                authCookie,
                graphUtils()
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

    protected String getUsersBaseUri(String username) {
        return new UserUris(username).baseUri().toString();
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
