/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.center;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import guru.bubl.module.model.User;
import guru.bubl.module.model.center_graph_element.CenterGraphElementsOperatorFactory;
import guru.bubl.module.model.json.CenterGraphElementsJson;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CenterGraphElementsResource {

    private User user;

    private CenterGraphElementsOperatorFactory centerGraphElementsOperatorFactory;

    @AssistedInject
    CenterGraphElementsResource(
            CenterGraphElementsOperatorFactory centerGraphElementsOperatorFactory,
            @Assisted User user
    ) {
        this.centerGraphElementsOperatorFactory = centerGraphElementsOperatorFactory;
        this.user = user;
    }

    @GET
    public Response get() {
        return Response.ok().entity(
                CenterGraphElementsJson.toJson(
                        centerGraphElementsOperatorFactory.forUser(user).getPublicAndPrivateWithLimitAndSkip(30, 0)
                )
        ).build();
    }

    @GET
    @Path("/page/{nbSkip}")
    public Response getPage(@PathParam("nbSkip") Integer nbSkip) {
        return Response.ok().entity(
                CenterGraphElementsJson.toJson(
                        centerGraphElementsOperatorFactory.forUser(user).getPublicAndPrivateWithLimitAndSkip(8, nbSkip)
                )
        ).build();
    }

}
