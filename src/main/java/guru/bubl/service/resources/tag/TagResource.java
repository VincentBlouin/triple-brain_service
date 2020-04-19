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
import guru.bubl.module.model.graph.graph_element.GraphElementOperator;
import guru.bubl.module.model.graph.fork.ForkOperator;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.graph.tag.TagFactory;
import guru.bubl.module.model.graph.tag.TagOperator;
import guru.bubl.module.model.graph.tag.TagPojo;
import guru.bubl.module.model.tag.TagJson;
import guru.bubl.service.resources.fork.ForkResource;
import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TagResource implements ForkResource {

    @Inject
    TagFactory tagFactory;

    @Inject
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

    @Override
    public URI getUriFromShortId(String shortId) {
        return new UserUris(
                authenticatedUser
        ).identificationUriFromShortId(
                shortId
        );
    }

    @Override
    public GraphElementOperator getOperatorFromShortId(String shortId) {
        return tagFactory.withUri(getUriFromShortId(shortId));
    }

    @Override
    public UserGraph getUserGraph() {
        return userGraph;
    }

    @Override
    public CenterGraphElementOperatorFactory getCenterOperatorFactory() {
        return centerGraphElementOperatorFactory;
    }

    @Override
    public ForkOperator getForkOperatorFromURI(URI uri) {
        return tagFactory.withUri(uri);
    }
}
