/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.meta;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import guru.bubl.module.model.User;
import guru.bubl.module.model.UserUris;
import guru.bubl.module.model.center_graph_element.CenterGraphElementOperator;
import guru.bubl.module.model.center_graph_element.CenterGraphElementOperatorFactory;
import guru.bubl.module.model.graph.GraphTransactional;
import guru.bubl.module.model.graph.identification.IdentificationFactory;
import guru.bubl.module.model.graph.identification.IdentificationOperator;
import guru.bubl.module.model.graph.identification.IdentifierPojo;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.json.LocalizedStringJson;
import guru.bubl.module.model.meta.MetaJson;
import guru.bubl.service.resources.vertex.OwnedSurroundGraphResource;
import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class IdentifierResource {

    @Inject
    IdentificationFactory identificationFactory;

    @javax.inject.Inject
    CenterGraphElementOperatorFactory centerGraphElementOperatorFactory;

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

    @GET
    @GraphTransactional
    @Path("/{identificationShortId}")
    public Response get(@PathParam("identificationShortId") String identificationShortId){
        IdentificationOperator identificationOperator = identificationFactory.withUri(
                new UserUris(
                        authenticatedUser
                ).identificationUriFromShortId(
                        identificationShortId
                )
        );
        IdentifierPojo identifierPojo = identificationOperator.buildPojo();
        return Response.ok().entity(MetaJson.singleToJson(identifierPojo)).build();
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

    @POST
    @Path("{shortId}/mergeTo/{destinationShortId}")
    @GraphTransactional
    public Response mergeTo(
            @PathParam("shortId") String shortId,
            @PathParam("destinationShortId") String destinationShortId
    ) {
        UserUris userUris = new UserUris(
                authenticatedUser
        );
        IdentificationOperator identificationOperator = identificationFactory.withUri(
                userUris.identificationUriFromShortId(
                        shortId
                )
        );
        URI destinationTagUri = userUris.identificationUriFromShortId(destinationShortId);
        if (!userGraph.haveElementWithId(destinationTagUri)) {
            return Response.status(
                    Response.Status.BAD_REQUEST
            ).build();
        }
        identificationOperator.mergeTo(
                identificationFactory.withUri(destinationTagUri)
        );
        return Response.noContent().build();
    }

    @Path("{shortId}/surround_graph")
    @GraphTransactional
    public OwnedSurroundGraphResource getSurroundGraphResource(
            @PathParam("shortId") String identificationShortId
    ){
        IdentificationOperator identificationOperator = identificationFactory.withUri(
                new UserUris(
                        authenticatedUser
                ).identificationUriFromShortId(
                        identificationShortId
                )
        );
        CenterGraphElementOperator centerGraphElementOperator = centerGraphElementOperatorFactory.usingFriendlyResource(
                identificationOperator
        );
        centerGraphElementOperator.incrementNumberOfVisits();
        centerGraphElementOperator.updateLastCenterDate();

        return new OwnedSurroundGraphResource(
                userGraph,
                identificationOperator
        );
    }
}
