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
import guru.bubl.module.model.graph.ShareLevel;
import guru.bubl.module.model.graph.relation.Relation;
import guru.bubl.module.model.graph.relation.RelationPojo;
import guru.bubl.module.model.json.LocalizedStringJson;
import guru.bubl.service.SessionHandler;
import org.codehaus.jettison.json.JSONException;
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

    public ClientResponse updateEdgeLabel(String label, Relation relation) {
        return NoEx.wrap(() -> {
            JSONObject localizedLabel = new JSONObject().put(
                    LocalizedStringJson.content.name(),
                    label
            );
            return resource
                    .path(relation.uri().toString())
                    .path("label")
                    .cookie(authCookie)
                    .header(SessionHandler.X_XSRF_TOKEN, xsrfToken)
                    .post(ClientResponse.class, localizedLabel);
        }).get();
    }

    public ClientResponse removeEdgeBetweenVertexAAndB() {
        Relation relationBetweenAAndB = edgeBetweenAAndB();
        return resource
                .path(relationBetweenAAndB.uri().toString())
                .cookie(authCookie)
                .header(SessionHandler.X_XSRF_TOKEN, xsrfToken)
                .delete(ClientResponse.class);
    }

    public Relation edgeWithUri(URI edgeUri) {
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
                RelationPojo.class
        );
    }

    public ClientResponse updateNoteOfEdge(String note, Relation relation) {
        return resource
                .path(relation.uri().getPath())
                .path("comment")
                .cookie(authCookie)
                .type(MediaType.TEXT_PLAIN)
                .header(SessionHandler.X_XSRF_TOKEN, xsrfToken)
                .post(ClientResponse.class, note);
    }

    public Relation edgeBetweenTwoVerticesUriGivenEdges(
            URI firstVertexUri,
            URI secondVertexUri,
            Map<URI, ? extends Relation> edges
    ) {
        try {
            for (Relation relation : edges.values()) {
                URI sourceVertexId = relation.sourceUri();
                URI destinationVertexId = relation.destinationUri();
                if (oneOfTwoUriIsUri(firstVertexUri, secondVertexUri, sourceVertexId) &&
                        oneOfTwoUriIsUri(firstVertexUri, secondVertexUri, destinationVertexId)) {
                    return relation;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        throw new RuntimeException("none found !");
    }

    public Relation edgeBetweenAAndB() {
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
        try {
            return resource
                    .path(userUris.baseEdgeUri().getPath())
                    .cookie(authCookie)
                    .header(SessionHandler.X_XSRF_TOKEN, xsrfToken)
                    .post(ClientResponse.class,
                            new JSONObject().put(
                                    "sourceUri",
                                    sourceVertexUri.toString()
                            ).put(
                                    "destinationUri",
                                    destinationVertexUri.toString()
                            ).put(
                                    "sourceShareLevel",
                                    ShareLevel.PRIVATE.name()
                            ).put(
                                    "destinationSharelevel",
                                    ShareLevel.PRIVATE.name()
                            )
                    );
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean oneOfTwoUriIsUri(URI first, URI second, URI toCompare) {
        return first.equals(toCompare) ||
                second.equals(toCompare);
    }

}
