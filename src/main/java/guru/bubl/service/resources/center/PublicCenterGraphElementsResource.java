/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.center;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import guru.bubl.module.model.User;
import guru.bubl.module.model.center_graph_element.CenterGraphElementsOperatorFactory;
import guru.bubl.module.model.json.CenterGraphElementsJson;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

public class PublicCenterGraphElementsResource {

    private User user;

    private CenterGraphElementsOperatorFactory centerGraphElementsOperatorFactory;

    @AssistedInject
    PublicCenterGraphElementsResource(
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
                        centerGraphElementsOperatorFactory.forUser(
                                user
                        ).getPublicOnlyOfTypeWithLimitAndSkip(30, 0)
                )
        ).build();
    }

    @GET
    @Path("/skip/{nbSkip}")
    public Response getPage(@PathParam("nbSkip") Integer nbSkip) {
        return Response.ok().entity(
                CenterGraphElementsJson.toJson(
                        centerGraphElementsOperatorFactory.forUser(user).getPublicOnlyOfTypeWithLimitAndSkip(8, nbSkip)
                )
        ).build();
    }
}
