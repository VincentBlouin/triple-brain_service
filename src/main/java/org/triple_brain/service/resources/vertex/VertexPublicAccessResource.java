package org.triple_brain.service.resources.vertex;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import org.triple_brain.module.model.graph.Vertex;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/*
* Copyright Mozilla Public License 1.1
*/
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VertexPublicAccessResource {

    private Vertex vertex;

    @AssistedInject
    public VertexPublicAccessResource(
            @Assisted Vertex vertex
    ){
        this.vertex = vertex;
    }

    @POST
    @Path("/")
    public Response makePublic(){
        vertex.makePublic();
        return Response.ok().build();
    }

    @DELETE
    @Path("/")
    public Response makePrivate(){
        vertex.makePrivate();
        return Response.ok().build();
    }
}
