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
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.json.LocalizedStringJson;
import guru.bubl.service.resources.vertex.OwnedSurroundGraphResource;
import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class IdentifierResource {

    @Inject
    IdentificationFactory identificationFactory;

    protected User authenticatedUser;
    protected UserGraph userGraph;

    @AssistedInject
    public IdentifierResource(
            @Assisted User authenticatedUser,
            @Assisted UserGraph userGraph
    ) {
        this.authenticatedUser = authenticatedUser;
        this.userGraph = userGraph;
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

    @POST
    @GraphTransactional
    @Path("/{identificationShortId}/comment")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response updateNote(
            @PathParam("identificationShortId") String identificationShortId,
            String comment
    ) {
        IdentificationOperator identificationOperator = identificationFactory.withUri(
                new UserUris(
                        authenticatedUser
                ).identificationUriFromShortId(
                        identificationShortId
                )
        );
        identificationOperator.comment(
                comment
        );
        return Response.noContent().build();
    }

    @Path("{shortId}/surround_graph")
    @GraphTransactional
    public OwnedSurroundGraphResource getVertexSurroundGraphResource(
            @PathParam("shortId") String identificationShortId
    ){
        IdentificationOperator identificationOperator = identificationFactory.withUri(
                new UserUris(
                        authenticatedUser
                ).identificationUriFromShortId(
                        identificationShortId
                )
        );
        return new OwnedSurroundGraphResource(
                userGraph,
                identificationOperator
        );
    }
}
