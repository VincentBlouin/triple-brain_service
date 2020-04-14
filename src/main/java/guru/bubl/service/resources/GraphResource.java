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
import guru.bubl.module.model.graph.edge.EdgeFactory;
import guru.bubl.module.model.graph.edge.EdgeOperator;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.service.resources.edge.EdgeResource;
import guru.bubl.service.resources.edge.EdgeResourceFactory;
import guru.bubl.service.resources.group_relation.GroupRelationResource;
import guru.bubl.service.resources.group_relation.GroupRelationResourceFactory;
import guru.bubl.service.resources.tag.TagResource;
import guru.bubl.service.resources.tag.TagResourceFactory;
import guru.bubl.service.resources.vertex.OwnedSurroundGraphResource;
import guru.bubl.service.resources.vertex.VertexResource;
import guru.bubl.service.resources.vertex.VertexResourceFactory;
import org.apache.commons.lang.StringUtils;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.net.URI;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GraphResource {

    @Inject
    private GraphFactory graphFactory;

    @Inject
    private VertexResourceFactory vertexResourceFactory;

    @Inject
    private EdgeResourceFactory edgeResourceFactory;

    @Inject
    private TagResourceFactory tagResourceFactory;

    @Inject
    private GraphElementOperatorFactory graphElementOperatorFactory;

    @Inject
    private GroupRelationResourceFactory groupRelationResourceFactory;

    @Inject
    private EdgeFactory edgeFactory;

    @Inject
    private GraphElementCollectionResourceFactory graphElementCollectionResourceFactory;

    @Inject
    private CenterGraphElementOperatorFactory centerGraphElementOperatorFactory;

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

    @Path("/gr")
    public GroupRelationResource groupRelationResource() {
        return groupRelationResourceFactory.withUserGraph(
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

    @Path("/graphElement/collection")
    public GraphElementCollectionResource graphElementCollectionResource() {
        return graphElementCollectionResourceFactory.withUserGraph(
                userGraph()
        );
    }

    @Path("/{type}/{shortId}/surround_graph")
    public OwnedSurroundGraphResource getSurroundGraph(
            @PathParam("type") String type,
            @PathParam("shortId") String shortId,
            @QueryParam("center") String isCenter
    ) {
        GraphElementOperator graphElementOperator;
        CenterGraphElementOperator centerGraphElementOperator;
        if (type.toLowerCase().equals("edge")) {
            EdgeOperator edgeOperator = edgeFactory.withUri(
                    new UserUris(user).edgeUriFromShortId(shortId)
            );
            graphElementOperator = graphElementOperatorFactory.withUri(
                    edgeOperator.sourceUri()
            );
            centerGraphElementOperator = centerGraphElementOperatorFactory.usingFriendlyResource(
                    edgeOperator
            );
        } else {
            graphElementOperator = graphElementFromShortIdAndType(shortId, type);
            centerGraphElementOperator = centerGraphElementOperatorFactory.usingFriendlyResource(
                    graphElementOperator
            );
        }
        if (!StringUtils.isEmpty(isCenter) && isCenter.equals("true")) {
            centerGraphElementOperator.incrementNumberOfVisits();
            centerGraphElementOperator.updateLastCenterDate();
        }
        return new OwnedSurroundGraphResource(
                graphFactory.loadForUser(user),
                graphElementOperator
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
