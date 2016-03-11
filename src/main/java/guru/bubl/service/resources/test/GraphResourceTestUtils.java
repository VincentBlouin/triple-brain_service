/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.test;

import guru.bubl.module.model.WholeGraph;
import guru.bubl.module.model.graph.GraphFactory;
import guru.bubl.module.model.graph.GraphTransactional;
import guru.bubl.module.model.graph.UserGraph;
import guru.bubl.module.model.graph.vertex.VertexInSubGraphOperator;
import guru.bubl.service.resources.GraphManipulatorResourceUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

@Path("/test/graph")
@Singleton
public class GraphResourceTestUtils {

    @Inject
    GraphFactory graphFactory;

    @Inject
    WholeGraph wholeGraph;

    @Path("graph_element/{graphElementId}/exists")
    @GraphTransactional
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response destinationVertices(@Context HttpServletRequest request, @PathParam("graphElementId") String graphElementId)throws Exception{
        UserGraph userGraph = graphFactory.loadForUser(GraphManipulatorResourceUtils.userFromSession(request.getSession()));
        return Response.ok(
                userGraph.haveElementWithId(
                        URI.create(graphElementId)
                ).toString()
        ).build();
    }


    @Path("set_all_number_of_connected_edges_to_zero")
    @GraphTransactional
    @Produces(MediaType.TEXT_PLAIN)
    public Response setAlNumberOfConnectedEdgesToZero()throws Exception{
        for(VertexInSubGraphOperator vertex: wholeGraph.getAllVertices()){
            vertex.setNumberOfConnectedEdges(0);
        }
        return Response.ok().build();
    }

    @Path("server")
    @GraphTransactional
    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    public Response shutDown()throws Exception{
//        graphDb.shutdown();
        return Response.ok().build();
    }

}
