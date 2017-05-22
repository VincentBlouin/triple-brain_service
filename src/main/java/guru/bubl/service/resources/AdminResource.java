/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources;

import com.google.inject.Inject;
import guru.bubl.module.model.WholeGraph;
import guru.bubl.module.model.admin.WholeGraphAdminFactory;
import guru.bubl.module.model.graph.GraphTransactional;
import guru.bubl.module.model.search.GraphIndexer;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

public class AdminResource {

    @Inject
    protected GraphIndexer graphIndexer;

    @Inject
    protected WholeGraph wholeGraph;

    @Inject
    protected WholeGraphAdminFactory wholeGraphAdminFactory;

    @Path("reindex")
    @GraphTransactional
    @POST
    public Response reindexAll(){
        graphIndexer.indexWholeGraph();
        return Response.noContent().build();
    }

    @Path("refresh_number_of_connected_edges")
    @GraphTransactional
    @POST
    public Response refreshNumberOfConnectedEdges(){
        wholeGraphAdminFactory.withWholeGraph(
                wholeGraph
        ).refreshNumberOfConnectedEdges();
        return Response.ok().build();
    }

    @Path("refresh_identifications_nb_references")
    @GraphTransactional
    @POST
    public Response refreshAllIdentificationsNumberOfReferences(){
        wholeGraphAdminFactory.withWholeGraph(
                wholeGraph
        ).refreshNumberOfReferencesToAllIdentifications();
        return Response.ok().build();
    }

    @Path("remove_metas_having_zero_references")
    @GraphTransactional
    @POST
    public Response removeMetasHavingZeroReferences(){
        wholeGraphAdminFactory.withWholeGraph(
                wholeGraph
        ).removeMetasHavingZeroReferences();
        return Response.ok().build();
    }

    @Path("re_add_identifications")
    @GraphTransactional
    @POST
    public Response reAddIdentifications(){
        wholeGraphAdminFactory.withWholeGraph(
                wholeGraph
        ).reAddIdentifications();
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
