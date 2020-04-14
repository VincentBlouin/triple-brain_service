/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.tag;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import guru.bubl.module.model.User;
import guru.bubl.module.model.UserUris;
import guru.bubl.module.model.center_graph_element.CenterGraphElementOperatorFactory;
import guru.bubl.module.model.graph.GraphElementOperator;
import guru.bubl.module.model.graph.ShareLevel;
import guru.bubl.module.model.graph.fork.ForkOperator;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.graph.tag.TagFactory;
import guru.bubl.module.model.graph.tag.TagOperator;
import guru.bubl.module.model.graph.tag.TagPojo;
import guru.bubl.module.model.json.LocalizedStringJson;
import guru.bubl.module.model.tag.TagJson;
import guru.bubl.service.resources.fork.ForkResource;
import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TagResource extends ForkResource {

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

    @Override
    public Response addVertexAndEdgeToSourceVertex(String sourceVertexShortId, JSONObject options) {
        throw new WebApplicationException(Response.Status.BAD_REQUEST);
    }

    @GET
    @Path("/{identificationShortId}")
    public Response get(@PathParam("identificationShortId") String shortId) {
        TagOperator tagOperator = tagFactory.withUri(
                getUriFromShortId(shortId)
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
                getUriFromShortId(shortId)
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
                getUriFromShortId(shortId)
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

    @Path("{shortId}/shareLevel")
    @POST
    public Response setShareLevel(@PathParam("shortId") String shortId, JSONObject shareLevel) {
        tagFactory.withUri(getUriFromShortId(shortId)).setShareLevel(
                ShareLevel.valueOf(
                        shareLevel.optString("shareLevel", ShareLevel.PRIVATE.name()).toUpperCase()
                )
        );
        return Response.noContent().build();
    }

    @Override
    protected URI getUriFromShortId(String shortId) {
        return new UserUris(
                authenticatedUser
        ).identificationUriFromShortId(
                shortId
        );
    }

    @Override
    protected GraphElementOperator getOperatorFromShortId(String shortId) {
        return tagFactory.withUri(getUriFromShortId(shortId));
    }

    @Override
    protected ForkOperator getForkOperatorFromURI(URI uri) {
        return tagFactory.withUri(uri);
    }
}
