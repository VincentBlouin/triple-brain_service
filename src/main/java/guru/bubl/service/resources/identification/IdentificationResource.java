/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.identification;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import guru.bubl.module.model.User;
import guru.bubl.module.model.UserUris;
import guru.bubl.module.model.graph.*;
import guru.bubl.module.model.graph.identification.IdentificationFactory;
import guru.bubl.module.model.graph.identification.IdentificationOperator;
import guru.bubl.module.model.json.LocalizedStringJson;
import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class IdentificationResource {

    @Inject
    IdentificationFactory identificationFactory;

    protected User authenticatedUser;

    @AssistedInject
    public IdentificationResource(
            @Assisted User authenticatedUser
    ) {
        this.authenticatedUser = authenticatedUser;
    }

    @POST
    @GraphTransactional
    @Path("/{identificationShortId}/label")
    public Response updateLabel(
            @PathParam("identificationShortId") String identificationShortId,
            JSONObject localizedLabel
    ) {
        IdentificationOperator identificationOperator = identificationFactory.withUri(
                new UserUris(
                        authenticatedUser
                ).identificationUriFromShortId(
                        identificationShortId
                )
        );
        identificationOperator.label(
                localizedLabel.optString(
                        LocalizedStringJson.content.name()
                )
        );
        return Response.noContent().build();
    }
}
