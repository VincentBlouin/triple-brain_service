/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import guru.bubl.module.model.WholeGraph;
import guru.bubl.module.model.admin.WholeGraphAdmin;
import guru.bubl.module.model.graph.GraphTransactional;
import guru.bubl.module.model.search.GraphIndexer;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

public class AdminResource {

    @Inject
    protected GraphIndexer graphIndexer;

    @Inject
    protected WholeGraphAdmin wholeGraphAdmin;

    @Path("reindex")
    @GraphTransactional
    @POST
    public Response reindexAll(){
        wholeGraphAdmin.reindexAll();
        return Response.noContent().build();
    }

    @Path("refresh_number_of_connected_edges")
    @GraphTransactional
    @POST
    public Response refreshNumberOfConnectedEdges(){
        wholeGraphAdmin.refreshNumberOfConnectedEdges();
        return Response.ok().build();
    }

    @Path("refresh_identifications_nb_references")
    @GraphTransactional
    @POST
    public Response refreshAllIdentificationsNumberOfReferences(){
        wholeGraphAdmin.refreshNumberOfReferencesToAllIdentifications();
        return Response.ok().build();
    }

    @Path("remove_metas_having_zero_references")
    @GraphTransactional
    @POST
    public Response removeMetasHavingZeroReferences(){
        wholeGraphAdmin.removeMetasHavingZeroReferences();
        return Response.ok().build();
    }

    @Path("re_add_identifications")
    @GraphTransactional
    @POST
    public Response reAddIdentifications(){
        wholeGraphAdmin.reAddIdentifications();
        return Response.ok().build();
    }

//    @Path("convert_small_images_to_base_64")
//    @GraphTransactional
//    @POST
//    public Response convertSmallImagesToBase64(){
//        new WholeGraphAdmin(
//                wholeGraph
//        ).convertAllSmallImagesToBase64();
//        return Response.ok().build();
//    }

}
