/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.center;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import guru.bubl.module.model.User;
import guru.bubl.module.model.center_graph_element.CenterGraphElementsOperatorFactory;
import guru.bubl.module.model.center_graph_element.CenteredGraphElementsOperator;
import guru.bubl.module.model.json.CenterGraphElementsJson;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/center-elements/public")
@Singleton
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PublicCenterGraphElementsResource {

    @Inject
    private CenterGraphElementsOperatorFactory centerGraphElementsOperatorFactory;

    @GET
    public Response get() {
        return this.getAtSkip(null);
    }

    @GET
    @Path("/skip/{nbSkip}")
    public Response getAtSkip(@PathParam("nbSkip") Integer nbSkip) {
        return Response.ok().entity(
                CenterGraphElementsJson.toJson(
                        getFromNbSkip(nbSkip).getAllPublic()
                )
        ).build();
    }

    @GET
    @Path("/user/{username}")
    public Response getForSpecificUser(@PathParam("username") String username) {
        return this.getForSpecificUserAtSkip(username, null);
    }

    @GET
    @Path("/user/{username}/skip/{nbSkip}")
    public Response getForSpecificUserAtSkip(@PathParam("username") String username, @PathParam("nbSkip") Integer nbSkip) {
        User user = User.withUsername(username);
        return Response.ok().entity(
                CenterGraphElementsJson.toJson(
                        getFromNbSkip(nbSkip).getPublicOfUser(user)
                )
        ).build();
    }

    @GET
    @Path("/pattern")
    public Response getPatterns() {
        return this.getPatternsAtSkip(null);
    }

    @GET
    @Path("/pattern/skip/{nbSkip}")
    public Response getPatternsAtSkip(@PathParam("nbSkip") Integer nbSkip) {
        return Response.ok().entity(
                CenterGraphElementsJson.toJson(
                        getFromNbSkip(nbSkip).getAllPatterns()
                )
        ).build();
    }

    private CenteredGraphElementsOperator getFromNbSkip(Integer nbSkip) {
        return nbSkip == null ?
                centerGraphElementsOperatorFactory.usingDefaultLimits() :
                centerGraphElementsOperatorFactory.usingLimitAndSkip(16, nbSkip);
    }
}
