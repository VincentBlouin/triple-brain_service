package org.triple_brain.service.resources.vertex;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import org.codehaus.jettison.json.JSONObject;
import org.triple_brain.module.model.graph.*;
import org.triple_brain.module.model.graph.edge.Edge;
import org.triple_brain.module.model.graph.edge.EdgeFactory;
import org.triple_brain.module.model.graph.vertex.Vertex;
import org.triple_brain.module.model.graph.vertex.VertexFactory;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/*
* Copyright Mozilla Public License 1.1
*/
public class VertexGroupResource {

    @Inject
    VertexFactory vertexFactory;

    @Inject
    EdgeFactory edgeFactory;

    private UserGraph userGraph;

    @AssistedInject
    public VertexGroupResource(
            @Assisted UserGraph userGraph
    ) {
        this.userGraph = userGraph;
    }

    @POST
    @GraphTransactional
    @Path("/")
    public Response createGroup(JSONObject includedGraphElementsUri, @Context UriInfo info){
        Vertex newVertex = vertexFactory.createFromGraphElements(
                verticesFromUris(
                        includedGraphElementsUri.optJSONObject("vertices")
                ),
                edgesFromUris(
                        includedGraphElementsUri.optJSONObject("edges")
                )
        );
        URI baseUri = info.getBaseUri();
        /*
        * todo I have to provide absolute uri but I should only give relative uri
        * I give absolute uri because if I give relative jersey preprends current path
         */
        URI newVertexUri = URI.create(
                baseUri.getScheme() + "://" + baseUri.getAuthority() + newVertex.uri().getPath()
        );
        return Response.created(
                newVertexUri
        ).build();
    }

    private Set<Vertex> verticesFromUris(JSONObject uris){
        Set<Vertex> vertices = new HashSet<>();
        Iterator<String> keysIt = uris.keys();
        while(keysIt.hasNext()){
            String uri = keysIt.next();
            Vertex vertex = vertexFactory.createOrLoadUsingUri(
                    URI.create(uri)
            );
            vertices.add(
                    vertex
            );
        }
        return vertices;
    }

    private Set<Edge> edgesFromUris(JSONObject uris){
        Set<Edge> edges = new HashSet<>();
        Iterator<String> keysIt = uris.keys();
        while(keysIt.hasNext()){
            String uri = keysIt.next();
            Edge edge = edgeFactory.createOrLoadUsingUri(
                    URI.create(uri)
            );
            edges.add(
                    edge
            );
        }
        return edges;
    }

}