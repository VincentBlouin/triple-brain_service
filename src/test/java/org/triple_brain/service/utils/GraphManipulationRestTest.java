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

    protected User authenticatedUser;
    protected  VertexRestTestUtils vertexUtils;
    protected  EdgeRestTestUtils edgeUtils;
    protected UserRestTestUtils userUtils;
    protected GraphRestTestUtils graphUtils;

    @Before
    public void before_graph_manipulator_rest_test() throws Exception{
        userUtils = UserRestTestUtils.withWebResource(
                resource
        );
        JSONObject userAsJson = userUtils.validForCreation();
        createAUser(userAsJson);
        authenticate(userAsJson);
        authenticatedUser = User.withUsernameAndEmail(
                userAsJson.getString(UserJsonFields.USER_NAME),
                userAsJson.getString(UserJsonFields.EMAIL)
        );
        authenticatedUser.password(DEFAULT_PASSWORD);

        vertexUtils = VertexRestTestUtils.withWebResourceAndAuthCookie(
                resource,
                authCookie,
                authenticatedUser
        );
        edgeUtils = EdgeRestTestUtils.withWebResourceAndAuthCookie(
                resource,
                authCookie,
                authenticatedUser
        );
        graphUtils = GraphRestTestUtils.withWebResourceAndAuthCookie(
                resource,
                authCookie,
                authenticatedUser
        );
        deleteAllUserVerticesFromSearch();
        graphUtils.makeGraphHave3SerialVerticesWithLongLabels();
    }


    private void deleteAllUserVerticesFromSearch() {
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




    public boolean graphElementWithIdExistsInCurrentGraph(URI graphElementId){
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

    protected JSONObject vertexA(){
        return graphUtils.vertexA();
    }
    protected JSONObject vertexB(){
        return graphUtils.vertexB();
    }
    protected JSONObject vertexC(){
        return graphUtils.vertexC();
    }

    protected URI vertexAUri(){
        return graphUtils.vertexAUri();
    }
    protected URI vertexBUri(){
        return graphUtils.vertexBUri();
    }
    protected URI vertexCUri(){
        return graphUtils.vertexCUri();
    }
}
