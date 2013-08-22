package org.triple_brain.service.utils;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.triple_brain.module.common_utils.Uris;
import org.triple_brain.module.model.User;
import org.triple_brain.module.model.json.graph.EdgeJsonFields;
import org.triple_brain.module.model.json.graph.GraphJsonFields;

import javax.ws.rs.core.NewCookie;
import java.net.URI;

/*
* Copyright Mozilla Public License 1.1
*/
public class EdgeRestTestUtils {

    private WebResource resource;
    private NewCookie authCookie;
    private GraphRestTestUtils graphUtils;
    public static EdgeRestTestUtils withWebResourceAndAuthCookie(WebResource resource, NewCookie authCookie, User authenticatedUser){
        return new EdgeRestTestUtils(resource, authCookie, authenticatedUser);
    }
    protected EdgeRestTestUtils(WebResource resource, NewCookie authCookie, User authenticatedUser){
        this.resource = resource;
        this.authCookie = authCookie;
        graphUtils = GraphRestTestUtils.withWebResourceAndAuthCookie(
                resource,
                authCookie,
                authenticatedUser
        );
    }

    public URI uriOfEdge(JSONObject jsonObject){
        try{
            return Uris.get(jsonObject.getString(EdgeJsonFields.ID));
        }catch(JSONException e){
            throw new RuntimeException(e);
        }
    }

    public ClientResponse updateEdgeLabel(String label, JSONObject edge)throws Exception{
        ClientResponse response = resource
                .path(edge.getString(EdgeJsonFields.ID))
                .path("label")
                .queryParam("label", label)
                .cookie(authCookie)
                .post(ClientResponse.class);
        return response;
    }

    public ClientResponse removeEdgeBetweenVertexAAndB() throws Exception{
        JSONObject edgeBetweenAAndB = edgeBetweenAAndB();
        ClientResponse response = resource
                .path(edgeBetweenAAndB.getString(EdgeJsonFields.ID))
                .cookie(authCookie)
                .delete(ClientResponse.class);
        return response;
    }

    public JSONObject edgeWithUri(URI edgeUri){
        ClientResponse response = resource
                .path("service")
                .path("users")
                .path("test")
                .path("edge")
                .path(Uris.encodeURL(edgeUri.toString()))
                .cookie(authCookie)
                .get(ClientResponse.class);
        return response.getEntity(JSONObject.class);
    }

    public boolean edgeIsInEdges(JSONObject edge, JSONArray edges){
        try{
            for(int i = 0 ; i < edges.length() ; i++){
                JSONObject edgeToCompare = edges.getJSONObject(i);
                if(uriOfEdge(edgeToCompare).equals(uriOfEdge(edge))){
                    return true;
                }
            }
        }catch(JSONException e){
            throw new RuntimeException(e);
        }
        return false;
    }

    public JSONObject edgeBetweenTwoVerticesUriGivenEdges(URI firstVertexUri, URI secondVertexUri, JSONArray edges){
        try{
            for(int i = 0 ; i < edges.length(); i++){
                JSONObject edge = edges.getJSONObject(i);
                URI sourceVertexId = URI.create(
                        edge.getString(EdgeJsonFields.SOURCE_VERTEX_ID)
                );
                URI destinationVertexId = URI.create(
                        edge.getString(EdgeJsonFields.DESTINATION_VERTEX_ID)
                );
                if(oneOfTwoUriIsUri(firstVertexUri, secondVertexUri, sourceVertexId) &&
                        oneOfTwoUriIsUri(firstVertexUri, secondVertexUri, destinationVertexId)){
                    return edge;
                }
            }
        }catch(Exception e){
            throw new RuntimeException(e);
        }
        throw new RuntimeException("none found !");
    }

    public JSONObject edgeBetweenAAndB()throws Exception{
        JSONArray allEdges = graphUtils.wholeGraph().getJSONArray(
                GraphJsonFields.EDGES
        );
        return edgeBetweenTwoVerticesUriGivenEdges(
                graphUtils.vertexAUri(),
                graphUtils.vertexBUri(),
                allEdges
        );
    }

    private boolean oneOfTwoUriIsUri(URI first, URI second, URI toCompare){
        return first.equals(toCompare) ||
                second.equals(toCompare);
    }

}
