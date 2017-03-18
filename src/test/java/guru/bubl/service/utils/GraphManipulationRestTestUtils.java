/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.utils;

import com.sun.jersey.api.client.ClientResponse;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Before;
import guru.bubl.module.common_utils.Uris;
import guru.bubl.module.model.User;
import guru.bubl.module.model.UserUris;
import guru.bubl.test.module.utils.ModelTestScenarios;
import guru.bubl.module.model.graph.vertex.VertexInSubGraph;
import guru.bubl.module.model.json.UserJson;

import java.net.URI;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class GraphManipulationRestTestUtils extends RestTestUtils {

    protected static ModelTestScenarios modelTestScenarios = new ModelTestScenarios();

    protected User defaultAuthenticatedUser;
    protected JSONObject defaultAuthenticatedUserAsJson;

    @Before
    public void before_graph_manipulator_rest_test() throws Exception {
        userUtils().deleteAllUsers();
        JSONObject userAsJson = userUtils().validForCreation();
        String username = userAsJson.getString(
                UserJson.USER_NAME
        );
        String accent = "é";
        userAsJson.put(
                UserJson.USER_NAME,
                username + accent
        );
        ClientResponse response = createUser(userAsJson);
        userAsJson = response.getEntity(JSONObject.class);
        authenticate(userAsJson);
        defaultAuthenticatedUserAsJson = userAsJson;
        defaultAuthenticatedUser = User.withEmailAndUsername(
                userAsJson.getString(UserJson.EMAIL),
                userAsJson.getString(UserJson.USER_NAME)
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

    protected IdentificationRestTestUtils identificationUtils() {
        return IdentificationRestTestUtils.withWebResourceAndAuthCookie(
                resource,
                authCookie,
                currentAuthenticatedUser
        );
    }

    protected JSONObject createAUser() {
        JSONObject newUser = userUtils().validForCreation();
        ClientResponse response = createUser(
                newUser
        );
        newUser = response.getEntity(JSONObject.class);
        return newUser;
    }


    protected PersistentSessionRestTestUtils persistentSessionsRestTestUtils(){
        return new PersistentSessionRestTestUtils(
                resource
        );
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

    public static String getUsersBaseUri(String username) {
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
