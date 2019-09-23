/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import guru.bubl.module.model.User;
import guru.bubl.module.model.UserUris;
import guru.bubl.module.model.center_graph_element.CenterGraphElementOperatorFactory;
import guru.bubl.module.model.graph.GraphElementOperator;
import guru.bubl.module.model.graph.GraphElementOperatorFactory;
import guru.bubl.module.model.graph.GraphElementType;
import guru.bubl.module.model.graph.GraphFactory;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.service.resources.edge.EdgeResource;
import guru.bubl.service.resources.edge.EdgeResourceFactory;
import guru.bubl.service.resources.meta.IdentificationResourceFactory;
import guru.bubl.service.resources.meta.IdentifierResource;
import guru.bubl.service.resources.vertex.VertexResource;
import guru.bubl.service.resources.vertex.VertexResourceFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GraphResource {

    @Inject
    GraphFactory graphFactory;

    @Inject
    VertexResourceFactory vertexResourceFactory;

    @Inject
    EdgeResourceFactory edgeResourceFactory;

    @Inject
    IdentificationResourceFactory identificationResourceFactory;

    @Inject
    GraphElementOperatorFactory graphElementOperatorFactory;

    @Inject
    CenterGraphElementOperatorFactory centerGraphElementOperatorFactory;

    private User user;

    @AssistedInject
    public GraphResource(
            @Assisted User user
    ) {
        this.user = user;
    }

    @Path("/{type}/{shortId}/center")
    @DELETE
    public Response removeCenter(@PathParam("type") String type, @PathParam("shortId") String shortId) {
        centerGraphElementOperatorFactory.usingFriendlyResource(
                graphElementFromShortIdAndType(shortId, type)
        ).remove();
        return Response.noContent().build();
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

//    @Path("/schema")
//    public SchemaResource schemaResource() {
//        return schemaResourceFactory.fromUserGraph(
//                userGraph()
//        );
//    }

    @Path("/identification")
    public IdentifierResource identificationResource() {
        return identificationResourceFactory.forAuthenticatedUserAndGraph(
                user,
                userGraph()
        );
    }

    private UserGraph userGraph() {
        return graphFactory.loadForUser(
                user
        );
    }

    private GraphElementOperator graphElementFromShortIdAndType(String shortId, String typeStr) {
        URI uri = new UserUris(
                user
        ).uriFromTypeStringAndShortId(typeStr, shortId);
        return graphElementOperatorFactory.withUri(uri);
    }

}
