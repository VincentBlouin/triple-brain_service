/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.vertex;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import guru.bubl.module.model.graph.GraphTransactional;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import org.codehaus.jettison.json.JSONObject;
import guru.bubl.module.model.graph.edge.Edge;
import guru.bubl.module.model.graph.edge.EdgeFactory;
import guru.bubl.module.model.graph.vertex.Vertex;
import guru.bubl.module.model.graph.vertex.VertexFactory;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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
            Vertex vertex = vertexFactory.withUri(
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
            Edge edge = edgeFactory.withUri(
                    URI.create(uri)
            );
            edges.add(
                    edge
            );
        }
        return edges;
    }

}
