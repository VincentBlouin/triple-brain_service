package org.triple_brain.service.resources;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import org.triple_brain.module.common_utils.Uris;
import org.triple_brain.module.model.UserUris;
import org.triple_brain.module.model.graph.GraphElementType;
import org.triple_brain.module.model.graph.GraphFactory;
import org.triple_brain.module.model.graph.GraphTransactional;
import org.triple_brain.module.model.graph.UserGraph;
import org.triple_brain.module.model.graph.edge.Edge;
import org.triple_brain.module.model.graph.edge.EdgeOperator;
import org.triple_brain.module.model.graph.vertex.VertexOperator;
import org.triple_brain.module.search.GraphIndexer;
import org.triple_brain.service.resources.vertex.GraphElementIdentificationResourceFactory;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.net.URI;

import static org.triple_brain.module.common_utils.Uris.decodeUrlSafe;


/**
 * Copyright Mozilla Public License 1.1
 */
@Produces(MediaType.APPLICATION_JSON)
public class EdgeResource {

    @Inject
    GraphFactory graphFactory;

    @Inject
    GraphIndexer graphIndexer;

    @Inject
    GraphElementIdentificationResourceFactory graphElementIdentificationResourceFactory;

    private UserGraph userGraph;

    @AssistedInject
    public EdgeResource(
        @Assisted UserGraph userGraph
    ){
        this.userGraph = userGraph;
    }

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/")
    @GraphTransactional
    public Response addRelation(
            @QueryParam("sourceVertexId") String sourceVertexId,
            @QueryParam("destinationVertexId") String destinationVertexId
        ){
        try{
            sourceVertexId = decodeUrlSafe(sourceVertexId);
            destinationVertexId = decodeUrlSafe(destinationVertexId);
        }catch (UnsupportedEncodingException e){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        VertexOperator sourceVertex = userGraph.vertexWithUri(URI.create(
                sourceVertexId
        ));
        VertexOperator destinationVertex = userGraph.vertexWithUri(URI.create(
                destinationVertexId
        ));
        Edge createdEdge = sourceVertex.addRelationToVertex(destinationVertex);
        return Response.created(URI.create(
                UserUris.graphElementShortId(createdEdge.uri())
        )).build();
    }

    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/{edgeShortId}")
    @GraphTransactional
    public Response removeRelation(
       @Context HttpServletRequest request
    ){
        EdgeOperator edge = userGraph.edgeWithUri(Uris.get(
                request.getRequestURI()
        ));
        VertexOperator sourceVertex = edge.sourceVertex();
        VertexOperator destinationVertex = edge.destinationVertex();
        graphIndexer.deleteGraphElement(
                edge
        );
        edge.remove();
        graphIndexer.indexVertex(
                sourceVertex
        );
        graphIndexer.indexVertex(
                destinationVertex
        );
        graphIndexer.commit();
        return Response.ok().build();
    }

    @POST
    @Path("{edgeShortId}/label")
    @Produces(MediaType.TEXT_PLAIN)
    @GraphTransactional
    public Response modifyEdgeLabel(
            @PathParam("edgeShortId") String edgeShortId,
            @QueryParam("label") String label){
        URI edgeId = edgeUriFromShortId(edgeShortId);
        EdgeOperator edge = userGraph.edgeWithUri(
            edgeId
        );
        edge.label(label);
        graphIndexer.handleEdgeLabelUpdated(edge);
        graphIndexer.commit();
        return Response.ok().build();
    }

    @PUT
    @Path("{shortId}/inverse")
    @GraphTransactional
    @Produces(MediaType.TEXT_PLAIN)
    public Response inverse(@PathParam("shortId") String shortId){
        edgeFromShortId(shortId).inverse();
        return Response.ok().build();
    }

    @Path("{shortId}/identification")
    @GraphTransactional
    public GraphElementIdentificationResource getVertexIdentificationResource(@PathParam("shortId") String shortId){
        return graphElementIdentificationResourceFactory.forGraphElement(
                edgeFromShortId(shortId),
                GraphElementType.EDGE
        );
    }

    private EdgeOperator edgeFromShortId(String shortId){
        return userGraph.edgeWithUri(
                edgeUriFromShortId(
                        shortId
                )
        );
    }

    private URI edgeUriFromShortId(String shortId){
        return  new UserUris(
                userGraph.user()
        ).edgeUriFromShortId(
                shortId
        );
    }
}
