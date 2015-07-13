/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.identification;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import guru.bubl.module.identification.RelatedIdentificationOperator;
import guru.bubl.module.model.User;
import guru.bubl.module.model.graph.FriendlyResourcePojo;
import guru.bubl.module.model.graph.GraphTransactional;
import guru.bubl.module.model.graph.IdentificationPojo;
import guru.bubl.module.model.json.FriendlyResourceJson;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Set;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class IdentificationResource {

    protected User authenticatedUser;

    @AssistedInject
    public IdentificationResource(
            @Assisted User authenticatedUser
    ) {
        this.authenticatedUser = authenticatedUser;
    }

    @GET
    @GraphTransactional
    @Path("/{identificationUri}")
    public Response get(@PathParam("identificationUri") String identificationUri) {
        Set<FriendlyResourcePojo> relatedResources = relatedIdentificationOperator.getResourcesRelatedToIdentificationForUser(
                new IdentificationPojo(
                        URI.create(identificationUri)
                ),
                authenticatedUser
        );
        return Response.ok().entity(
                FriendlyResourceJson.multipleToJson(
                        relatedResources
                )
        ).build();
    }
}
