/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.vertex;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import guru.bubl.module.model.UserUris;
import guru.bubl.module.model.center_graph_element.CenterGraphElementOperator;
import guru.bubl.module.model.center_graph_element.CenterGraphElementOperatorFactory;
import guru.bubl.module.model.graph.GraphElementType;
import guru.bubl.module.model.graph.ShareLevel;
import guru.bubl.module.model.graph.edge.EdgeJson;
import guru.bubl.module.model.graph.edge.EdgeOperator;
import guru.bubl.module.model.graph.edge.EdgePojo;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.graph.vertex.*;
import guru.bubl.module.model.json.LocalizedStringJson;
import guru.bubl.module.model.json.StatementJsonFields;
import guru.bubl.module.model.search.GraphIndexer;
import guru.bubl.service.resources.GraphElementTagResource;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VertexResource {

    @Inject
    GraphIndexer graphIndexer;

    @Inject
    VertexSuggestionResourceFactory vertexSuggestionResourceFactory;

    @Inject
    VertexCollectionResourceFactory vertexCollectionResourceFactory;

    @Inject
    GraphElementTagResourceFactory graphElementTagResourceFactory;

    @Inject
    VertexImageResourceFactory vertexImageResourceFactory;

    @Inject
    CenterGraphElementOperatorFactory centerGraphElementOperatorFactory;

    @Inject
    VertexFactory vertexFactory;

    private UserGraph userGraph;

    @AssistedInject
    public VertexResource(
            @Assisted UserGraph userGraph
    ) {
        this.userGraph = userGraph;
    }

    @POST
    @Path("/")
    public Response createVertex() {
        VertexPojo newVertex = userGraph.createVertex();
        CenterGraphElementOperator centerGraphElementOperator = centerGraphElementOperatorFactory.usingFriendlyResource(
                newVertex
        );
        centerGraphElementOperator.incrementNumberOfVisits();
        centerGraphElementOperator.updateLastCenterDate();
        return Response.ok()
                .entity(VertexInSubGraphJson.toJson(
                        new VertexInSubGraphPojo(
                                newVertex
                        )
                ))
                .build();
    }

    @POST
    @Path("/{sourceVertexShortId}")
    public Response addVertexAndEdgeToSourceVertex(
            @PathParam("sourceVertexShortId") String sourceVertexShortId,
            JSONObject options
    ) {
        VertexOperator sourceVertex = vertexFromShortId(
                sourceVertexShortId
        );
        EdgePojo newEdge;
        if (options.has("vertexId") && options.has("edgeId")) {
            newEdge = sourceVertex.addVertexAndRelationWithIds(
                    options.optString("vertexId"),
                    options.optString("edgeId")
            );
        } else {
            newEdge = sourceVertex.addVertexAndRelation();
        }
        VertexInSubGraphPojo newVertex = newEdge.destinationVertex();
        VertexInSubGraphPojo sourceVertexPojo = new VertexInSubGraphPojo(
                sourceVertex.uri()
        );
        JSONObject jsonCreatedStatement = new JSONObject();
        try {
            jsonCreatedStatement.put(
                    StatementJsonFields.source_vertex.name(),
                    VertexInSubGraphJson.toJson(
                            sourceVertexPojo
                    )
            );
            jsonCreatedStatement.put(
                    StatementJsonFields.edge.name(),
                    EdgeJson.toJson(new EdgePojo(
                                    newEdge.getGraphElement(),
                                    sourceVertexPojo,
                                    newVertex
                            )
                    )
            );
            jsonCreatedStatement.put(
                    StatementJsonFields.end_vertex.name(),
                    VertexInSubGraphJson.toJson(
                            newVertex
                    )
            );
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        //TODO response should be of type created
        return Response.ok(jsonCreatedStatement).build();
    }

    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/{shortId}")
    public Response removeVertex(
            @PathParam("shortId") String shortId
    ) {
        VertexOperator vertex = vertexFromShortId(
                shortId
        );
        Set<VertexOperator> connectedVertices = new HashSet<VertexOperator>();
        graphIndexer.deleteGraphElement(
                vertex
        );
        for (EdgeOperator edge : vertex.connectedEdges().values()) {
            graphIndexer.deleteGraphElement(
                    edge
            );
            connectedVertices.add(edge.otherVertex(vertex));
        }
        vertex.remove();
        return Response.ok().build();
    }

    @POST
    @Path("/{shortId}/pattern")
    public Response setAsPattern(@PathParam("shortId") String shortId) {
        URI vertexId = uriFromShortId(shortId);
        VertexOperator vertex = userGraph.vertexWithUri(
                vertexId
        );
        vertex.makePattern();
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{shortId}/pattern")
    public Response undoPattern(@PathParam("shortId") String shortId) {
        URI vertexId = uriFromShortId(shortId);
        VertexOperator vertex = userGraph.vertexWithUri(
                vertexId
        );
        vertex.undoPattern();
        return Response.noContent().build();
    }


    @POST
    @Path("{shortId}/label")
    public Response updateVertexLabel(
            @PathParam("shortId") String shortId,
            JSONObject localizedLabel
    ) {
        URI vertexId = uriFromShortId(shortId);
        VertexOperator vertex = userGraph.vertexWithUri(
                vertexId
        );
        vertex.label(
                localizedLabel.optString(
                        LocalizedStringJson.content.name()
                )
        );
        return Response.noContent().build();
    }

    @POST
    @Path("{shortId}/comment")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response updateVertexComments(
            @PathParam("shortId") String shortId,
            String comment
    ) {
        URI vertexId = uriFromShortId(shortId);
        VertexOperator vertex = userGraph.vertexWithUri(
                vertexId
        );
        vertex.comment(comment);
        return Response.noContent().build();
    }


//    @Path("{shortId}/image")
//    public VertexImageResource image(
//            @PathParam("shortId") String shortId
//    ) {
//        return vertexImageResourceFactory.ofVertex(
//                vertexFromShortId(shortId)
//        );
//    }

    @Path("{shortId}/surround_graph")
    public OwnedSurroundGraphResource getVertexSurroundGraphResource(
            @PathParam("shortId") String shortId,
            @QueryParam("center") String isCenter
    ) {
        VertexOperator vertex = vertexFromShortId(shortId);
        if (!StringUtils.isEmpty(isCenter) && isCenter.equals("true")) {
            CenterGraphElementOperator centerGraphElementOperator = centerGraphElementOperatorFactory.usingFriendlyResource(
                    vertex
            );
            centerGraphElementOperator.incrementNumberOfVisits();
            centerGraphElementOperator.updateLastCenterDate();
        }
        return new OwnedSurroundGraphResource(
                userGraph,
                vertex
        );
    }

    @Path("{shortId}/identification")
    public GraphElementTagResource getVertexIdentificationResource(
            @PathParam("shortId") String shortId) {
        return graphElementTagResourceFactory.forGraphElement(
                vertexFromShortId(shortId),
                GraphElementType.Vertex
        );
    }

//    @Path("{shortId}/suggestions")
//    public VertexSuggestionResource getVertexSuggestionResource(
//            @PathParam("shortId") String shortId
//    ) {
//        return vertexSuggestionResourceFactory.ofVertex(
//                vertexFromShortId(shortId)
//        );
//    }

    @Path("{shortId}/shareLevel")
    @POST
    public Response setShareLevel(@PathParam("shortId") String shortId, JSONObject shareLevel) {
        vertexFromShortId(shortId).setShareLevel(
                ShareLevel.valueOf(
                        shareLevel.optString("shareLevel", ShareLevel.PRIVATE.name()).toUpperCase()
                )
        );
        return Response.noContent().build();
    }

    @Path("collection")
    public VertexCollectionResource getCollectionResource() {
        return vertexCollectionResourceFactory.withUserGraph(
                userGraph
        );
    }

    @POST
    @Path("{shortId}/childrenIndex")
    public Response saveChildrenIndexes(
            @PathParam("shortId") String shortId,
            JSONObject childrenIndexes
    ) {
        vertexFactory.withUri(uriFromShortId(shortId)).setChildrenIndex(
                childrenIndexes.toString()
        );
        return Response.noContent().build();
    }

    @POST
    @Path("{shortId}/font")
    public Response saveFont(
            @PathParam("shortId") String shortId,
            JSONObject font
    ) {
        vertexFactory.withUri(uriFromShortId(shortId)).setFont(
                font.toString()
        );
        return Response.noContent().build();
    }

    @POST
    @Path("{shortId}/mergeTo/{destinationShortId}")
    public Response mergeTo(
            @PathParam("shortId") String shortId,
            @PathParam("destinationShortId") String destinationShortId
    ) {
        URI destinationVertexUri = uriFromShortId(destinationShortId);
        if (!userGraph.haveElementWithId(destinationVertexUri)) {
            return Response.status(
                    Response.Status.BAD_REQUEST
            ).build();
        }
        vertexFactory.withUri(uriFromShortId(shortId)).mergeTo(
                vertexFactory.withUri(destinationVertexUri)
        );
        return Response.noContent().build();
    }

    private URI uriFromShortId(String shortId) {
        return new UserUris(
                userGraph.user()
        ).vertexUriFromShortId(
                shortId
        );
    }

    private VertexOperator vertexFromShortId(String shortId) {
        URI vertexId = uriFromShortId(shortId);
        return userGraph.vertexWithUri(
                vertexId
        );
    }
}
