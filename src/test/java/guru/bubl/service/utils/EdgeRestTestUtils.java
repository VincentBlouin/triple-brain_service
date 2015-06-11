/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.utils;

import com.google.gson.Gson;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.codehaus.jettison.json.JSONObject;
import guru.bubl.module.common_utils.Uris;
import guru.bubl.module.model.User;
import guru.bubl.module.model.UserUris;
import guru.bubl.module.model.graph.edge.Edge;
import guru.bubl.module.model.graph.edge.EdgePojo;
import guru.bubl.module.model.json.LocalizedStringJson;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import java.net.URI;
import java.util.Map;

import static guru.bubl.module.common_utils.Uris.encodeURL;

public class EdgeRestTestUtils {

    private WebResource resource;
    private NewCookie authCookie;
    private GraphRestTestUtils graphUtils;
    private Gson gson = new Gson();

    public static EdgeRestTestUtils withWebResourceAndAuthCookie(WebResource resource, NewCookie authCookie, User authenticatedUser) {
        return new EdgeRestTestUtils(resource, authCookie, authenticatedUser);
    }

    protected EdgeRestTestUtils(WebResource resource, NewCookie authCookie, User authenticatedUser) {
        this.resource = resource;
        this.authCookie = authCookie;
        graphUtils = GraphRestTestUtils.withWebResourceAndAuthCookie(
                resource,
                authCookie,
                authenticatedUser
        );
    }

    public ClientResponse updateEdgeLabel(String label, Edge edge) throws Exception {
        JSONObject localizedLabel = new JSONObject().put(
                LocalizedStringJson.content.name(),
                label
        );
        return resource
                .path(edge.uri().toString())
                .path("label")
                .cookie(authCookie)
                .post(ClientResponse.class, localizedLabel);
    }

    public ClientResponse removeEdgeBetweenVertexAAndB() throws Exception {
        Edge edgeBetweenAAndB = edgeBetweenAAndB();
        return resource
                .path(edgeBetweenAAndB.uri().toString())
                .cookie(authCookie)
                .delete(ClientResponse.class);
    }

    public Edge edgeWithUri(URI edgeUri) {
        ClientResponse response = resource
                .path("service")
                .path("test")
                .path("edge")
                .path(Uris.encodeURL(edgeUri.toString()))
                .cookie(authCookie)
                .get(ClientResponse.class);
        return gson.fromJson(
                response.getEntity(JSONObject.class).toString(),
                EdgePojo.class
        );
    }

    public ClientResponse updateNoteOfEdge(String note, Edge edge){
        return resource
                .path(edge.uri().getPath())
                .path("comment")
                .cookie(authCookie)
                .type(MediaType.TEXT_PLAIN)
                .post(ClientResponse.class, note);
    }

    public Edge edgeBetweenTwoVerticesUriGivenEdges(
            URI firstVertexUri,
            URI secondVertexUri,
            Map<URI, ? extends Edge> edges
    ) {
        try {
            for (Edge edge : edges.values()) {
                URI sourceVertexId = edge.sourceVertex().uri();
                URI destinationVertexId = edge.destinationVertex().uri();
                if (oneOfTwoUriIsUri(firstVertexUri, secondVertexUri, sourceVertexId) &&
                        oneOfTwoUriIsUri(firstVertexUri, secondVertexUri, destinationVertexId)) {
                    return edge;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        throw new RuntimeException("none found !");
    }

    public Edge edgeBetweenAAndB() {
        return edgeBetweenTwoVerticesUriGivenEdges(
                graphUtils.vertexAUri(),
                graphUtils.vertexBUri(),
                graphUtils.graphWithCenterVertexUri(graphUtils.vertexAUri()).edges()
        );
    }

    public ClientResponse addRelationBetweenVertexAAndC() {
        return addRelationBetweenSourceAndDestinationVertexUri(
                graphUtils.vertexAUri(),
                graphUtils.vertexCUri()
        );
    }

    public ClientResponse addRelationBetweenSourceAndDestinationVertexUri(
            URI sourceVertexUri,
            URI destinationVertexUri
    ) {
        UserUris userUris = new UserUris(
                UserUris.ownerUserNameFromUri(sourceVertexUri)
        );
        return resource
                .path(userUris.baseEdgeUri().getPath())
                .queryParam("sourceVertexId", encodeURL(
                        sourceVertexUri.toString()
                ))
                .queryParam("destinationVertexId", encodeURL(
                        destinationVertexUri.toString()
                ))
                .cookie(authCookie)
                .post(ClientResponse.class);
    }

    private boolean oneOfTwoUriIsUri(URI first, URI second, URI toCompare) {
        return first.equals(toCompare) ||
                second.equals(toCompare);
    }

}
