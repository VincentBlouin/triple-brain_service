package org.triple_brain.service.resources.vertex;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import org.codehaus.jettison.json.JSONArray;
import org.triple_brain.module.model.graph.GraphTransactional;
import org.triple_brain.module.model.graph.UserGraph;
import org.triple_brain.module.model.graph.Vertex;
import org.triple_brain.module.model.graph.VertexFactory;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

/*
* Copyright Mozilla Public License 1.1
*/
public class VertexGroupResource {

    @Inject
    VertexFactory vertexFactory;

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
    public Response createGroup(JSONArray includedVerticesUri, @Context UriInfo info){
        Vertex newVertex = vertexFactory.createFromVertices(
                verticesFromUris(
                        includedVerticesUri
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

    private Set<Vertex> verticesFromUris(JSONArray uris){
        Set<Vertex> vertices = new HashSet<>();
        for(int i = 0 ; i < uris.length(); i++){
            Vertex vertex = vertexFactory.createOrLoadUsingUri(
                    URI.create(uris.optString(i))
            );
            vertices.add(
                    vertex
            );
        }
        return vertices;
    }

}
