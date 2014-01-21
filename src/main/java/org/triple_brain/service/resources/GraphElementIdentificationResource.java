package org.triple_brain.service.resources;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import org.codehaus.jettison.json.JSONObject;
import org.triple_brain.module.model.*;
import org.triple_brain.module.model.graph.FriendlyResourcePojo;
import org.triple_brain.module.model.graph.GraphElementOperator;
import org.triple_brain.module.model.graph.GraphTransactional;
import org.triple_brain.module.model.graph.edge.Edge;
import org.triple_brain.module.model.graph.vertex.VertexOperator;
import org.triple_brain.module.model.validator.FriendlyResourceValidator;
import org.triple_brain.module.search.GraphIndexer;
import org.triple_brain.service.ResourceServiceUtils;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

/*
* Copyright Mozilla Public License 1.1
*/
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GraphElementIdentificationResource {
    public static enum identification_types {
        SAME_AS, TYPE, GENERIC
    }

    public static final String IDENTIFICATION_TYPE_STRING = "type";

    @Inject
    FriendlyResourceFactory friendlyResourceFactory;

    @Inject
    GraphIndexer graphIndexer;

    @Inject
    ResourceServiceUtils resourceServiceUtils;

    @Inject
    GraphTransaction graphTransaction;

    private GraphElementOperator graphElement;
    private boolean isVertex;

    @AssistedInject
    public GraphElementIdentificationResource(
            @Assisted GraphElementOperator graphElement,
            @Assisted boolean isVertex
    ) {
        this.graphElement = graphElement;
        this.isVertex = isVertex;
    }

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @GraphTransactional
    @Path("/")
    public Response add(JSONObject identification) {
        FriendlyResourceValidator validator = new FriendlyResourceValidator();
        if (!validator.validate(identification).isEmpty()) {
            throw new WebApplicationException(
                    Response.Status.NOT_ACCEPTABLE
            );
        }
        FriendlyResource friendlyResource = friendlyResourceFactory.createOrLoadUsingJson(
                identification
        );
        String type = identification.optString("type");
        if (type.equalsIgnoreCase(identification_types.SAME_AS.name())) {
            graphElement.addSameAs(
                    friendlyResource
            );
        } else if (type.equalsIgnoreCase(identification_types.TYPE.name())) {
            graphElement.addType(
                    friendlyResource
            );
        } else if (type.equalsIgnoreCase(identification_types.GENERIC.name())) {
            graphElement.addGenericIdentification(
                    friendlyResource
            );
        } else {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        reindexGraphElement();
        FriendlyResource friendlyResourceCached = new FriendlyResourcePojo(
                friendlyResource.uri(),
                friendlyResource.label(),
                friendlyResource.images(),
                friendlyResource.comment(),
                friendlyResource.creationDate(),
                friendlyResource.lastModificationDate()
        );

        updateImagesOfExternalResourceIfNecessary(
                friendlyResourceCached
        );
        updateDescriptionOfExternalResourceIfNecessary(
                friendlyResourceCached
        );
        return Response.ok().build();
    }

    @DELETE
    @GraphTransactional
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/")
    public Response removeFriendlyResource(
            @QueryParam("uri") String identificationUri
    ) {
        FriendlyResource friendlyResource = friendlyResourceFactory.createOrLoadFromUri(
                URI.create(identificationUri)
        );
        graphElement.removeIdentification(friendlyResource);
        reindexGraphElement();
        return Response.ok().build();
    }

    private void updateImagesOfExternalResourceIfNecessary(FriendlyResource friendlyResource) {
        if (friendlyResource.gotImages()) {
            return;
        }
        if (FreebaseFriendlyResource.isFromFreebase(friendlyResource)) {
            FreebaseFriendlyResource freebaseResource = FreebaseFriendlyResource.fromFriendlyResource(
                    friendlyResource
            );
            freebaseResource.getImages(
                    resourceServiceUtils.imagesUpdateHandler
            );
        }
    }

    private void updateDescriptionOfExternalResourceIfNecessary(FriendlyResource friendlyResource) {
        if (!friendlyResource.gotComments()) {
            if (FreebaseFriendlyResource.isFromFreebase(friendlyResource)) {
                FreebaseFriendlyResource freebaseResource = FreebaseFriendlyResource.fromFriendlyResource(
                        friendlyResource
                );
                freebaseResource.getDescription(
                        resourceServiceUtils.descriptionUpdateHandler
                );
            }
        }
    }

    private void reindexGraphElement() {
        if (isVertex) {
            graphIndexer.indexVertex(
                    (VertexOperator) graphElement
            );
        } else {
            graphIndexer.indexRelation(
                    (Edge) graphElement
            );
        }
        graphIndexer.commit();
    }
}
