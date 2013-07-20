package org.triple_brain.service.resources.vertex;

/*
* Copyright Mozilla Public License 1.1
*/

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import org.triple_brain.module.model.graph.*;
import org.triple_brain.module.model.json.graph.GraphJSONFields;
import org.triple_brain.service.resources.DrawnGraphResource;
import org.triple_brain.service.resources.DrawnGraphResourceFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Iterator;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VertexSurroundGraphResource {
    @Inject
    GraphFactory graphFactory;

    @Inject
    DrawnGraphResourceFactory drawnGraphResourceFactory;

    private Vertex centerVertex;
    private Integer depthOfSubVertices;

    @AssistedInject
    public VertexSurroundGraphResource(
            @Assisted Vertex centerVertex,
            @Assisted Integer depth
    ) {
        this.centerVertex = centerVertex;
        this.depthOfSubVertices = depth;
    }

    @GET
    @Path("/")
    public Response get() {
        return Response.ok(
                GraphJSONFields.toJson(
                        getGraph()
                )
        ).build();
    }

    @Path("/drawn")
    public DrawnGraphResource getDrawnGraphResource(){
        return drawnGraphResourceFactory.ofGraph(
                getGraph()
        );
    }

    private SubGraph getGraph(){
        UserGraph userGraph = graphFactory.loadForUser(
                centerVertex.owner()
        );
        SubGraph graph = userGraph.graphWithDepthAndCenterVertexId(
                depthOfSubVertices,
                centerVertex.id()
        );
//        removeVerticesNotAllowedToAccess(
//                graph
//        );
        return graph;
    }

    public void removeVerticesAndEdgesNotAllowedToAccess(SubGraph graph) {
        Iterator<VertexInSubGraph> iterator = graph.vertices().iterator();
        while (iterator.hasNext()) {
            Vertex vertex = iterator.next();
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
        return vertex.owner().equals(vertex.owner()) ||
                vertex.isPublic();
    }
}
