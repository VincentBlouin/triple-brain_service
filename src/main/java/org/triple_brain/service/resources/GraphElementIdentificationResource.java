package org.triple_brain.service.resources;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import org.codehaus.jettison.json.JSONObject;
import org.triple_brain.module.model.*;
import org.triple_brain.module.model.graph.GraphElement;
import org.triple_brain.service.ResourceServiceUtils;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.net.URI;

import static org.triple_brain.module.common_utils.Uris.decodeURL;

/*
* Copyright Mozilla Public License 1.1
*/
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GraphElementIdentificationResource {
    public static enum identification_types {
        SAME_AS, TYPE
    }

    public static final String IDENTIFICATION_TYPE_STRING = "type";

    @Inject
    FriendlyResourceFactory friendlyResourceFactory;

    @Inject
    ResourceServiceUtils resourceServiceUtils;

    @Inject
    BeforeAfterEachRestCall beforeAfterEachRestCall;

    private GraphElement graphElement;

    @AssistedInject
    public GraphElementIdentificationResource(
            @Assisted GraphElement graphElement
    ) {
        this.graphElement = graphElement;
    }

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/")
    public Response add(JSONObject identification) {
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
        } else {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        FriendlyResourceCached friendlyResourceCached = FriendlyResourceCached.fromFriendlyResource(
                friendlyResource
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
    @Produces(MediaType.TEXT_PLAIN)
    @Path("{friendly_resource_uri}")
    public Response removeFriendlyResource(
            @PathParam("friendly_resource_uri") String friendlyResourceUri
    ) {
        try {
            friendlyResourceUri = decodeURL(friendlyResourceUri);
        } catch (UnsupportedEncodingException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        FriendlyResource friendlyResource = friendlyResourceFactory.createOrLoadFromUri(
                URI.create(friendlyResourceUri)
        );
        graphElement.removeIdentification(friendlyResource);
        return Response.ok().build();
    }

    private void updateImagesOfExternalResourceIfNecessary(FriendlyResourceCached friendlyResource) {
        if (!friendlyResource.gotTheImages()) {
            if (FreebaseFriendlyResource.isFromFreebase(friendlyResource)) {
                FreebaseFriendlyResource freebaseResource = FreebaseFriendlyResource.fromFriendlyResource(
                        friendlyResource
                );
                freebaseResource.getImages(
                        resourceServiceUtils.imagesUpdateHandler
                );
            }
        }
    }

    private void updateDescriptionOfExternalResourceIfNecessary(FriendlyResourceCached friendlyResource) {
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
}
