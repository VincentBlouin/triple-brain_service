package org.triple_brain.service.resources.test;

import org.triple_brain.module.model.graph.GraphFactory;
import org.triple_brain.module.model.graph.GraphTransactional;
import org.triple_brain.module.model.graph.UserGraph;
import org.triple_brain.module.model.graph.edge.EdgeOperator;
import org.triple_brain.module.model.graph.edge.EdgePojo;
import org.triple_brain.module.model.json.graph.EdgeJson;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.net.URI;

import static org.triple_brain.service.resources.GraphManipulatorResourceUtils.userFromSession;

/*
* Copyright Mozilla Public License 1.1
*/
@Path("/test/edge/")
@Singleton
public class EdgeResourceTestUtils {

    @Inject
    GraphFactory graphFactory;

    @Path("{edgeId}")
    @GraphTransactional
    @GET
    public Response vertexWithId(@Context HttpServletRequest request, @PathParam("edgeId") String edgeId)throws Exception{
        UserGraph userGraph = graphFactory.loadForUser(userFromSession(request.getSession()));
        EdgeOperator edge = userGraph.edgeWithUri(new URI(edgeId));
        return Response.ok(EdgeJson.toJson(
                new EdgePojo(edge)
        )).build();
    }

}
