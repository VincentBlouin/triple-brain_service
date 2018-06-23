/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.vertex;

import guru.bubl.module.model.graph.GraphTransactional;
import guru.bubl.module.model.graph.ShareLevel;
import guru.bubl.module.model.graph.subgraph.SubGraph;
import guru.bubl.module.model.graph.subgraph.SubGraphPojo;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.graph.edge.Edge;
import guru.bubl.module.model.graph.vertex.Vertex;
import guru.bubl.module.model.graph.SubGraphJson;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NotOwnedSurroundGraphResource {
    private Vertex centerVertex;
    private UserGraph userGraph;
    private Boolean skipVerification;

    public NotOwnedSurroundGraphResource(
            UserGraph userGraph,
            Vertex centerVertex,
            Boolean skipVerification
    ) {
        this.userGraph = userGraph;
        this.centerVertex = centerVertex;
        this.skipVerification = skipVerification;
    }

    @GET
    @Path("/")
    @GraphTransactional
    public Response get(@QueryParam("depth") Integer depth) {
        Set<ShareLevel> inShareLevels = new HashSet<>();
        inShareLevels.add(ShareLevel.PUBLIC);
        inShareLevels.add(ShareLevel.PUBLIC_WITH_LINK);
        if (skipVerification) {
            inShareLevels.add(ShareLevel.PRIVATE);
            inShareLevels.add(ShareLevel.FRIENDS);
        }
        if (depth == null) {
            depth = 1;
        }
        SubGraphPojo subGraph = userGraph.aroundVertexUriInShareLevelsWithDepth(
                centerVertex.uri(),
                inShareLevels,
                depth
        );
        if (subGraph.vertices().isEmpty()) {
            return Response.status(
                    Response.Status.FORBIDDEN
            ).build();
        }
        return Response.ok(
                SubGraphJson.toJson(
                        subGraph
                )
        ).build();
    }


}
