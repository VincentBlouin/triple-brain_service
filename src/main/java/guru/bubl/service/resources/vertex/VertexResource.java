/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.vertex;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import guru.bubl.module.model.UserUris;
import guru.bubl.module.model.center_graph_element.CenterGraphElementOperatorFactory;
import guru.bubl.module.model.graph.GraphElementType;
import guru.bubl.module.model.graph.GraphTransactional;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.graph.edge.EdgeOperator;
import guru.bubl.module.model.graph.edge.EdgePojo;
import guru.bubl.module.model.graph.vertex.VertexInSubGraphPojo;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import guru.bubl.module.model.graph.vertex.VertexPojo;
import guru.bubl.module.model.json.LocalizedStringJson;
import guru.bubl.module.model.json.StatementJsonFields;
import guru.bubl.module.model.json.graph.EdgeJson;
import guru.bubl.module.model.json.graph.VertexInSubGraphJson;
import guru.bubl.module.model.search.GraphIndexer;
import guru.bubl.service.resources.GraphElementIdentificationResource;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
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
    VertexCollectionPublicAccessResourceFactory vertexCollectionPublicAccessResourceFactory;

    @Inject
    GraphElementIdentificationResourceFactory graphElementIdentificationResourceFactory;

    @Inject
    VertexGroupResourceFactory vertexGroupResourceFactory;

    @Inject
    VertexImageResourceFactory vertexImageResourceFactory;

    @Inject
    CenterGraphElementOperatorFactory centerGraphElementOperatorFactory;

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
        centerGraphElementOperatorFactory.usingGraphElement(
                newVertex
        ).incrementNumberOfVisits();
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

        EdgeOperator createdEdge = sourceVertex.addVertexAndRelation();
        VertexOperator createdVertex = createdEdge.destinationVertex();
        graphIndexer.indexVertex(
                createdVertex
        );
        graphIndexer.commit();
        JSONObject jsonCreatedStatement = new JSONObject();
        try {
            jsonCreatedStatement.put(
                    StatementJsonFields.source_vertex.name(),
                    VertexInSubGraphJson.toJson(
                            new VertexInSubGraphPojo(
                                    sourceVertex.uri()
                            )
                    )
            );
            jsonCreatedStatement.put(
                    StatementJsonFields.edge.name(),
                    EdgeJson.toJson(new EdgePojo(
                                    createdEdge.uri(),
                                    createdEdge.sourceVertex().uri(),
                                    createdEdge.destinationVertex().uri()
                            )
                    )
            );
            jsonCreatedStatement.put(
                    StatementJsonFields.end_vertex.name(),
                    VertexInSubGraphJson.toJson(
                            new VertexInSubGraphPojo(
                                    createdVertex.uri()
                            )
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
        for (EdgeOperator edge : vertex.connectedEdges()) {
            graphIndexer.deleteGraphElement(
                    edge
            );
            connectedVertices.add(edge.otherVertex(vertex));
        }
        vertex.remove();
        for (VertexOperator connectedVertex : connectedVertices) {
            graphIndexer.indexVertex(
                    connectedVertex
            );
        }
        graphIndexer.commit();
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
        graphIndexer.indexVertex(
                vertex
        );
        graphIndexer.commit();
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
        graphIndexer.indexVertex(vertex);
        graphIndexer.commit();
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
    public VertexOwnedSurroundGraphResource getVertexSurroundGraphResource(
            @PathParam("shortId") String shortId,
            @QueryParam("center") String isCenter
    ) {
        VertexOperator vertex = vertexFromShortId(shortId);
        if (!StringUtils.isEmpty(isCenter) && isCenter.equals("true")) {
            centerGraphElementOperatorFactory.usingGraphElement(
                    vertex
            ).incrementNumberOfVisits();
        }
        return new VertexOwnedSurroundGraphResource(
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
