package org.triple_brain.service.resources.vertex;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.triple_brain.module.model.UserUris;
import org.triple_brain.module.model.graph.Edge;
import org.triple_brain.module.model.graph.UserGraph;
import org.triple_brain.module.model.graph.Vertex;
import org.triple_brain.module.model.json.graph.EdgeJsonFields;
import org.triple_brain.module.model.json.graph.VertexJsonFields;
import org.triple_brain.module.search.GraphIndexer;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

import static org.triple_brain.module.model.json.StatementJsonFields.*;
import static org.triple_brain.service.resources.GraphManipulatorResourceUtils.userFromSession;

/**
 * Copyright Mozilla Public License 1.1
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VertexResource {

    @Inject
    GraphIndexer graphIndexer;

    @Inject
    VertexSuggestionResourceFactory vertexSuggestionResourceFactory;

    @Inject
    VertexPublicAccessResourceFactory vertexPublicAccessResourceFactory;

    @Inject VertexIdentificationResourceFactory vertexIdentificationResourceFactory;

    @Inject
    VertexSurroundGraphResourceFactory vertexSurroundGraphResourceFactory;

    private UserGraph userGraph;

    @AssistedInject
    public VertexResource(
            @Assisted UserGraph userGraph
    ){
        this.userGraph = userGraph;
    }

    @POST
    @Path("/{sourceVertexShortId}")
    public Response addVertexAndEdgeToSourceVertex(
            @Context HttpServletRequest request
    ) {
        Vertex sourceVertex = userGraph.vertexWithURI(URI.create(
            request.getRequestURI()
        ));
        Edge createdEdge = sourceVertex.addVertexAndRelation();
        Vertex createdVertex = createdEdge.destinationVertex();
        graphIndexer.indexVertexOfUser(
                createdVertex,
                userFromSession(request.getSession())
        );
        JSONObject jsonCreatedStatement = new JSONObject();
        try {
            jsonCreatedStatement.put(
                    SOURCE_VERTEX, VertexJsonFields.toJson(sourceVertex)
            );
            jsonCreatedStatement.put(
                    EDGE, EdgeJsonFields.toJson(createdEdge)
            );
            jsonCreatedStatement.put(
                    END_VERTEX, VertexJsonFields.toJson(createdVertex)
            );
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        //TODO response should be of type created
        return Response.ok(jsonCreatedStatement).build();
    }

    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/{vertexShortId}")
    public Response removeVertex(
            @Context HttpServletRequest request
    ) {
        graphIndexer.deleteVertexOfUser(
                userGraph.vertexWithURI(URI.create(
                    request.getRequestURI()
                )),
                userGraph.user()
        );
        Vertex vertex = userGraph.vertexWithURI(URI.create(
                request.getRequestURI()
        ));
        vertex.remove();
        return Response.ok().build();
    }

    @POST
    @Path("{shortId}/label")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    public Response updateVertexLabel(
            @PathParam("shortId") String shortId,
            @QueryParam("label") String label
    ) {
        URI vertexId = uriFromShortId(shortId);
        Vertex vertex = userGraph.vertexWithURI(
                vertexId
        );
        vertex.label(label);

        graphIndexer.indexVertexOfUser(
                vertex,
                userGraph.user()
        );
        return Response.ok().build();
    }

    @POST
    @Path("{shortId}/note")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    public Response updateVertexNote(
            @PathParam("shortId") String shortId,
            String note
    ) {
        URI vertexId = uriFromShortId(shortId);
        Vertex vertex = userGraph.vertexWithURI(
                vertexId
        );
        vertex.note(note);
        graphIndexer.indexVertex(vertex);
        return Response.ok().build();
    }

    @Path("{shortId}/surround_graph/{depthOfSubVertices}")
    public VertexSurroundGraphResource getVertexSurroundGraphResource(
            @PathParam("shortId") String shortId,
            @PathParam("depthOfSubVertices") Integer depth
            ){
        return vertexSurroundGraphResourceFactory.ofCenterVertexWithDepth(
                vertexFromShortId(shortId),
                depth
        );
    }

    @Path("{shortId}/identification")
    public VertexIdentificationResource getVertexIdentificationResource(@PathParam("shortId") String shortId){
        return vertexIdentificationResourceFactory.forVertex(
                vertexFromShortId(shortId)
        );
    }

    @Path("{shortId}/suggestions")
    public VertexSuggestionResource getVertexSuggestionResource(
            @PathParam("shortId") String shortId
    ) {
        return vertexSuggestionResourceFactory.ofVertex(
                vertexFromShortId(shortId)
        );
    }

    @Path("{shortId}/public_access")
    public VertexPublicAccessResource getPublicAccessResource(
            @PathParam("shortId") String shortId
    ){
        return vertexPublicAccessResourceFactory.ofVertex(
                vertexFromShortId(shortId)
        );
    }

    private URI uriFromShortId(String shortId){
        return  new UserUris(
                userGraph.user()
        ).vertexUriFromShortId(
                shortId
        );
    }

    private Vertex vertexFromShortId(String shortId){
        URI vertexId = uriFromShortId(shortId);
        return userGraph.vertexWithURI(
                vertexId
        );
    }
}
