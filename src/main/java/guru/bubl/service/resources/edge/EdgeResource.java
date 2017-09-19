/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.edge;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import guru.bubl.module.model.UserUris;
import guru.bubl.module.model.center_graph_element.CenterGraphElementOperator;
import guru.bubl.module.model.center_graph_element.CenterGraphElementOperatorFactory;
import guru.bubl.module.model.graph.GraphElementType;
import guru.bubl.module.model.graph.GraphTransactional;
import guru.bubl.module.model.graph.edge.Edge;
import guru.bubl.module.model.graph.edge.EdgeOperator;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.graph.vertex.Vertex;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import guru.bubl.module.model.graph.vertex.VertexPojo;
import guru.bubl.module.model.json.LocalizedStringJson;
import guru.bubl.service.resources.GraphElementIdentificationResource;
import guru.bubl.service.resources.vertex.GraphElementIdentificationResourceFactory;
import guru.bubl.service.resources.vertex.OwnedSurroundGraphResource;
import org.codehaus.jettison.json.JSONObject;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.net.URI;

import static guru.bubl.module.common_utils.Uris.decodeUrlSafe;


@Produces(MediaType.APPLICATION_JSON)
public class EdgeResource {

    @Inject
    GraphElementIdentificationResourceFactory graphElementIdentificationResourceFactory;

    @Inject
    CenterGraphElementOperatorFactory centerGraphElementOperatorFactory;

    private UserGraph userGraph;

    @AssistedInject
    public EdgeResource(
            @Assisted UserGraph userGraph
    ) {
        this.userGraph = userGraph;
    }

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/")
    @GraphTransactional
    public Response addRelation(
            @QueryParam("sourceVertexId") String sourceVertexId,
            @QueryParam("destinationVertexId") String destinationVertexId
    ) {
        try {
            sourceVertexId = decodeUrlSafe(sourceVertexId);
            destinationVertexId = decodeUrlSafe(destinationVertexId);
        } catch (UnsupportedEncodingException e) {
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
            @PathParam("edgeShortId") String edgeShortId
    ) {
        EdgeOperator edge = edgeFromShortId(edgeShortId);
        edge.remove();
        return Response.ok().build();
    }

    @POST
    @Path("{edgeShortId}/label")
    @Produces(MediaType.TEXT_PLAIN)
    @GraphTransactional
    public Response modifyEdgeLabel(
            @PathParam("edgeShortId") String edgeShortId,
            JSONObject localizedLabel) {
        URI edgeId = edgeUriFromShortId(edgeShortId);
        EdgeOperator edge = userGraph.edgeWithUri(
                edgeId
        );
        edge.label(
                localizedLabel.optString(
                        LocalizedStringJson.content.name()
                )
        );
        return Response.ok().build();
    }

    @PUT
    @Path("{shortId}/inverse")
    @GraphTransactional
    @Produces(MediaType.TEXT_PLAIN)
    public Response inverse(@PathParam("shortId") String shortId) {
        edgeFromShortId(shortId).inverse();
        return Response.ok().build();
    }

    @Path("{shortId}/identification")
    @GraphTransactional
    public GraphElementIdentificationResource getVertexIdentificationResource(@PathParam("shortId") String shortId) {
        return graphElementIdentificationResourceFactory.forGraphElement(
                edgeFromShortId(shortId),
                GraphElementType.edge
        );
    }

    @POST
    @GraphTransactional
    @Path("{shortId}/comment")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response updateComment(
            @PathParam("shortId") String shortId,
            String comment
    ) {
        EdgeOperator edgeOperator = edgeFromShortId(shortId);
        edgeOperator.comment(comment);
        return Response.noContent().build();
    }

    @PUT
    @GraphTransactional
    @Path("{shortId}/source-vertex/{sourceVertexShortId}")
    public Response changeSourceVertex(
            @PathParam("shortId") String shortId,
            @PathParam("sourceVertexShortId") String sourceVertexShortId
    ) {
        Vertex newSourceVertex = new VertexPojo(
                new UserUris(
                        userGraph.user()
                ).vertexUriFromShortId(
                        sourceVertexShortId
                )
        );
        edgeFromShortId(shortId).changeSourceVertex(
                newSourceVertex
        );
        return Response.noContent().build();
    }

    @PUT
    @GraphTransactional
    @Path("{shortId}/destination-vertex/{destinationVertexShortId}")
    public Response changeDestinationVertex(
            @PathParam("shortId") String shortId,
            @PathParam("destinationVertexShortId") String destinationVertexShortId
    ) {
        Vertex newSourceVertex = new VertexPojo(
                new UserUris(
                        userGraph.user()
                ).vertexUriFromShortId(
                        destinationVertexShortId
                )
        );
        edgeFromShortId(shortId).changeDestinationVertex(
                newSourceVertex
        );
        return Response.noContent().build();
    }



    @GraphTransactional
    @Path("{shortId}/surround_graph")
    public OwnedSurroundGraphResource getSurroundGraph(
            @PathParam("shortId") String shortId
    ) {
        Edge edge = edgeFromShortId(shortId);
        CenterGraphElementOperator centerGraphElementOperator = centerGraphElementOperatorFactory.usingFriendlyResource(
                edge
        );
        centerGraphElementOperator.incrementNumberOfVisits();
        centerGraphElementOperator.updateLastCenterDate();
        return new OwnedSurroundGraphResource(
                userGraph,
                edge.sourceVertex()
        );
    }

    private EdgeOperator edgeFromShortId(String shortId) {
        return userGraph.edgeWithUri(
                edgeUriFromShortId(
                        shortId
                )
        );
    }

    private URI edgeUriFromShortId(String shortId) {
        return new UserUris(
                userGraph.user()
        ).edgeUriFromShortId(
                shortId
        );
    }
}
