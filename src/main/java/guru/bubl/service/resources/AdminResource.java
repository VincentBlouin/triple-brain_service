/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources;

import com.google.inject.Inject;
import guru.bubl.module.model.admin.WholeGraphAdmin;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

public class AdminResource {

    @Inject
    protected WholeGraphAdmin wholeGraphAdmin;

    @Path("reindex")
    @POST
    public Response reindexAll(){
        wholeGraphAdmin.reindexAll();
        return Response.noContent().build();
    }

    @Path("refresh_number_of_connected_edges")
    @POST
    public Response refreshNumberOfConnectedEdges(){
        wholeGraphAdmin.refreshNbNeighbors();
        return Response.ok().build();
    }

    @Path("refresh_identifications_nb_references")
    @POST
    public Response refreshAllIdentificationsNumberOfReferences(){
        wholeGraphAdmin.refreshNbNeighborsToAllTags();
        return Response.ok().build();
    }

}
