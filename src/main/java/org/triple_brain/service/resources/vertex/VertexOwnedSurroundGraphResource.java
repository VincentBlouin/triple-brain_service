package org.triple_brain.service.resources.vertex;

/*
* Copyright Mozilla Public License 1.1
*/

import org.triple_brain.module.model.graph.GraphTransactional;
import org.triple_brain.module.model.graph.SubGraphPojo;
import org.triple_brain.module.model.graph.UserGraph;
import org.triple_brain.module.model.graph.vertex.Vertex;
import org.triple_brain.module.model.json.graph.SubGraphJson;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VertexOwnedSurroundGraphResource {

    private Vertex centerVertex;
    private Integer depthOfSubVertices;
    private UserGraph userGraph;

    public VertexOwnedSurroundGraphResource(
            UserGraph userGraph,
            Vertex centerVertex,
            Integer depth
    ) {
        this.userGraph = userGraph;
        this.centerVertex = centerVertex;
        this.depthOfSubVertices = depth;
    }

    @GET
    @Path("/")
    @GraphTransactional
    public Response get() {
        return Response.ok(
                SubGraphJson.toJson(
                        getGraph()
                )
        ).build();
    }

    private SubGraphPojo getGraph(){
        return userGraph.graphWithDepthAndCenterVertexId(
                depthOfSubVertices,
                centerVertex.uri()
        );
    }
}
