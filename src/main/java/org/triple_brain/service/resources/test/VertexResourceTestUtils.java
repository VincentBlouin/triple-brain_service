package org.triple_brain.service.resources.test;

import org.codehaus.jettison.json.JSONArray;
import org.triple_brain.module.common_utils.Uris;
import org.triple_brain.module.model.graph.*;
import org.triple_brain.module.model.json.graph.EdgeJson;
import org.triple_brain.module.model.json.graph.VertexJson;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

import static org.triple_brain.service.resources.GraphManipulatorResourceUtils.userFromSession;

/*
* Copyright Mozilla Public License 1.1
*/
@Path("/test/vertex/")
@Singleton
@Produces(MediaType.APPLICATION_JSON)
public class VertexResourceTestUtils {
    @Inject
    GraphFactory graphFactory;

    @Path("{vertexId}")
    @GraphTransactional
    @GET
    public Response vertexWithId(@Context HttpServletRequest request, @PathParam("vertexId") String vertexId)throws Exception{
        UserGraph userGraph = graphFactory.loadForUser(userFromSession(request.getSession()));
        Vertex vertex = userGraph.vertexWithUri(new URI(vertexId));
        return Response.ok(VertexJson.toJson(vertex)).build();
    }

    @Path("{vertexId}/connected_edges")
    @GraphTransactional
    @GET
    public Response connectedEdges(@Context HttpServletRequest request, @PathParam("vertexId") String vertexId)throws Exception{
        UserGraph userGraph = graphFactory.loadForUser(userFromSession(request.getSession()));
        Vertex vertex = userGraph.vertexWithUri(new URI(vertexId));
        JSONArray edges = new JSONArray();
        for(Edge edge : vertex.connectedEdges()){
            edges.put(
                    EdgeJson.toJson(
                            edge
                    )
            );
        }
        return Response.ok(edges).build();
    }

    @Path("{vertexId}/has_destination/{otherVertexId}")
    @Produces(MediaType.TEXT_PLAIN)
    @GraphTransactional
    @GET
    public Response destinationVertices(@Context HttpServletRequest request, @PathParam("vertexId") String vertexId, @PathParam("otherVertexId") String otherVertexId)throws Exception{
        UserGraph userGraph = graphFactory.loadForUser(userFromSession(request.getSession()));
        Vertex vertex = userGraph.vertexWithUri(new URI(Uris.decodeURL(vertexId)));
        Vertex otherVertex = userGraph.vertexWithUri(new URI(Uris.decodeURL(otherVertexId)));
        return Response.ok(
                vertex.hasDestinationVertex(otherVertex).toString()
        ).build();
    }
}
