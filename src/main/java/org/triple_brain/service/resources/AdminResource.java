package org.triple_brain.service.resources;

import com.google.inject.Inject;
import org.triple_brain.module.model.WholeGraph;
import org.triple_brain.module.model.admin.WholeGraphAdmin;
import org.triple_brain.module.model.graph.GraphTransactional;
import org.triple_brain.module.search.GraphIndexer;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/*
* Copyright Mozilla Public License 1.1
*/
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
        return Response.ok().build();
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
