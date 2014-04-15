package org.triple_brain.service.resources.vertex;

/*
* Copyright Mozilla Public License 1.1
*/

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import org.triple_brain.module.model.graph.*;
import org.triple_brain.module.model.graph.edge.Edge;
import org.triple_brain.module.model.graph.vertex.Vertex;
import org.triple_brain.module.model.graph.vertex.VertexFactory;
import org.triple_brain.module.model.graph.vertex.VertexInSubGraph;
import org.triple_brain.module.model.graph.vertex.VertexOperator;
import org.triple_brain.module.model.json.graph.SubGraphJson;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Iterator;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VertexSurroundGraphResource {
    @Inject
    GraphFactory graphFactory;

    @Inject
    VertexFactory vertexFactory;

    private Vertex centerVertex;
    private Integer depthOfSubVertices;
    private UserGraph userGraph;

    @AssistedInject
    public VertexSurroundGraphResource(
            @Assisted UserGraph userGraph,
            @Assisted Vertex centerVertex,
            @Assisted Integer depth
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
        SubGraphPojo graph = userGraph.graphWithDepthAndCenterVertexId(
                depthOfSubVertices,
                centerVertex.uri()
        );
//        removeVerticesNotAllowedToAccess(
//                graph
//        );
        return graph;
    }

    public void removeVerticesAndEdgesNotAllowedToAccess(SubGraph graph) {
        Iterator<? extends VertexInSubGraph> iterator = graph.vertices().values().iterator();
        while (iterator.hasNext()) {
            Vertex vertex = iterator.next();
            if (!canAccessVertex(vertex)) {
                VertexOperator vertexOperator = vertexFactory.withUri(
                        vertex.uri()
                );
                for (Edge edge : vertexOperator.connectedEdges()) {
                    graph.edges().remove(
                            edge
                    );
                }
                iterator.remove();
            }
        }
    }

    private boolean canAccessVertex(Vertex vertex) {
        return centerVertex.ownerUsername().equals(vertex.ownerUsername()) ||
                vertex.isPublic();
    }
}
