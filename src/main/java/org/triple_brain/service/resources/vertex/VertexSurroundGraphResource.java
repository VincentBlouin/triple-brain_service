package org.triple_brain.service.resources.vertex;

/*
* Copyright Mozilla Public License 1.1
*/

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import org.triple_brain.module.model.graph.GraphFactory;
import org.triple_brain.module.model.graph.GraphTransactional;
import org.triple_brain.module.model.graph.SubGraph;
import org.triple_brain.module.model.graph.UserGraph;
import org.triple_brain.module.model.graph.edge.Edge;
import org.triple_brain.module.model.graph.vertex.Vertex;
import org.triple_brain.module.model.graph.vertex.VertexInSubGraphOperator;
import org.triple_brain.module.model.graph.vertex.VertexOperator;
import org.triple_brain.module.model.json.graph.GraphJsonFields;

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
                GraphJsonFields.toJson(
                        getGraph()
                )
        ).build();
    }

    private SubGraph getGraph(){
        SubGraph graph = userGraph.graphWithDepthAndCenterVertexId(
                depthOfSubVertices,
                centerVertex.uri()
        );
//        removeVerticesNotAllowedToAccess(
//                graph
//        );
        return graph;
    }

    public void removeVerticesAndEdgesNotAllowedToAccess(SubGraph graph) {
        Iterator<VertexInSubGraphOperator> iterator = graph.vertices().iterator();
        while (iterator.hasNext()) {
            VertexOperator vertex = iterator.next();
            if (!canAccessVertex(vertex)) {
                for (Edge edge : vertex.connectedEdges()) {
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
