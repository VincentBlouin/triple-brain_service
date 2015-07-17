/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources;

import com.google.inject.Inject;
import guru.bubl.module.model.WholeGraph;
import guru.bubl.module.model.admin.WholeGraphAdmin;
import guru.bubl.module.model.graph.GraphTransactional;
import guru.bubl.module.model.search.GraphIndexer;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

public class AdminResource {

    @Inject
    GraphIndexer graphIndexer;

    @Inject
    WholeGraph wholeGraph;

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
        new WholeGraphAdmin(
                wholeGraph
        ).refreshNumberOfConnectedEdges();
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
