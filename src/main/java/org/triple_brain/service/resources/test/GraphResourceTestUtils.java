package org.triple_brain.service.resources.test;

import org.neo4j.graphdb.GraphDatabaseService;
import org.triple_brain.module.model.graph.GraphFactory;
import org.triple_brain.module.model.graph.UserGraph;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.triple_brain.service.resources.GraphManipulatorResourceUtils.userFromSession;

/*
* Copyright Mozilla Public License 1.1
*/

@Path("/test/graph")
@Singleton
@PermitAll
public class GraphResourceTestUtils {
    @Inject
    GraphFactory graphFactory;

    @Inject
    GraphDatabaseService graphDb;

    @Path("graph_element/{graphElementId}/exists")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response destinationVertices(@Context HttpServletRequest request, @PathParam("graphElementId") String graphElementId)throws Exception{
        UserGraph userGraph = graphFactory.loadForUser(userFromSession(request.getSession()));
        return Response.ok(
                userGraph.haveElementWithId(graphElementId).toString()
        ).build();
    }


    @Path("server")
    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    public Response shutDown()throws Exception{
        graphDb.shutdown();
        return Response.ok().build();
    }




}
