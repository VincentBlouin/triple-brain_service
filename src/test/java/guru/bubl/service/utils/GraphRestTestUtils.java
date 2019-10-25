/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.utils;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import guru.bubl.module.model.User;
import guru.bubl.module.model.UserUris;
import guru.bubl.module.model.center_graph_element.CenterGraphElementPojo;
import guru.bubl.module.model.graph.SubGraphJson;
import guru.bubl.module.model.graph.subgraph.SubGraphPojo;
import guru.bubl.module.model.graph.vertex.Vertex;
import guru.bubl.module.model.graph.vertex.VertexInSubGraph;
import guru.bubl.module.model.json.CenterGraphElementsJson;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import java.util.Set;

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
            vertexC;

    private User authenticatedUser;

    public static GraphRestTestUtils withWebResourceAndAuthCookie(NewCookie authCookie, User authenticatedUser) {
        return new GraphRestTestUtils(authCookie, authenticatedUser);
    }

    protected GraphRestTestUtils(NewCookie authCookie, User authenticatedUser) {
        this.authCookie = authCookie;
        this.authenticatedUser = authenticatedUser;
        vertexUtils = VertexRestTestUtils.withWebResourceAndAuthCookie(
                resource,
                authCookie,
                authenticatedUser
        );
    }

    public SubGraphPojo graphWithCenterVertexUri(URI vertexUri) {
        return graphWithCenterVertexUri(
                vertexUri,
                authCookie
        );
    }

    public static SubGraphPojo graphWithCenterVertexUri(URI vertexUri, NewCookie authCookie) {
        ClientResponse response = resource
                .path(vertexUri.getPath())
                .path("surround_graph")
                .queryParam("center", "true")
                .cookie(authCookie)
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
                .path("make_graph_have_3_serial_vertices_with_long_labels")
                .cookie(authCookie)
                .get(ClientResponse.class);
        JSONArray verticesABAndC = response.getEntity(JSONArray.class);
        try {
            vertexA = verticesABAndC.getJSONObject(0);
            vertexB = verticesABAndC.getJSONObject(1);
            vertexC = verticesABAndC.getJSONObject(2);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return verticesABAndC;
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

    public VertexInSubGraph vertexA() {
        return vertexUtils.vertexWithUriOfAnyUser(vertexAUri());
    }

    public VertexInSubGraph vertexB() {
        return vertexUtils.vertexWithUriOfAnyUser(vertexBUri());
    }

    public VertexInSubGraph vertexC() {
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

    public CenterGraphElementPojo getCenterGraphElementHavingUriInElements(URI uri, Set<CenterGraphElementPojo> elements) {
        return elements.stream().filter(
                (centerGraphElement) -> centerGraphElement.getGraphElement().uri().equals(
                        uri
                )
        ).findAny().get();
    }

    public ClientResponse getNonOwnedGraphOfCentralVertex(Vertex vertex) {
        return getNonOwnedGraphOfCentralVertexWithUri(
                vertex.uri()
        );
    }

    public ClientResponse getNonOwnedGraphOfCentralVertexWithUri(URI vertexUri) {
        return getNonOwnedGraphOfCentralVertexWithUriAtDepth(
                vertexUri,
                1
        );
    }

    public ClientResponse getNonOwnedGraphOfCentralVertexWithUriAtDepth(URI vertexUri, Integer depth) {
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
