/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.fork;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import guru.bubl.module.model.User;
import guru.bubl.module.model.graph.SubGraphJson;
import guru.bubl.module.model.graph.subgraph.SubGraphForker;
import guru.bubl.module.model.graph.subgraph.SubGraphForkerFactory;
import guru.bubl.module.model.graph.subgraph.SubGraphPojo;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

public class ForkResource {

    SubGraphForker subGraphForker;

    @AssistedInject
    public ForkResource(
            SubGraphForkerFactory subGraphForkerFactory,
            @Assisted User user
    ) {
        this.subGraphForker = subGraphForkerFactory.forUser(
                user
        );
    }

    @POST
    @Path("/")
    public Response fork(String subgraph){
        SubGraphPojo subGraphPojo = SubGraphJson.fromJson(subgraph);
        subGraphForker.fork(subGraphPojo);
        return Response.ok().build();
    }
}
