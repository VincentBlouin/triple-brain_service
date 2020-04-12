/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import guru.bubl.module.common_utils.NoEx;
import guru.bubl.module.common_utils.Uris;
import guru.bubl.module.model.User;
import guru.bubl.module.model.UserUris;
import guru.bubl.module.model.graph.ShareLevel;
import guru.bubl.module.model.graph.edge.Edge;
import guru.bubl.module.model.graph.edge.EdgePojo;
import guru.bubl.module.model.graph.vertex.Vertex;
import guru.bubl.module.model.graph.vertex.Vertex;
import guru.bubl.module.model.graph.vertex.VertexJson;
import guru.bubl.module.model.graph.vertex.VertexPojo;
import guru.bubl.module.model.json.LocalizedStringJson;
import guru.bubl.service.SessionHandler;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class VertexRestTestUtils {

    private WebResource resource;
    private NewCookie authCookie;
    private User authenticatedUser;
    private Gson gson = new Gson();
    private String xsrfToken;

    public static VertexRestTestUtils withWebResourceAndAuthCookie(WebResource resource, NewCookie authCookie, User authenticatedUser, String xsrfToken) {
        return new VertexRestTestUtils(resource, authCookie, authenticatedUser, xsrfToken);
    }

    protected VertexRestTestUtils(WebResource resource, NewCookie authCookie, User authenticatedUser, String xsrfToken) {
        this.resource = resource;
        this.authCookie = authCookie;
        this.authenticatedUser = authenticatedUser;
        this.xsrfToken = xsrfToken;
    }

    public ClientResponse updateVertexLabelUsingRest(URI vertexUri, String label) {
        try {
            JSONObject localizedLabel = new JSONObject().put(
                    LocalizedStringJson.content.name(),
                    label
            );
            return resource
                    .path(vertexUri.getPath())
                    .path("label")
                    .cookie(authCookie)
                    .header(SessionHandler.X_XSRF_TOKEN, xsrfToken)
                    .post(ClientResponse.class, localizedLabel);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public Vertex vertexWithUriOfAnyUser(URI vertexUri) {
        ClientResponse response = resource
                .path("service")
                .path("test")
                .path("vertex")
                .path(Uris.encodeURL(vertexUri.toString()))
                .cookie(authCookie)
                .header(SessionHandler.X_XSRF_TOKEN, xsrfToken)
                .get(ClientResponse.class);
        JSONObject jsonObject = response.getEntity(JSONObject.class);
        return vertexFromJson(
                jsonObject
        );
    }


    public Set<Edge> connectedEdgesOfVertexWithURI(URI vertexUri) {
        ClientResponse response = resource
                .path("service")
                .path("test")
                .path("vertex")
                .path(Uris.encodeURL(vertexUri.toString()))
                .path("connected_edges")
                .cookie(authCookie)
                .header(SessionHandler.X_XSRF_TOKEN, xsrfToken)
                .get(ClientResponse.class);
        JSONArray jsonArray = response.getEntity(JSONArray.class);
        return gson.fromJson(
                jsonArray.toString(),
                new TypeToken<Set<EdgePojo>>() {
                }.getType()
        );
    }

    public boolean vertexWithUriHasDestinationVertexWithUri(URI vertexUri, URI destinationVertexUri) {
        ClientResponse response = resource
                .path("service")
                .path("test")
                .path("vertex")
                .path(Uris.encodeURL(vertexUri.toString()))
                .path("has_destination")
                .path(Uris.encodeURL(destinationVertexUri.toString()))
                .cookie(authCookie)
                .header(SessionHandler.X_XSRF_TOKEN, xsrfToken)
                .get(ClientResponse.class);
        String hasDestinationStr = response.getEntity(String.class);
        return Boolean.valueOf(hasDestinationStr);
    }

    public URI uriOfVertex(JSONObject jsonObject) {
        return vertexFromJson(
                jsonObject
        ).uri();
    }

    public ClientResponse updateVertexANote(String note) {
        return resource
                .path(graphUtils().vertexAUri().getPath())
                .path("comment")
                .cookie(authCookie)
                .type(MediaType.TEXT_PLAIN)
                .header(SessionHandler.X_XSRF_TOKEN, xsrfToken)
                .post(ClientResponse.class, note);
    }

    public ClientResponse addAVertexToForkWithUri(URI vertexUri) {
        return resource
                .path(vertexUri.getPath())
                .cookie(authCookie)
                .type(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(SessionHandler.X_XSRF_TOKEN, xsrfToken)
                .post(ClientResponse.class, "{}");
    }

    public ClientResponse makePublicVertexWithUri(URI vertexUri) {
        return this.setShareLevel(
                vertexUri,
                ShareLevel.PUBLIC
        );
    }

    public ClientResponse setShareLevel(URI uri, ShareLevel shareLevel) {
        return NoEx.wrap(() -> {
            ClientResponse clientResponse = resource
                    .path(uri.getPath())
                    .path("shareLevel")
                    .cookie(authCookie)
                    .header(SessionHandler.X_XSRF_TOKEN, xsrfToken)
                    .post(ClientResponse.class, new JSONObject().put(
                            "shareLevel", shareLevel.name()
                    ));
            assertThat(
                    clientResponse.getStatus(),
                    is(Response.Status.NO_CONTENT.getStatusCode())
            );
            return clientResponse;
        }).get();
    }

    public ClientResponse makePrivateVertexWithUri(URI vertexUri) {
        return this.setShareLevel(
                vertexUri,
                ShareLevel.PRIVATE
        );
    }

    public String getVertexBaseUri() {
        return new UserUris(authenticatedUser).baseVertexUri().toString();
    }

    public ClientResponse removeVertexB() {
        return resource
                .path(graphUtils().vertexBUri().getPath())
                .cookie(authCookie)
                .header(SessionHandler.X_XSRF_TOKEN, xsrfToken)
                .delete(ClientResponse.class);
    }

    public URI baseVertexUri() {
        return new UserUris(
                authenticatedUser
        ).baseVertexUri();
    }

    public Vertex createSingleVertex() {
        return VertexJson.fromJson(
                responseForCreateSingleVertex().getEntity(
                        JSONObject.class
                )
        );
    }

    public ClientResponse responseForCreateSingleVertex() {
        return resource
                .path(
                        new UserUris(authenticatedUser).baseVertexUri().getPath()
                )
                .cookie(authCookie)
                .header(SessionHandler.X_XSRF_TOKEN, xsrfToken)
                .post(ClientResponse.class);
    }


    public ClientResponse makePattern(URI uri) {
        return resource
                .path(
                        uri.getPath()
                )
                .path("pattern")
                .cookie(authCookie)
                .header(SessionHandler.X_XSRF_TOKEN, xsrfToken)
                .post(ClientResponse.class);
    }

    private GraphRestTestUtils graphUtils() {
        return GraphRestTestUtils.withWebResourceAndAuthCookie(
                authCookie,
                authenticatedUser,
                xsrfToken
        );
    }

    protected Vertex vertexFromJson(JSONObject jsonObject) {
        return gson.fromJson(
                jsonObject.toString(),
                VertexPojo.class
        );
    }
}
