/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.edge;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import guru.bubl.module.model.UserUris;
import guru.bubl.module.model.center_graph_element.CenterGraphElementOperator;
import guru.bubl.module.model.center_graph_element.CenterGraphElementOperatorFactory;
import guru.bubl.module.model.graph.ShareLevel;
import guru.bubl.module.model.graph.edge.Edge;
import guru.bubl.module.model.graph.edge.EdgeFactory;
import guru.bubl.module.model.graph.edge.EdgeOperator;
import guru.bubl.module.model.graph.group_relation.GroupRelationPojo;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.graph.vertex.Vertex;
import guru.bubl.module.model.graph.vertex.VertexFactory;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import guru.bubl.module.model.graph.vertex.VertexPojo;
import guru.bubl.module.model.json.JsonUtils;
import guru.bubl.module.model.json.LocalizedStringJson;
import guru.bubl.module.model.tag.TagJson;
import guru.bubl.service.resources.GraphElementTagResource;
import guru.bubl.service.resources.vertex.GraphElementTagResourceFactory;
import guru.bubl.service.resources.vertex.OwnedSurroundGraphResource;
import org.codehaus.jettison.json.JSONObject;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.UUID;

import static guru.bubl.module.common_utils.Uris.decodeUrlSafe;


@Produces(MediaType.APPLICATION_JSON)
public class EdgeResource {

    @Inject
    private GraphElementTagResourceFactory graphElementTagResourceFactory;

    @Inject
    private CenterGraphElementOperatorFactory centerGraphElementOperatorFactory;

    @Inject
    private VertexFactory vertexFactory;

    @Inject
    private EdgeFactory edgeFactory;

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
        VertexOperator sourceVertex = vertexFactory.withUri(URI.create(
                sourceVertexId
        ));
        VertexOperator destinationVertex = vertexFactory.withUri(URI.create(
                destinationVertexId
        ));
        Edge createdEdge = sourceVertex.addRelationToVertex(destinationVertex);
        if (createdEdge == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        return Response.created(URI.create(
                UserUris.graphElementShortId(createdEdge.uri())
        )).build();
    }

    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/{edgeShortId}")
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
    public Response modifyEdgeLabel(
            @PathParam("edgeShortId") String edgeShortId,
            JSONObject localizedLabel) {
        URI edgeId = edgeUriFromShortId(edgeShortId);
        EdgeOperator edge = edgeFactory.withUri(
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
    @Produces(MediaType.TEXT_PLAIN)
    public Response inverse(@PathParam("shortId") String shortId) {
        edgeFromShortId(shortId).inverse();
        return Response.ok().build();
    }

    @Path("{shortId}/identification")
    public GraphElementTagResource getVertexIdentificationResource(@PathParam("shortId") String shortId) {
        return graphElementTagResourceFactory.forGraphElement(
                edgeFromShortId(shortId)
        );
    }

    @POST
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
    @Path("{shortId}/source-vertex/{sourceVertexShortId}")
    public Response changeSourceVertex(
            @PathParam("shortId") String shortId,
            @PathParam("sourceVertexShortId") String sourceVertexShortId,
            JSONObject shareLevels
    ) {
        Vertex newSourceVertex = new VertexPojo(
                new UserUris(
                        userGraph.user()
                ).vertexUriFromShortId(
                        sourceVertexShortId
                )
        );
        edgeFromShortId(shortId).changeSource(
                newSourceVertex.uri(),
                ShareLevel.valueOf(
                        shareLevels.optString("oldEndShareLevel", ShareLevel.PRIVATE.name()).toUpperCase()
                ),
                ShareLevel.valueOf(
                        shareLevels.optString("keptEndShareLevel", ShareLevel.PRIVATE.name()).toUpperCase()
                ),
                ShareLevel.valueOf(
                        shareLevels.optString("newEndShareLevel", ShareLevel.PRIVATE.name()).toUpperCase()
                )
        );
        return Response.noContent().build();
    }

    @PUT
    @Path("{shortId}/destination-vertex/{destinationVertexShortId}")
    public Response changeDestinationVertex(
            @PathParam("shortId") String shortId,
            @PathParam("destinationVertexShortId") String destinationVertexShortId,
            JSONObject shareLevels
    ) {
        Vertex newSourceVertex = new VertexPojo(
                new UserUris(
                        userGraph.user()
                ).vertexUriFromShortId(
                        destinationVertexShortId
                )
        );
        edgeFromShortId(shortId).changeDestination(
                newSourceVertex.uri(),
                ShareLevel.valueOf(
                        shareLevels.optString("oldEndShareLevel", ShareLevel.PRIVATE.name()).toUpperCase()
                ),
                ShareLevel.valueOf(
                        shareLevels.optString("keptEndShareLevel", ShareLevel.PRIVATE.name()).toUpperCase()
                ),
                ShareLevel.valueOf(
                        shareLevels.optString("newEndShareLevel", ShareLevel.PRIVATE.name()).toUpperCase()
                )
        );
        return Response.noContent().build();
    }

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
                edge.sourceFork()
        );
    }


    @POST
    @Path("{shortId}/convertToGroupRelation")
    public Response convertToGroupRelation(
            @PathParam("shortId") String shortId,
            JSONObject params
    ) {
        EdgeOperator edge = edgeFromShortId(shortId);
        GroupRelationPojo newGroupRelation = edge.convertToGroupRelation(
                params.optString("newGroupRelationShortId", UUID.randomUUID().toString()),
                TagJson.singleFromJson(params.optJSONObject("tag").toString()),
                params.optBoolean("isNewTag"),
                ShareLevel.valueOf(params.optString("initialShareLevel", ShareLevel.PRIVATE.name()))
        );
        return Response.ok(
                JsonUtils.getGson().toJson(
                        newGroupRelation
                )
        ).build();
    }

//    @POST
//    @Path("{shortId}/convertToGroupRelation")
//    public Response convertToGroupRelation(@PathParam("shortId") String shortId, JSONObject tagJson) {
//        Edge edge = edgeFromShortId(shortId);
//        TagPojo tag = TagJson.singleFromJson(
//                tagJson.toString()
//        );
//        Map<URI, TagPojo> tags = graphElement.addTag(
//                tag
//        );
//        return Response.ok().entity(
//                TagJson.toJson(tags)
//        ).build();
//        return Response
//    }

    private EdgeOperator edgeFromShortId(String shortId) {
        return edgeFactory.withUri(
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
