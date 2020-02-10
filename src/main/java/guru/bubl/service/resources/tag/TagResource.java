/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.tag;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import guru.bubl.module.model.User;
import guru.bubl.module.model.UserUris;
import guru.bubl.module.model.center_graph_element.CenterGraphElementOperator;
import guru.bubl.module.model.center_graph_element.CenterGraphElementOperatorFactory;
import guru.bubl.module.model.graph.ShareLevel;
import guru.bubl.module.model.graph.tag.TagFactory;
import guru.bubl.module.model.graph.tag.TagOperator;
import guru.bubl.module.model.graph.tag.TagPojo;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.json.LocalizedStringJson;
import guru.bubl.module.model.tag.TagJson;
import guru.bubl.service.resources.vertex.OwnedSurroundGraphResource;
import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TagResource {

    @Inject
    TagFactory tagFactory;

    @javax.inject.Inject
    CenterGraphElementOperatorFactory centerGraphElementOperatorFactory;

    protected User authenticatedUser;
    protected UserGraph userGraph;

    @AssistedInject
    public TagResource(
            @Assisted User authenticatedUser,
            @Assisted UserGraph userGraph
    ) {
        this.authenticatedUser = authenticatedUser;
        this.userGraph = userGraph;
    }

    @GET
    @Path("/{identificationShortId}")
    public Response get(@PathParam("identificationShortId") String shortId) {
        TagOperator tagOperator = tagFactory.withUri(
                tagUriFromShortId(shortId)
        );
        TagPojo tagPojo = tagOperator.buildPojo();
        return Response.ok().entity(TagJson.singleToJson(tagPojo)).build();
    }


    @POST
    @Path("/{identificationShortId}/label")
    public Response updateLabel(
            @PathParam("identificationShortId") String shortId,
            JSONObject localizedLabel
    ) {
        TagOperator tagOperator = tagFactory.withUri(
                tagUriFromShortId(shortId)
        );
        tagOperator.label(
                localizedLabel.optString(
                        LocalizedStringJson.content.name()
                )
        );
        return Response.noContent().build();
    }

    @POST
    @Path("/{identificationShortId}/comment")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response updateNote(
            @PathParam("identificationShortId") String shortId,
            String comment
    ) {
        TagOperator tagOperator = tagFactory.withUri(
                tagUriFromShortId(shortId)
        );
        tagOperator.comment(
                comment
        );
        return Response.noContent().build();
    }

    @POST
    @Path("{shortId}/mergeTo/{destinationShortId}")
    public Response mergeTo(
            @PathParam("shortId") String shortId,
            @PathParam("destinationShortId") String destinationShortId
    ) {
        UserUris userUris = new UserUris(
                authenticatedUser
        );
        TagOperator tagOperator = tagFactory.withUri(
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
        tagOperator.mergeTo(
                tagFactory.withUri(destinationTagUri)
        );
        return Response.noContent().build();
    }

    @Path("{shortId}/surround_graph")
    public OwnedSurroundGraphResource getSurroundGraphResource(
            @PathParam("shortId") String shortId
    ) {
        TagOperator tagOperator = tagFactory.withUri(
                tagUriFromShortId(shortId)
        );
        CenterGraphElementOperator centerGraphElementOperator = centerGraphElementOperatorFactory.usingFriendlyResource(
                tagOperator
        );
        centerGraphElementOperator.incrementNumberOfVisits();
        centerGraphElementOperator.updateLastCenterDate();

        return new OwnedSurroundGraphResource(
                userGraph,
                tagOperator
        );
    }

    @POST
    @Path("{shortId}/childrenIndex")
    public Response saveChildrenIndexes(
            @PathParam("shortId") String shortId,
            JSONObject childrenIndexes
    ) {
        tagFactory.withUri(tagUriFromShortId(shortId)).setChildrenIndex(
                childrenIndexes.toString()
        );
        return Response.noContent().build();
    }

    @POST
    @Path("{shortId}/colors")
    public Response saveColors(
            @PathParam("shortId") String shortId,
            JSONObject colors
    ) {
        tagFactory.withUri(tagUriFromShortId(shortId)).setColors(
                colors.toString()
        );
        return Response.noContent().build();
    }

    @POST
    @Path("{shortId}/font")
    public Response saveFont(
            @PathParam("shortId") String shortId,
            JSONObject font
    ) {
        tagFactory.withUri(tagUriFromShortId(shortId)).setFont(
                font.toString()
        );
        return Response.noContent().build();
    }



    @Path("{shortId}/shareLevel")
    @POST
    public Response setShareLevel(@PathParam("shortId") String shortId, JSONObject shareLevel) {
        tagFactory.withUri(tagUriFromShortId(shortId)).setShareLevel(
                ShareLevel.valueOf(
                        shareLevel.optString("shareLevel", ShareLevel.PRIVATE.name()).toUpperCase()
                )
        );
        return Response.noContent().build();
    }

    private URI tagUriFromShortId(String shortId) {
        return new UserUris(
                authenticatedUser
        ).identificationUriFromShortId(
                shortId
        );
    }

}
