package org.triple_brain.service.utils;

import com.google.gson.Gson;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.codehaus.jettison.json.JSONObject;
import org.triple_brain.module.common_utils.Uris;
import org.triple_brain.module.model.User;
import org.triple_brain.module.model.graph.edge.Edge;
import org.triple_brain.module.model.graph.edge.EdgePojo;

import javax.ws.rs.core.NewCookie;
import java.net.URI;
import java.util.Map;
import java.util.Set;

/*
* Copyright Mozilla Public License 1.1
*/
public class EdgeRestTestUtils {

    private WebResource resource;
    private NewCookie authCookie;
    private GraphRestTestUtils graphUtils;
    private Gson gson = new Gson();
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

    public ClientResponse updateEdgeLabel(String label, Edge edge)throws Exception{
        ClientResponse response = resource
                .path(edge.uri().toString())
                .path("label")
                .queryParam("label", label)
                .cookie(authCookie)
                .post(ClientResponse.class);
        return response;
    }

    public ClientResponse removeEdgeBetweenVertexAAndB() throws Exception{
        Edge edgeBetweenAAndB = edgeBetweenAAndB();
        ClientResponse response = resource
                .path(edgeBetweenAAndB.uri().toString())
                .cookie(authCookie)
                .delete(ClientResponse.class);
        return response;
    }

    public Edge edgeWithUri(URI edgeUri){
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

    public Edge edgeBetweenTwoVerticesUriGivenEdges(
            URI firstVertexUri,
            URI secondVertexUri,
            Map<URI, ? extends Edge> edges
    ){
        try{
            for(Edge edge : edges.values()){
                URI sourceVertexId = edge.sourceVertex().uri();
                URI destinationVertexId = edge.destinationVertex().uri();
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

    public Edge edgeBetweenAAndB(){
        return edgeBetweenTwoVerticesUriGivenEdges(
                graphUtils.vertexAUri(),
                graphUtils.vertexBUri(),
                graphUtils.wholeGraph().edges()
        );
    }

    private boolean oneOfTwoUriIsUri(URI first, URI second, URI toCompare){
        return first.equals(toCompare) ||
                second.equals(toCompare);
    }

}
