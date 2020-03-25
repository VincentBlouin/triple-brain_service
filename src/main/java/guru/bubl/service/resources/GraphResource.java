/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import guru.bubl.module.model.User;
import guru.bubl.module.model.UserUris;
import guru.bubl.module.model.center_graph_element.CenterGraphElementOperator;
import guru.bubl.module.model.center_graph_element.CenterGraphElementOperatorFactory;
import guru.bubl.module.model.graph.GraphElementOperator;
import guru.bubl.module.model.graph.GraphElementOperatorFactory;
import guru.bubl.module.model.graph.GraphFactory;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.graph.vertex.NbNeighbors;
import guru.bubl.module.model.graph.vertex.VertexTypeOperatorFactory;
import guru.bubl.service.resources.edge.EdgeResource;
import guru.bubl.service.resources.edge.EdgeResourceFactory;
import guru.bubl.service.resources.tag.TagResourceFactory;
import guru.bubl.service.resources.tag.TagResource;
import guru.bubl.service.resources.vertex.VertexResource;
import guru.bubl.service.resources.vertex.VertexResourceFactory;
import org.codehaus.jettison.json.JSONObject;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Date;

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
    TagResourceFactory tagResourceFactory;

    @Inject
    GraphElementOperatorFactory graphElementOperatorFactory;

    @Inject
    CenterGraphElementOperatorFactory centerGraphElementOperatorFactory;

    @Inject
    GraphElementCollectionResourceFactory graphElementCollectionResourceFactory;

    @Inject
    VertexTypeOperatorFactory vertexTypeOperatorFactory;

    private User user;

    @AssistedInject
    public GraphResource(
            @Assisted User user
    ) {
        this.user = user;
    }

    @Path("/{type}/{shortId}/center")
    @POST
    public Response makeCenter(@PathParam("type") String type, @PathParam("shortId") String shortId, JSONObject data) {
        CenterGraphElementOperator centerGraphElementOperator = centerGraphElementOperatorFactory.usingFriendlyResource(
                graphElementFromShortIdAndType(shortId, type)
        );
        centerGraphElementOperator.incrementNumberOfVisits();
        centerGraphElementOperator.setLastCenterDate(
                new Date(
                        data.optLong(
                                "lastCenterDate",
                                new Date().getTime()
                        )
                )
        );
        return Response.noContent().build();
    }

    /*
        makeCenterIdentification should not be necessary because makeCenter is a generic method
        for any graph element type but somehow "makeCenter" does not work for identification
     */
    @Path("/identification/{shortId}/center")
    @POST
    public Response makeCenterIdentification(@PathParam("shortId") String shortId,
                                             JSONObject data
    ) {
        return this.makeCenter(
                "identification",
                shortId,
                data
        );
    }

    @Path("/{type}/{shortId}/center")
    @DELETE
    public Response removeCenter(@PathParam("type") String type, @PathParam("shortId") String shortId) {
        centerGraphElementOperatorFactory.usingFriendlyResource(
                graphElementFromShortIdAndType(shortId, type)
        ).remove();
        return Response.noContent().build();
    }


    @POST
    @Path("/{type}/{shortId}/colors")
    public Response saveColors(
            @PathParam("type") String type,
            @PathParam("shortId") String shortId,
            JSONObject colors
    ) {
        graphElementFromShortIdAndType(shortId, type).setColors(
                colors.toString()
        );
        return Response.noContent().build();
    }

    @POST
    @Path("/{type}/{shortId}/nbNeighbors")
    public Response setNbNeighbors(
            @PathParam("type") String type,
            @PathParam("shortId") String shortId,
            JSONObject nbNeighbors
    ) {
        URI uri = new UserUris(
                user
        ).uriFromTypeStringAndShortId(type, shortId);
        NbNeighbors nbNeighborsOperator = vertexTypeOperatorFactory.withUri(uri).getNbNeighbors();
        if (nbNeighbors.has("private_")) {
            nbNeighborsOperator.setPrivate(nbNeighbors.optInt("private_", 0));
        }
        if (nbNeighbors.has("friend")) {
            nbNeighborsOperator.setFriend(nbNeighbors.optInt("friend", 0));
        }
        if (nbNeighbors.has("public_")) {
            nbNeighborsOperator.setPublic(nbNeighbors.optInt("public_", 0));
        }
        return Response.noContent().build();
    }

    /*
        removeIdentificationCenter should not be necessary because removeCenter is a generic method
        for any graph element type but somehow "removeCenter" does not work for identification
     */
    @Path("/identification/{shortId}/center")
    @DELETE
    public Response removeIdentificationCenter(@PathParam("shortId") String shortId) {
        return this.removeCenter(
                "identification",
                shortId
        );
    }

    @Path("/graphElement/collection")
    public GraphElementCollectionResource graphElementCollectionResource() {
        return graphElementCollectionResourceFactory.withUserGraph(
                userGraph()
        );
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

    @Path("/identification")
    public TagResource identificationResource() {
        return tagResourceFactory.forAuthenticatedUserAndGraph(
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
