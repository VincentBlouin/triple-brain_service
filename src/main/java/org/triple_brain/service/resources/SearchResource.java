package org.triple_brain.service.resources;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import org.triple_brain.module.model.User;
import org.triple_brain.module.model.graph.GraphTransactional;
import org.triple_brain.module.search.GraphSearch;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/*
* Copyright Mozilla Public License 1.1
*/
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.TEXT_PLAIN)
public class SearchResource {

    @Inject
    GraphSearch graphSearch;

    private User user;

    @AssistedInject
    public SearchResource(
            @Assisted User user
    ) {
        this.user = user;
    }

    @GET
    @Path("own_vertices/auto_complete")
    @GraphTransactional
    public Response searchOwnVerticesForAutoComplete(
            @QueryParam("text") String searchText
    ) {
        return Response.ok(
                graphSearch.searchOnlyForOwnVerticesForAutoCompletionByLabel(
                        searchText,
                        user
                )).build();
    }

    @GET
    @Path("vertices/auto_complete")
    @GraphTransactional
    public Response searchVerticesForAutoComplete(
            @QueryParam("text") String searchText
    ) {
        return Response.ok(
                graphSearch.searchOwnVerticesAndPublicOnesForAutoCompletionByLabel(
                        searchText,
                        user
                )).build();
    }

    @GET
    @Path("relations/auto_complete")
    @GraphTransactional
    public Response searchRelationsForAutoComplete(
            @QueryParam("text") String searchText
    ) {
        return Response.ok(
                graphSearch.searchRelationsForAutoCompletionByLabel(
                        searchText,
                        user
                )).build();
    }
}
