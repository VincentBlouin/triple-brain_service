/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.utils;

import com.google.gson.Gson;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import guru.bubl.module.common_utils.NoEx;
import guru.bubl.module.common_utils.Uris;
import guru.bubl.module.model.User;
import guru.bubl.module.model.UserUris;
import guru.bubl.module.model.graph.edge.Edge;
import guru.bubl.module.model.graph.edge.EdgePojo;
import guru.bubl.module.model.json.LocalizedStringJson;
import guru.bubl.service.SessionHandler;
import org.codehaus.jettison.json.JSONObject;

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
    private String xsrfToken;

    public static EdgeRestTestUtils withWebResourceAndAuthCookie(WebResource resource, NewCookie authCookie, User authenticatedUser, String xsrfToken) {
        return new EdgeRestTestUtils(resource, authCookie, authenticatedUser, xsrfToken);
    }

    protected EdgeRestTestUtils(WebResource resource, NewCookie authCookie, User authenticatedUser, String xsrfToken) {
        this.resource = resource;
        this.authCookie = authCookie;
        graphUtils = GraphRestTestUtils.withWebResourceAndAuthCookie(
                authCookie,
                authenticatedUser,
                xsrfToken
        );
        this.xsrfToken = xsrfToken;
    }

    public ClientResponse updateEdgeLabel(String label, Edge edge) {
        return NoEx.wrap(() -> {
            JSONObject localizedLabel = new JSONObject().put(
                    LocalizedStringJson.content.name(),
                    label
            );
            return resource
                    .path(edge.uri().toString())
                    .path("label")
                    .cookie(authCookie)
                    .header(SessionHandler.X_XSRF_TOKEN, xsrfToken)
                    .post(ClientResponse.class, localizedLabel);
        }).get();
    }

    public ClientResponse removeEdgeBetweenVertexAAndB() {
        Edge edgeBetweenAAndB = edgeBetweenAAndB();
        return resource
                .path(edgeBetweenAAndB.uri().toString())
                .cookie(authCookie)
                .header(SessionHandler.X_XSRF_TOKEN, xsrfToken)
                .delete(ClientResponse.class);
    }

    public Edge edgeWithUri(URI edgeUri) {
        ClientResponse response = resource
                .path("service")
                .path("test")
                .path("edge")
                .path(Uris.encodeURL(edgeUri.toString()))
                .cookie(authCookie)
                .header(SessionHandler.X_XSRF_TOKEN, xsrfToken)
                .get(ClientResponse.class);
        return gson.fromJson(
                response.getEntity(JSONObject.class).toString(),
                EdgePojo.class
        );
    }

    public ClientResponse updateNoteOfEdge(String note, Edge edge) {
        return resource
                .path(edge.uri().getPath())
                .path("comment")
                .cookie(authCookie)
                .type(MediaType.TEXT_PLAIN)
                .header(SessionHandler.X_XSRF_TOKEN, xsrfToken)
                .post(ClientResponse.class, note);
    }

    public Edge edgeBetweenTwoVerticesUriGivenEdges(
            URI firstVertexUri,
            URI secondVertexUri,
            Map<URI, ? extends Edge> edges
    ) {
        try {
            for (Edge edge : edges.values()) {
                URI sourceVertexId = edge.sourceUri();
                URI destinationVertexId = edge.destinationUri();
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
                .header(SessionHandler.X_XSRF_TOKEN, xsrfToken)
                .post(ClientResponse.class);
    }

    private boolean oneOfTwoUriIsUri(URI first, URI second, URI toCompare) {
        return first.equals(toCompare) ||
                second.equals(toCompare);
    }

}
