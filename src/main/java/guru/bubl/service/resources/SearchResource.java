/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources;

import com.google.gson.Gson;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import guru.bubl.module.common_utils.NoEx;
import guru.bubl.module.model.User;
import guru.bubl.module.model.search.GraphSearch;
import org.codehaus.jettison.json.JSONObject;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
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

    @POST
    @Path("own_all_resource/auto_complete")
    public Response searchAllOwnResourcesForAutoComplete(
            JSONObject options
    ) {
        return Response.ok(
                gson.toJson(
                        graphSearch.searchForAllOwnResources(
                                options.optString("searchText"),
                                user
                        )
                )
        ).build();
    }

    @POST
    @Path("own_vertices/auto_complete")
    public Response searchOwnVerticesForAutoComplete(
            JSONObject options
    ) {
        return NoEx.wrap(() -> Response.ok(
                gson.toJson(
                        graphSearch.searchOnlyForOwnVerticesForAutoCompletionByLabel(
                                options.getString("searchText"),
                                user
                        )
                )).build()
        ).get();
    }

    @POST
    @Path("own_tags/auto_complete")
    public Response searchOwnTagsForAutoComplete(
            JSONObject options
    ) {
        return NoEx.wrap(() -> Response.ok(
                gson.toJson(
                        graphSearch.searchOwnTagsForAutoCompletionByLabel(
                                options.getString("searchText"),
                                user
                        )
                )).build()
        ).get();
    }

    @POST
    @Path("own_vertices_and_schemas/auto_complete")
    public Response searchOwnVerticesAndSchemasForAutoComplete(
            JSONObject options
    ) {
        return NoEx.wrap(() -> Response.ok(
                gson.toJson(
                        graphSearch.searchOnlyForOwnVerticesOrSchemasForAutoCompletionByLabel(
                                options.getString("searchText"),
                                user
                        )
                )).build()
        ).get();
    }

    @POST
    @Path("vertices/auto_complete")
    public Response searchVerticesForAutoComplete(
            JSONObject options
    ) {
        return NoEx.wrap(() -> Response.ok(
                gson.toJson(
                        graphSearch.searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                                options.getString("searchText"),
                                user
                        ))).build()
        ).get();
    }

    @POST
    @Path("relations/auto_complete")
    public Response searchRelationsForAutoComplete(
            JSONObject options
    ) {
        return NoEx.wrap(() -> Response.ok(
                gson.toJson(
                        graphSearch.searchRelationsPropertiesSchemasForAutoCompletionByLabel(
                                options.getString("searchText"),
                                user
                        ))).build()
        ).get();
    }

    @GET
    @Path("details")
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
