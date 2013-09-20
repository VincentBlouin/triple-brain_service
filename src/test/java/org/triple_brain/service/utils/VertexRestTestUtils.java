package org.triple_brain.service.utils;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.triple_brain.module.common_utils.Uris;
import org.triple_brain.module.model.User;
import org.triple_brain.module.model.json.graph.VertexJson;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.net.URI;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/*
* Copyright Mozilla Public License 1.1
*/
public class VertexRestTestUtils {

    private WebResource resource;
    private NewCookie authCookie;
    private User authenticatedUser;

    public static VertexRestTestUtils withWebResourceAndAuthCookie(WebResource resource, NewCookie authCookie, User authenticatedUser){
        return new VertexRestTestUtils(resource, authCookie, authenticatedUser);
    }
    protected VertexRestTestUtils(WebResource resource, NewCookie authCookie, User authenticatedUser){
        this.resource = resource;
        this.authCookie = authCookie;
        this.authenticatedUser = authenticatedUser;
    }

    public JSONObject vertexWithUri(URI vertexUri){
        ClientResponse response = resource
                .path("service")
                .path("users")
                .path("test")
                .path("vertex")
                .path(Uris.encodeURL(vertexUri.toString()))
                .cookie(authCookie)
                .get(ClientResponse.class);
        return response.getEntity(JSONObject.class);
    }

    public JSONArray connectedEdgesOfVertexWithURI(URI vertexUri) {
        ClientResponse response = resource
                .path("service")
                .path("users")
                .path("test")
                .path("vertex")
                .path(Uris.encodeURL(vertexUri.toString()))
                .path("connected_edges")
                .cookie(authCookie)
                .get(ClientResponse.class);
        return response.getEntity(JSONArray.class);
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
        try{
            return Uris.get(jsonObject.getString(VertexJson.URI));
        }catch(JSONException e){
            throw new RuntimeException(e);
        }
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

    public boolean vertexIsInVertices(JSONObject vertex, JSONObject vertices){
        try{
            return vertices.has(vertex.getString(VertexJson.URI));
        }catch(JSONException e){
            throw new RuntimeException(e);
        }
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

    private GraphRestTestUtils graphUtils(){
        return GraphRestTestUtils.withWebResourceAndAuthCookie(
                resource,
                authCookie,
                authenticatedUser
        );
    }
}
