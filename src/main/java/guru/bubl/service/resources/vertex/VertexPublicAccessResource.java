/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.vertex;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import guru.bubl.module.model.graph.GraphTransactional;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import guru.bubl.module.search.GraphIndexer;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Produces(MediaType.TEXT_PLAIN)
@Consumes(MediaType.TEXT_PLAIN)
public class VertexPublicAccessResource {

    @Inject
    GraphIndexer graphIndexer;

    private VertexOperator vertex;

    @AssistedInject
    public VertexPublicAccessResource(
            @Assisted VertexOperator vertex
    ){
        this.vertex = vertex;
    }

    @POST
    @Path("/")
    @GraphTransactional
    public Response makePublic(){
        vertex.makePublic();
        graphIndexer.indexVertex(
                vertex
        );
        graphIndexer.commit();
        return Response.ok().build();
    }

    @DELETE
    @Path("/")
    @GraphTransactional
    public Response makePrivate(){
        vertex.makePrivate();
        graphIndexer.indexVertex(
                vertex
        );
        graphIndexer.commit();
        return Response.ok().build();
    }
}
