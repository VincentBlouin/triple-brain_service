package org.triple_brain.service.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.triple_brain.module.common_utils.Uris;
import org.triple_brain.module.model.User;
import org.triple_brain.module.model.UserUris;
import org.triple_brain.module.model.graph.edge.Edge;
import org.triple_brain.module.model.graph.edge.EdgePojo;
import org.triple_brain.module.model.graph.vertex.VertexInSubGraph;
import org.triple_brain.module.model.graph.vertex.VertexInSubGraphPojo;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/*
* Copyright Mozilla Public License 1.1
*/
public class VertexRestTestUtils {

    private WebResource resource;
    private NewCookie authCookie;
    private User authenticatedUser;
    private Gson gson = new Gson();

    public static VertexRestTestUtils withWebResourceAndAuthCookie(WebResource resource, NewCookie authCookie, User authenticatedUser){
        return new VertexRestTestUtils(resource, authCookie, authenticatedUser);
    }
    protected VertexRestTestUtils(WebResource resource, NewCookie authCookie, User authenticatedUser){
        this.resource = resource;
        this.authCookie = authCookie;
        this.authenticatedUser = authenticatedUser;
    }

    public VertexInSubGraph vertexWithUriOfAnyUser(URI vertexUri){
        ClientResponse response = resource
                .path("service")
                .path("users")
                .path("test")
                .path("vertex")
                .path(Uris.encodeURL(vertexUri.toString()))
                .cookie(authCookie)
                .get(ClientResponse.class);
        JSONObject jsonObject = response.getEntity(JSONObject.class);
        return vertexFromJson(
                jsonObject
        );
    }

    public VertexInSubGraph vertexWithUriOfCurrentUser(URI vertexUri){
        ClientResponse response = resource
                .path(vertexUri.getPath())
                .cookie(authCookie)
                .accept(MediaType.APPLICATION_JSON)
                .get(ClientResponse.class);
        assertThat(
                response.getStatus(),
                is(Response.Status.OK.getStatusCode())
        );
        JSONObject jsonObject = response.getEntity(JSONObject.class);
        return vertexFromJson(
                jsonObject
        );
    }

    public Set<Edge> connectedEdgesOfVertexWithURI(URI vertexUri) {
        ClientResponse response = resource
                .path("service")
                .path("users")
                .path("test")
                .path("vertex")
                .path(Uris.encodeURL(vertexUri.toString()))
                .path("connected_edges")
                .cookie(authCookie)
                .get(ClientResponse.class);
        JSONArray jsonArray = response.getEntity(JSONArray.class);
        return gson.fromJson(
                jsonArray.toString(),
                new TypeToken<Set<EdgePojo>>() {
                }.getType()
        );
    }

    public boolean vertexWithUriHasDestinationVertexWithUri(URI vertexUri, URI destinationVertexUri){
        ClientResponse response = resource
                .path("service")
                .path("users")
                .path("test")
                .path("vertex")
                .path(Uris.encodeURL(vertexUri.toString()))
                .path("has_destination")
                .path(Uris.encodeURL(destinationVertexUri.toString()))
                .cookie(authCookie)
                .get(ClientResponse.class);
        String hasDestinationStr = response.getEntity(String.class);
        return Boolean.valueOf(hasDestinationStr);
    }

    public URI uriOfVertex(JSONObject jsonObject){
        return vertexFromJson(
                jsonObject
        ).uri();
    }

    public ClientResponse updateVertexANote(String note) throws Exception {
        ClientResponse response = resource
                .path(graphUtils().vertexAUri().getPath())
                .path("comment")
                .cookie(authCookie)
                .type(MediaType.TEXT_PLAIN)
                .post(ClientResponse.class, note);
        return response;
    }

    public ClientResponse addAVertexToVertexAWithUri(URI vertexUri) throws Exception {
        ClientResponse response = resource
                .path(vertexUri.getPath())
                .cookie(authCookie)
                .post(ClientResponse.class);
        return response;
    }

    public ClientResponse makePublicVertexWithUri(URI vertexUri){
        ClientResponse clientResponse = resource
                .path(vertexUri.getPath())
                .path("public_access")
                .cookie(authCookie)
                .post(ClientResponse.class);
        assertThat(
                clientResponse.getStatus(),
                is(Response.Status.OK.getStatusCode())
        );
        return clientResponse;
    }

    public ClientResponse makePrivateVertexWithUri(URI vertexUri){
        ClientResponse clientResponse = resource
                .path(vertexUri.getPath())
                .path("public_access")
                .cookie(authCookie)
                .delete(ClientResponse.class);
        assertThat(
                clientResponse.getStatus(),
                is(Response.Status.OK.getStatusCode())
        );
        return clientResponse;
    }

    public ClientResponse removeVertexB(){
        ClientResponse response = resource
                .path(graphUtils().vertexBUri().getPath())
                .cookie(authCookie)
                .delete(ClientResponse.class);
        return response;
    }

    public URI baseVertexUri(){
        return new UserUris(
                authenticatedUser
        ).baseVertexUri();
    }

    private GraphRestTestUtils graphUtils(){
        return GraphRestTestUtils.withWebResourceAndAuthCookie(
                resource,
                authCookie,
                authenticatedUser
        );
    }

    protected VertexInSubGraph vertexFromJson(JSONObject jsonObject){
        return gson.fromJson(
                jsonObject.toString(),
                VertexInSubGraphPojo.class
        );
    }
}
