/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.identification;

import com.google.gson.Gson;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import guru.bubl.module.model.IdentifiedTo;
import guru.bubl.module.model.User;
import guru.bubl.module.model.graph.FriendlyResourcePojo;
import guru.bubl.module.model.graph.GraphElement;
import guru.bubl.module.model.graph.GraphTransactional;
import guru.bubl.module.model.graph.IdentificationPojo;
import guru.bubl.module.model.json.FriendlyResourceJson;
import guru.bubl.module.model.json.graph.GraphElementJson;
import guru.bubl.module.model.search.GraphElementSearchResult;

import javax.inject.Inject;
import javax.naming.directory.SearchResult;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Set;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class IdentifiedToResource {

    private static Gson gson = new Gson();

    protected User authenticatedUser;

    @Inject
    IdentifiedTo identifiedTo;

    @AssistedInject
    public IdentifiedToResource(
            @Assisted User authenticatedUser
    ) {
        this.authenticatedUser = authenticatedUser;
    }

    @GET
    @GraphTransactional
    @Path("/{identificationUri}")
    public Response get(@PathParam("identificationUri") String identificationUri) {
        Set<GraphElementSearchResult> relatedResources = identifiedTo.getForIdentificationAndUser(
                new IdentificationPojo(
                        URI.create(identificationUri)
                ),
                authenticatedUser
        );
        return Response.ok().entity(
                gson.toJson(
                        relatedResources
                )
        ).build();
    }
}
