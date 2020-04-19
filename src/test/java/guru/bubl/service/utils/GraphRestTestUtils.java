/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.utils;

import com.sun.jersey.api.client.ClientResponse;
import guru.bubl.module.model.User;
import guru.bubl.module.model.UserUris;
import guru.bubl.module.model.center_graph_element.CenterGraphElementPojo;
import guru.bubl.module.model.graph.subgraph.SubGraphJson;
import guru.bubl.module.model.graph.group_relation.GroupRelationPojo;
import guru.bubl.module.model.graph.subgraph.SubGraphPojo;
import guru.bubl.module.model.graph.vertex.Vertex;
import guru.bubl.module.model.json.CenterGraphElementsJson;
import guru.bubl.module.model.json.JsonUtils;
import guru.bubl.service.SessionHandler;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

import static guru.bubl.service.utils.GraphManipulationRestTestUtils.getUsersBaseUri;
import static guru.bubl.service.utils.RestTestUtils.resource;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class GraphRestTestUtils {

    private NewCookie authCookie;

    private VertexRestTestUtils vertexUtils;

    private static JSONObject
            vertexA,
            vertexB,
            vertexC,
            todoGroupRelationJson;

    private User authenticatedUser;

    private String xsrfToken;

    public static GraphRestTestUtils withWebResourceAndAuthCookie(NewCookie authCookie, User authenticatedUser, String xsrfToken) {
        return new GraphRestTestUtils(authCookie, authenticatedUser, xsrfToken);
    }

    protected GraphRestTestUtils(NewCookie authCookie, User authenticatedUser, String xsrfToken) {
        this.authCookie = authCookie;
        this.authenticatedUser = authenticatedUser;
        vertexUtils = VertexRestTestUtils.withWebResourceAndAuthCookie(
                resource,
                authCookie,
                authenticatedUser,
                xsrfToken
        );
        this.xsrfToken = xsrfToken;
    }

    public SubGraphPojo graphWithCenterVertexUri(URI vertexUri) {
        return graphWithCenterVertexUri(
                vertexUri,
                authCookie,
                xsrfToken
        );
    }

    public static SubGraphPojo graphWithCenterVertexUri(URI vertexUri, NewCookie authCookie, String xsrfToken) {
        ClientResponse response = resource
                .path(vertexUri.getPath())
                .path("surround_graph")
                .queryParam("center", "true")
                .cookie(authCookie)
                .header(SessionHandler.X_XSRF_TOKEN, xsrfToken)
                .get(ClientResponse.class);
        assertThat(
                response.getStatus(),
                is(Response.Status.OK.getStatusCode())
        );
        return SubGraphJson.fromJson(
                response.getEntity(JSONObject.class).toString()
        );
    }

    public JSONArray makeGraphHave3SerialVerticesWithLongLabels() {
        return makeGraphHave3SerialVerticesWithLongLabelsUsingCookie(
                authCookie
        );
    }

    public JSONArray makeGraphHave3SerialVerticesWithLongLabelsUsingCookie(NewCookie authCookie) {
        ClientResponse response = resource
                .path("service")
                .path("test")
                .path("rebuild_service_scenario")
                .cookie(authCookie)
                .header(SessionHandler.X_XSRF_TOKEN, xsrfToken)
                .get(ClientResponse.class);
        JSONArray graphElements = response.getEntity(JSONArray.class);
        try {
            vertexA = graphElements.getJSONObject(0);
            vertexB = graphElements.getJSONObject(1);
            vertexC = graphElements.getJSONObject(2);
            todoGroupRelationJson = graphElements.getJSONObject(3);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return graphElements;
    }

    public URI vertexAUri() {
        return vertexUtils.uriOfVertex(
                vertexA
        );
    }

    public URI vertexBUri() {
        return vertexUtils.uriOfVertex(
                vertexB
        );
    }

    public URI vertexCUri() {
        return vertexUtils.uriOfVertex(
                vertexC
        );
    }

    public GroupRelationPojo getTodoGroupRelation() {
        return JsonUtils.getGson().fromJson(
                todoGroupRelationJson.toString(),
                GroupRelationPojo.class
        );
    }

    public Vertex vertexA() {
        return vertexUtils.vertexWithUriOfAnyUser(vertexAUri());
    }

    public Vertex vertexB() {
        return vertexUtils.vertexWithUriOfAnyUser(vertexBUri());
    }

    public Vertex vertexC() {
        return vertexUtils.vertexWithUriOfAnyUser(vertexCUri());
    }

    public String getCurrentGraphUri() {
        return new UserUris(
                authenticatedUser
        ).graphUri().toString();
    }

    public URI getElementUriInResponse(ClientResponse response) {
        return URI.create(
                URI.create(
                        response.getHeaders().get("Location").get(0)
                ).getPath()
        );
    }

    public ClientResponse getCenterGraphElementsResponse() {
        return getCenterGraphElementsResponseForGraphElementTypeAndUser(
                authenticatedUser
        );
    }

    public ClientResponse getPublicCenterGraphElementsResponse() {
        return getPublicCenterGraphElementsResponseForUser(
                authenticatedUser
        );
    }

    public ClientResponse getCenterGraphElementsResponseForGraphElementTypeAndUser(User user) {
        return resource
                .path(user.id())
                .path("center-elements")
                .cookie(authCookie)
                .header(SessionHandler.X_XSRF_TOKEN, xsrfToken)
                .get(ClientResponse.class);
    }

    public ClientResponse getPublicCenterGraphElementsResponseForUser(User user) {
        return resource
                .path("service")
                .path("center-elements")
                .path("public")
                .path("user")
                .path(user.username())
                .cookie(authCookie)
                .header(SessionHandler.X_XSRF_TOKEN, xsrfToken)
                .get(ClientResponse.class);
    }

    public List<CenterGraphElementPojo> getCenterGraphElements() {
        return getCenterGraphElementsFromClientResponse(
                getCenterGraphElementsResponse()
        );
    }

    public static List<CenterGraphElementPojo> getCenterGraphElementsFromClientResponse(ClientResponse clientResponse) {
        return CenterGraphElementsJson.fromJson(
                clientResponse.getEntity(
                        String.class
                ));
    }

    public CenterGraphElementPojo getCenterGraphElementHavingUriInElements(URI uri, List<CenterGraphElementPojo> elements) {
        return elements.stream().filter(
                (centerGraphElement) -> centerGraphElement.getGraphElement().uri().equals(
                        uri
                )
        ).findAny().get();
    }

    public ClientResponse getNonOwnedGraphOfCentralVertex(Vertex vertex) {
        return getNonOwnedGraphOfCentralVertex(
                vertex,
                xsrfToken
        );
    }

    public ClientResponse getNonOwnedGraphOfCentralVertex(Vertex vertex, String xsrfToken) {
        return getNonOwnedGraphOfCentralVertexWithUri(
                vertex.uri(),
                xsrfToken
        );
    }

    public ClientResponse getNonOwnedGraphOfCentralVertexWithUri(URI vertexUri) {
        return getNonOwnedGraphOfCentralVertexWithUri(vertexUri, xsrfToken);
    }

    public ClientResponse getNonOwnedGraphOfCentralVertexWithUri(URI vertexUri, String xsrfToken) {
        return getNonOwnedGraphOfCentralVertexWithUriAtDepth(
                vertexUri,
                1,
                xsrfToken
        );
    }

    public ClientResponse getNonOwnedGraphOfCentralVertexWithUriAtDepth(URI vertexUri, Integer depth) {
        return getNonOwnedGraphOfCentralVertexWithUriAtDepth(
                vertexUri, depth, xsrfToken
        );
    }

    public ClientResponse getNonOwnedGraphOfCentralVertexWithUriAtDepth(URI vertexUri, Integer depth, String xsrfToken) {
        String shortId = UserUris.graphElementShortId(
                vertexUri
        );
        return resource
                .path(getUsersBaseUri(UserUris.ownerUserNameFromUri(vertexUri)))
                .path("non_owned")
                .path("vertex")
                .path(shortId)
                .path("surround_graph")
                .queryParam("depth", depth.toString())
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .cookie(authCookie)
                .header(SessionHandler.X_XSRF_TOKEN, xsrfToken)
                .get(ClientResponse.class);
    }

    public CenterGraphElementPojo getCenterWithUri(List<CenterGraphElementPojo> centers, URI centerUri) {
        for (CenterGraphElementPojo centerGraphElement : centers) {
            if (centerGraphElement.getGraphElement().uri().equals(centerUri)) {
                return centerGraphElement;
            }
        }
        return null;
    }
}
