/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.relation;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import guru.bubl.module.model.UserUris;
import guru.bubl.module.model.center_graph_element.CenterGraphElementOperatorFactory;
import guru.bubl.module.model.graph.GraphElementOperator;
import guru.bubl.module.model.graph.GraphElementType;
import guru.bubl.module.model.graph.ShareLevel;
import guru.bubl.module.model.graph.edge.EdgeOperator;
import guru.bubl.module.model.graph.relation.Relation;
import guru.bubl.module.model.graph.relation.RelationFactory;
import guru.bubl.module.model.graph.relation.RelationOperator;
import guru.bubl.module.model.graph.group_relation.GroupRelationPojo;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.graph.vertex.VertexFactory;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import guru.bubl.module.model.json.JsonUtils;
import guru.bubl.module.model.json.LocalizedStringJson;
import guru.bubl.service.resources.GraphElementResource;
import guru.bubl.service.resources.GraphElementTagResource;
import guru.bubl.service.resources.edge.EdgeResource;
import guru.bubl.service.resources.vertex.GraphElementTagResourceFactory;
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
public class RelationResource implements EdgeResource {

    @Inject
    private GraphElementTagResourceFactory graphElementTagResourceFactory;

    @Inject
    private VertexFactory vertexFactory;

    @Inject
    private RelationFactory relationFactory;

    @Inject
    private CenterGraphElementOperatorFactory centerGraphElementOperatorFactory;

    private UserGraph userGraph;

    @AssistedInject
    public RelationResource(
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
        Relation createdRelation = sourceVertex.addRelationToVertex(destinationVertex);
        if (createdRelation == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        return Response.created(URI.create(
                UserUris.graphElementShortId(createdRelation.uri())
        )).build();
    }

    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/{edgeShortId}")
    public Response removeRelation(
            @PathParam("edgeShortId") String edgeShortId
    ) {
        RelationOperator edge = edgeFromShortId(edgeShortId);
        edge.remove();
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
    @Path("{shortId}/convertToGroupRelation")
    public Response convertToGroupRelation(
            @PathParam("shortId") String shortId,
            JSONObject params
    ) {
        RelationOperator edge = edgeFromShortId(shortId);
        GroupRelationPojo newGroupRelation = edge.convertToGroupRelation(
                params.optString("newGroupRelationShortId", UUID.randomUUID().toString()),
                ShareLevel.valueOf(params.optString("initialShareLevel", ShareLevel.PRIVATE.name())),
                params.optString("label", "")
        );
        return Response.ok(
                JsonUtils.getGson().toJson(
                        newGroupRelation
                )
        ).build();
    }

    private RelationOperator edgeFromShortId(String shortId) {
        return relationFactory.withUri(
                getUriFromShortId(
                        shortId
                )
        );
    }

    @Override
    public URI getUriFromShortId(String shortId) {
        return new UserUris(
                userGraph.user()
        ).edgeUriFromShortId(
                shortId
        );
    }

    @Override
    public GraphElementOperator getOperatorFromShortId(String shortId) {
        return vertexFactory.withUri(
                getUriFromShortId(shortId)
        );
    }

    @Override
    public UserGraph getUserGraph() {
        return userGraph;
    }

    @Override
    public CenterGraphElementOperatorFactory getCenterOperatorFactory() {
        return centerGraphElementOperatorFactory;
    }

    @Override
    public EdgeOperator getEdgeOperatorFromShortId(String shortId) {
        return relationFactory.withUri(
                getUriFromShortId(shortId)
        );
    }
}
