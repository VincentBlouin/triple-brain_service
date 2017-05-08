/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.vertex;

import guru.bubl.module.model.FriendlyResource;
import guru.bubl.module.model.graph.GraphTransactional;
import guru.bubl.module.model.graph.subgraph.SubGraph;
import guru.bubl.module.model.graph.subgraph.SubGraphPojo;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.graph.edge.Edge;
import guru.bubl.module.model.graph.vertex.Vertex;
import guru.bubl.module.model.json.graph.SubGraphJson;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Iterator;

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
        if(null == depth){
            depth = 1;
        }
        return Response.ok(
                SubGraphJson.toJson(
                        getGraphAtDepth(depth)
                )
        ).build();
    }



    private SubGraphPojo getGraph() {
        return getGraphAtDepth(1);
    }

    private SubGraphPojo getGraphAtDepth(Integer depth) {
        SubGraphPojo graph = userGraph.graphWithDepthAndCenterBubbleUri(
                depth,
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
