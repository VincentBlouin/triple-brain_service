package org.triple_brain.service.resources.vertex;

import org.triple_brain.module.model.graph.GraphTransactional;
import org.triple_brain.module.model.graph.SubGraph;
import org.triple_brain.module.model.graph.SubGraphPojo;
import org.triple_brain.module.model.graph.UserGraph;
import org.triple_brain.module.model.graph.vertex.Vertex;
import org.triple_brain.module.model.graph.vertex.VertexInSubGraph;
import org.triple_brain.module.model.json.graph.SubGraphJson;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Iterator;

/*
* Copyright Mozilla Public License 1.1
*/
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VertexNonOwnedSurroundGraphResource {
    private Vertex centerVertex;
    private UserGraph userGraph;
    private Boolean skipVerification;

    public VertexNonOwnedSurroundGraphResource(
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
    public Response get() {
        return Response.ok(
                SubGraphJson.toJson(
                        getGraph()
                )
        ).build();
    }

    private SubGraphPojo getGraph() {
        SubGraphPojo graph = userGraph.graphWithDepthAndCenterVertexId(
                1,
                centerVertex.uri()
        );
        removeVerticesNotAllowedToAccess(
                graph
        );
        return graph;
    }


    public void removeVerticesNotAllowedToAccess(SubGraph graph) {
        if(skipVerification){
            return;
        }
        Iterator<? extends VertexInSubGraph> iterator = graph.vertices().values().iterator();
        while (iterator.hasNext()) {
            Vertex vertex = iterator.next();
            if (!vertex.isPublic()) {
               /* I would like to verify if the center vertex is accessible
               before getting the graph but I get an NotInTransaction Exception */
                if (vertex.equals(centerVertex)) {
                    throw new WebApplicationException(
                            Response.Status.FORBIDDEN
                    );
                }
                iterator.remove();
            }
        }
    }
}
