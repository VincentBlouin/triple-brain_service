/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.test;

import guru.bubl.module.model.graph.GraphFactory;
import guru.bubl.module.model.graph.GraphTransactional;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.graph.edge.EdgeOperator;
import guru.bubl.module.model.graph.edge.EdgePojo;
import guru.bubl.module.model.json.graph.EdgeJson;
import guru.bubl.service.resources.GraphManipulatorResourceUtils;

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
    GraphFactory graphFactory;

    @Path("{edgeId}")
    @GraphTransactional
    @GET
    public Response vertexWithId(@Context HttpServletRequest request, @PathParam("edgeId") String edgeId)throws Exception{
        UserGraph userGraph = graphFactory.loadForUser(GraphManipulatorResourceUtils.userFromSession(request.getSession()));
        EdgeOperator edge = userGraph.edgeWithUri(new URI(edgeId));
        return Response.ok(EdgeJson.toJson(
                new EdgePojo(edge)
        )).build();
    }

}
