/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.test;

import guru.bubl.module.model.graph.GraphFactory;
import guru.bubl.module.model.graph.edge.EdgeFactory;
import guru.bubl.module.model.graph.edge.EdgeJson;
import guru.bubl.module.model.graph.edge.EdgeOperator;
import guru.bubl.module.model.graph.edge.EdgePojo;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.service.SessionHandler;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.net.URI;

@Path("/test/edge/")
@Singleton
public class EdgeResourceTestUtils {

    @Inject
    private GraphFactory graphFactory;

    @Inject
    private SessionHandler sessionHandler;

    @Inject
    private EdgeFactory edgeFactory;

    @Path("{edgeId}")
    @GET
    public Response vertexWithId(@Context HttpServletRequest request, @PathParam("edgeId") String edgeId) throws Exception {
        UserGraph userGraph = graphFactory.loadForUser(sessionHandler.userFromSession(request.getSession()));
        EdgeOperator edge = edgeFactory.withUri(new URI(edgeId));
        return Response.ok(EdgeJson.toJson(
                new EdgePojo(edge)
        )).build();
    }

}
