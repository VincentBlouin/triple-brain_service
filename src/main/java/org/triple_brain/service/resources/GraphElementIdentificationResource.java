package org.triple_brain.service.resources;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import org.codehaus.jettison.json.JSONObject;
import org.triple_brain.module.model.FreebaseFriendlyResource;
import org.triple_brain.module.model.FriendlyResource;
import org.triple_brain.module.model.FriendlyResourceFactory;
import org.triple_brain.module.model.graph.FriendlyResourcePojo;
import org.triple_brain.module.model.graph.GraphElementOperator;
import org.triple_brain.module.model.graph.GraphTransactional;
import org.triple_brain.module.model.graph.edge.Edge;
import org.triple_brain.module.model.graph.vertex.VertexOperator;
import org.triple_brain.module.model.json.FriendlyResourceJson;
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
    @GraphTransactional
    @Path("/")
    public Response add(JSONObject identificationJson) {
        FriendlyResourceValidator validator = new FriendlyResourceValidator();
        FriendlyResourcePojo identification = FriendlyResourceJson.fromJson(
                identificationJson
        );
        if (!validator.validate(identification).isEmpty()) {
            throw new WebApplicationException(
                    Response.Status.NOT_ACCEPTABLE
            );
        }
        String type = identificationJson.optString("type");
        if (type.equalsIgnoreCase(identification_types.SAME_AS.name())) {
            identification = graphElement.addSameAs(
                    identification
            );
        } else if (type.equalsIgnoreCase(identification_types.TYPE.name())) {
            identification = graphElement.addType(
                    identification
            );
        } else if (type.equalsIgnoreCase(identification_types.GENERIC.name())) {
            identification = graphElement.addGenericIdentification(
                    identification
            );
        } else {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        reindexGraphElement();
        updateImagesOfExternalResourceIfNecessary(
                identification
        );
        updateDescriptionOfExternalResourceIfNecessary(
                identification
        );
        return Response.noContent().build();
    }

    @DELETE
    @GraphTransactional
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/")
    public Response removeFriendlyResource(
            @QueryParam("uri") String identificationUri
    ) {
        FriendlyResource friendlyResource = friendlyResourceFactory.withUri(
                URI.create(identificationUri)
        );
        graphElement.removeIdentification(friendlyResource);
        reindexGraphElement();
        return Response.ok().build();
    }

    private void updateImagesOfExternalResourceIfNecessary(FriendlyResourcePojo friendlyResource) {
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

    private void updateDescriptionOfExternalResourceIfNecessary(FriendlyResourcePojo friendlyResource) {
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
