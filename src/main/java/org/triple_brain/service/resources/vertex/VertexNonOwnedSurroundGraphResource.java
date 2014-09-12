/*
 * Copyright Vincent Blouin under the Mozilla Public License 1.1
 */

package org.triple_brain.service.resources.vertex;

import org.triple_brain.module.model.graph.GraphTransactional;
import org.triple_brain.module.model.graph.SubGraph;
import org.triple_brain.module.model.graph.SubGraphPojo;
import org.triple_brain.module.model.graph.UserGraph;
import org.triple_brain.module.model.graph.edge.Edge;
import org.triple_brain.module.model.graph.vertex.Vertex;
import org.triple_brain.module.model.json.graph.SubGraphJson;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Iterator;

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
        removeGraphElementsNotAllowedToAccess(
                graph
        );
        return graph;
    }

    private void removeGraphElementsNotAllowedToAccess(SubGraph graph) {
        if (skipVerification) {
            return;
        }
        if(graph.edges().isEmpty()){
            removeVertexFromGraphIfPrivate(
                    centerVertex,
                    graph
            );
            return;
        }
        Iterator<? extends Edge> iterator = graph.edges().values().iterator();
        while (iterator.hasNext()) {
            Edge edge = iterator.next();
            removeVertexFromEdgesAndGraphIfPrivate(
                    edge.sourceVertex(),
                    iterator,
                    graph
            );
            removeVertexFromEdgesAndGraphIfPrivate(
                    edge.destinationVertex(),
                    iterator,
                    graph
            );
        }
    }

    private void removeVertexFromEdgesAndGraphIfPrivate(Vertex vertex, Iterator<? extends Edge> iterator, SubGraph graph){
        if(graph.containsVertex(vertex)){
            if(removeVertexFromGraphIfPrivate(vertex, graph)){
                iterator.remove();
            }
        }else{
            iterator.remove();
        }
    }

    private Boolean removeVertexFromGraphIfPrivate(Vertex vertex, SubGraph graph){
        Vertex vertexInVertices = graph.vertexWithIdentifier(
                vertex.uri()
        );
        if (!vertexInVertices.isPublic()) {
            throwExceptionIfCenterVertex(vertexInVertices);
            graph.vertices().remove(vertexInVertices.uri());
            return true;
        }
        return false;
    }

    private void throwExceptionIfCenterVertex(Vertex vertex){
        /* I would like to verify if the center vertex is accessible
               before getting the graph but I get an NotInTransaction Exception */
        if (vertex.equals(centerVertex)) {
            throw new WebApplicationException(
                    Response.Status.FORBIDDEN
            );
        }
    }
}
