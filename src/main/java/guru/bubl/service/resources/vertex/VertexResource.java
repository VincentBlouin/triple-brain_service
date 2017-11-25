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
import guru.bubl.module.model.graph.GraphTransactional;
import guru.bubl.module.model.graph.edge.EdgeOperator;
import guru.bubl.module.model.graph.edge.EdgePojo;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.graph.vertex.*;
import guru.bubl.module.model.json.LocalizedStringJson;
import guru.bubl.module.model.json.StatementJsonFields;
import guru.bubl.module.model.graph.edge.EdgeJson;
import guru.bubl.module.model.graph.vertex.VertexInSubGraphJson;
import guru.bubl.module.model.search.GraphIndexer;
import guru.bubl.service.resources.GraphElementIdentificationResource;
import guru.bubl.service.resources.sort.GraphElementSortResource;
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
    VertexPublicAccessResourceFactory vertexPublicAccessResourceFactory;

    @Inject
    VertexCollectionResourceFactory vertexCollectionResourceFactory;

    @Inject
    VertexCollectionPublicAccessResourceFactory vertexCollectionPublicAccessResourceFactory;

    @Inject
    GraphElementIdentificationResourceFactory graphElementIdentificationResourceFactory;

    @Inject
    VertexGroupResourceFactory vertexGroupResourceFactory;

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
    @GraphTransactional
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

    @Path("/group")
    public VertexGroupResource getVertexGroupResource() {
        return vertexGroupResourceFactory.withUserGraph(
                userGraph
        );
    }

    @POST
    @GraphTransactional
    @Path("/{sourceVertexShortId}")
    public Response addVertexAndEdgeToSourceVertex(
            @PathParam("sourceVertexShortId") String sourceVertexShortId
    ) {
        VertexOperator sourceVertex = vertexFromShortId(
                sourceVertexShortId
        );

        EdgePojo newEdge = sourceVertex.addVertexAndRelation();
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
    @GraphTransactional
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
    @GraphTransactional
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
    @GraphTransactional
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


    @Path("{shortId}/image")
    @GraphTransactional
    public VertexImageResource image(
            @PathParam("shortId") String shortId
    ) {
        return vertexImageResourceFactory.ofVertex(
                vertexFromShortId(shortId)
        );
    }

    @Path("any")
    @GET
    @GraphTransactional
    @Produces(MediaType.TEXT_PLAIN)
    public Response getAnyVertexUri(
    ) {
        return Response.ok().entity(
                userGraph.defaultVertex().uri().toString()
        ).build();
    }

    @Path("{shortId}/surround_graph")
    @GraphTransactional
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
    @GraphTransactional
    public GraphElementIdentificationResource getVertexIdentificationResource(
            @PathParam("shortId") String shortId) {
        return graphElementIdentificationResourceFactory.forGraphElement(
                vertexFromShortId(shortId),
                GraphElementType.vertex
        );
    }

    @Path("{shortId}/suggestions")
    @GraphTransactional
    public VertexSuggestionResource getVertexSuggestionResource(
            @PathParam("shortId") String shortId
    ) {
        return vertexSuggestionResourceFactory.ofVertex(
                vertexFromShortId(shortId)
        );
    }

    @Path("collection")
    @GraphTransactional
    public VertexCollectionResource getCollectionResource() {
        return vertexCollectionResourceFactory.withUserGraph(
                userGraph
        );
    }

    /*
    * public_access should be in the vertex collection
    * resource but it doesn't work. I get 415 http status
    * Unsupported media type. I don't understand.
    */
    @Path("collection/public_access")
    @GraphTransactional
    public VertexCollectionPublicAccessResource getCollectionPublicAccessResource() {
        return vertexCollectionPublicAccessResourceFactory.withUserGraph(
                userGraph
        );
    }

    @Path("{shortId}/public_access")
    @GraphTransactional
    public VertexPublicAccessResource getPublicAccessResource(
            @PathParam("shortId") String shortId
    ) {
        return vertexPublicAccessResourceFactory.ofVertex(
                vertexFromShortId(shortId)
        );
    }

    @Path("{shortId}/sort")
    @GraphTransactional
    public GraphElementSortResource getSortResource(
            @PathParam("shortId") String shortId
    ) {
        return new GraphElementSortResource(
                vertexFromShortId(shortId)
        );
    }

    @POST
    @Path("{shortId}/mergeTo/{destinationShortId}")
    @GraphTransactional
    public Response getMergeResource(
            @PathParam("shortId") String shortId,
            @PathParam("destinationShortId") String destinationShortId
    ) {
        URI destinationVertexUri = uriFromShortId(destinationShortId);
        if(!userGraph.haveElementWithId(destinationVertexUri)){
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
