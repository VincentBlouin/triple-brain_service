/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources;

import com.google.gson.Gson;
import guru.bubl.module.model.graph.GraphTransactional;
import guru.bubl.module.search.GraphElementSearchResult;
import guru.bubl.module.search.GraphSearch;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

@Path("/search")
@Singleton
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.TEXT_PLAIN)
public class PublicSearchResource {

    private Gson gson = new Gson();

    @Inject
    GraphSearch graphSearch;

    @GET
    @Path("/")
    @GraphTransactional
    public Response search(@QueryParam("text") String searchText) {
        return Response.ok(
                gson.toJson(
                        graphSearch.searchPublicVerticesOnly(
                                searchText
                        )
                )
        ).build();
    }

    @GET
    @Path("/details")
    @GraphTransactional
    public Response searchDetails(@QueryParam("uri") String uri) {
        GraphElementSearchResult searchResult = graphSearch.getDetailsAnonymously(
                URI.create(uri)
        );
        if(null == searchResult){
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
        return Response.ok(
                gson.toJson(
                        searchResult
                )
        ).build();
    }
}
