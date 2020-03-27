/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.test;

import guru.bubl.module.model.WholeGraph;
import guru.bubl.module.model.graph.GraphFactory;
import guru.bubl.module.model.graph.GraphTransactional;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.graph.vertex.VertexInSubGraphOperator;
import guru.bubl.service.SessionHandler;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Session;

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

    @Inject
    SessionHandler sessionHandler;

    @Inject
    Driver driver;

    @Path("graph_element/{graphElementId}/exists")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response destinationVertices(@Context HttpServletRequest request, @PathParam("graphElementId") String graphElementId) throws Exception {
        UserGraph userGraph = graphFactory.loadForUser(sessionHandler.userFromSession(request.getSession()));
        return Response.ok(
                userGraph.haveElementWithId(
                        URI.create(graphElementId)
                ).toString()
        ).build();
    }


    @Path("set_all_number_of_connected_edges_to_zero")
    @Produces(MediaType.TEXT_PLAIN)
    public Response setAlNumberOfConnectedEdgesToZero() {
        try (Session session = driver.session()) {
            session.run("MATCH (n:GraphElement) SET n.nb_private_neighbors=0;");
        }
        return Response.ok().build();
    }

    @Path("server")
    @GraphTransactional
    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    public Response shutDown() throws Exception {
//        graphDb.shutdown();
        return Response.ok().build();
    }

}
