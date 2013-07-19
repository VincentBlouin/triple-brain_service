package org.triple_brain.service.resources;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import org.triple_brain.module.model.User;
import org.triple_brain.module.model.graph.*;
import org.triple_brain.module.model.json.graph.GraphJSONFields;
import org.triple_brain.service.resources.vertex.VertexResource;
import org.triple_brain.service.resources.vertex.VertexResourceFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Iterator;

import static org.triple_brain.module.common_utils.Uris.decodeURL;

/**
 * Copyright Mozilla Public License 1.1
 */
@Produces(MediaType.APPLICATION_JSON)
public class GraphResource {

    @Inject
    GraphFactory graphFactory;

    @Inject
    VertexResourceFactory vertexResourceFactory;

    @Inject
    EdgeResourceFactory edgeResourceFactory;

    private User user;

    @AssistedInject
    public GraphResource(
            @Assisted User user
    ) {
        this.user = user;
    }

    @Path("/vertex")
    public VertexResource vertexResource() {
        return vertexResourceFactory.withUserGraph(
                userGraph()
        );
    }

    @Path("/edge")
    public EdgeResource edgeResource() {
        return edgeResourceFactory.withUserGraph(
                userGraph()
        );
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_XML)
    public Response rdfXML() {
        UserGraph userGraph = userGraph();
        return Response.ok(userGraph.toRdfXml()).build();
    }

    @GET
    @Path("/{depthOfSubVertices}")
    public Response graph(
            @PathParam("depthOfSubVertices") Integer depthOfSubVertices
    ) {
        UserGraph userGraph = userGraph();
        SubGraph graph = userGraph.graphWithDefaultVertexAndDepth(depthOfSubVertices);
        return Response.ok(
                GraphJSONFields.toJson(graph),
                MediaType.APPLICATION_JSON
        ).build();
    }

    @GET
    @Path("/{depthOfSubVertices}/{centralVertexId}")
    public Response graph(
            @PathParam("depthOfSubVertices") Integer depthOfSubVertices,
            @PathParam("centralVertexId") String centralVertexId
    ) {
        try {
            centralVertexId = decodeURL(centralVertexId);
        } catch (UnsupportedEncodingException e) {
            Response.status(Response.Status.BAD_REQUEST).build();
        }
        UserGraph userGraph = userGraph();
        Vertex vertex = userGraph.vertexWithURI(URI.create(
                centralVertexId
        ));
//        if(!canAccessVertex(vertex)){
//            throw new WebApplicationException(
//                    Response.Status.FORBIDDEN
//            );
//        }
        SubGraph graph = userGraph.graphWithDepthAndCenterVertexId(
                depthOfSubVertices,
                centralVertexId
        );
//        removeVerticesNotAllowedToAccess(
//                graph
//        );
        return Response.ok(
                GraphJSONFields.toJson(graph),
                MediaType.APPLICATION_JSON
        ).build();
    }

    private UserGraph userGraph() {
        return graphFactory.loadForUser(
                user
        );
    }

    private boolean canAccessVertex(Vertex vertex) {
        return vertex.owner().equals(user) ||
                vertex.isPublic();
    }

    private void removeVerticesNotAllowedToAccess(SubGraph graph){
        Iterator<VertexInSubGraph> iterator =  graph.vertices().iterator();
        while(iterator.hasNext()){
            Vertex vertex = iterator.next();
            if(!canAccessVertex(vertex)){
                iterator.remove();
            }
        }
    }
}
