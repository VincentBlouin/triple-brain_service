/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import guru.bubl.module.model.User;
import guru.bubl.module.model.center_graph_element.CenterGraphElementsOperatorFactory;
import guru.bubl.module.model.json.CenterGraphElementsJson;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Produces(MediaType.APPLICATION_JSON)
public class CenterGraphElementsResource {

    private User user;

    private CenterGraphElementsOperatorFactory centerGraphElementsOperatorFactory;

    @AssistedInject
    CenterGraphElementsResource(
            CenterGraphElementsOperatorFactory centerGraphElementsOperatorFactory,
            @Assisted User user
    ){
        this.centerGraphElementsOperatorFactory = centerGraphElementsOperatorFactory;
        this.user = user;
    }

    @GET
    public Response get(){
        return Response.ok().entity(
                CenterGraphElementsJson.toJson(
                        centerGraphElementsOperatorFactory.forUser(user).get()
                )
        ).build();
    }

}
