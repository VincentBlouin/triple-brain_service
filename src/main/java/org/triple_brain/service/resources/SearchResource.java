/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package org.triple_brain.service.resources;

import com.google.gson.Gson;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import org.triple_brain.module.model.User;
import org.triple_brain.module.model.graph.GraphTransactional;
import org.triple_brain.module.search.GraphSearch;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.TEXT_PLAIN)
public class SearchResource {

    @Inject
    GraphSearch graphSearch;

    private User user;

    private Gson gson = new Gson();

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
                gson.toJson(
                        graphSearch.searchOnlyForOwnVerticesForAutoCompletionByLabel(
                                searchText,
                                user
                        )
                )).build();
    }
    @GET
    @Path("own_vertices_and_schemas/auto_complete")
    @GraphTransactional
    public Response searchOwnVerticesAndSchemasForAutoComplete(
            @QueryParam("text") String searchText
    ) {
        return Response.ok(
                gson.toJson(
                        graphSearch.searchOnlyForOwnVerticesOrSchemasForAutoCompletionByLabel(
                                searchText,
                                user
                        )
                )).build();
    }

    @GET
    @Path("vertices/auto_complete")
    @GraphTransactional
    public Response searchVerticesForAutoComplete(
            @QueryParam("text") String searchText
    ) {
        return Response.ok(gson.toJson(
                graphSearch.searchSchemasOwnVerticesAndPublicOnesForAutoCompletionByLabel(
                        searchText,
                        user
                ))).build();
    }

    @GET
    @Path("relations/auto_complete")
    @GraphTransactional
    public Response searchRelationsForAutoComplete(
            @QueryParam("text") String searchText
    ) {
        return Response.ok(gson.toJson(
                graphSearch.searchRelationsPropertiesOrSchemasForAutoCompletionByLabel(
                        searchText,
                        user
                ))).build();
    }

    @GET
    @Path("details")
    @GraphTransactional
    public Response getDetails(
            @QueryParam("uri") String uri
    ) {
        return Response.ok(gson.toJson(
                graphSearch.getDetails(
                        URI.create(uri),
                        user
                )
        )).build();
    }
}
