/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.test;

import guru.bubl.module.model.graph.*;
import guru.bubl.service.resources.GraphManipulatorResourceUtils;
import org.codehaus.jettison.json.JSONArray;
import guru.bubl.module.common_utils.Uris;
import guru.bubl.module.model.graph.edge.EdgeOperator;
import guru.bubl.module.model.graph.edge.EdgePojo;
import guru.bubl.module.model.graph.vertex.Vertex;
import guru.bubl.module.model.graph.vertex.VertexInSubGraphPojo;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import guru.bubl.module.model.json.graph.EdgeJson;
import guru.bubl.module.model.json.graph.VertexInSubGraphJson;

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
        UserGraph userGraph = graphFactory.loadForUser(
                GraphManipulatorResourceUtils.userFromSession(request.getSession())
        );
        URI vertexUri = new URI(vertexId);
        SubGraphPojo subGraph = userGraph.graphWithDepthAndCenterVertexId(
                1,
                vertexUri
        );
        return Response.ok(VertexInSubGraphJson.toJson(
                subGraph.vertexWithIdentifier(
                        vertexUri
                )
        )).build();
    }

    @Path("{vertexId}/connected_edges")
    @GraphTransactional
    @GET
    public Response connectedEdges(@Context HttpServletRequest request, @PathParam("vertexId") String vertexId)throws Exception{
        UserGraph userGraph = graphFactory.loadForUser(GraphManipulatorResourceUtils.userFromSession(request.getSession()));
        VertexOperator vertex = userGraph.vertexWithUri(new URI(vertexId));
        JSONArray edges = new JSONArray();
        for(EdgeOperator edge : vertex.connectedEdges()){
            edges.put(
                    EdgeJson.toJson(
                            new EdgePojo(edge)
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
        UserGraph userGraph = graphFactory.loadForUser(GraphManipulatorResourceUtils.userFromSession(request.getSession()));
        VertexOperator vertex = userGraph.vertexWithUri(new URI(Uris.decodeUrlSafe(vertexId)));
        Vertex otherVertex = userGraph.vertexWithUri(new URI(Uris.decodeUrlSafe(otherVertexId)));
        return Response.ok(
                vertex.hasDestinationVertex(otherVertex).toString()
        ).build();
    }
}
