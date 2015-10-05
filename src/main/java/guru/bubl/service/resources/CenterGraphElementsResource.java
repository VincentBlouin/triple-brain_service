/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import guru.bubl.module.model.User;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Produces(MediaType.APPLICATION_JSON)
public class CenterGraphElementsResource {

    private User user;

    private CenterGraphElementsResourceFactory centerGraphElementsResourceFactory;

    @AssistedInject
    CenterGraphElementsResource(
            CenterGraphElementsResourceFactory centerGraphElementsResourceFactory,
            @Assisted User user
    ){
        this.centerGraphElementsResourceFactory = centerGraphElementsResourceFactory;
        this.user = user;
    }

    @GET
    public Response get(){
        return Response.ok().build();
    }

}
