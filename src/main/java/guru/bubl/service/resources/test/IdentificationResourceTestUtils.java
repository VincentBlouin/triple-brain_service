/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.test;

import guru.bubl.module.identification.RelatedIdentificationOperator;
import guru.bubl.module.model.graph.FriendlyResourcePojo;
import guru.bubl.module.model.graph.GraphTransactional;
import guru.bubl.module.model.graph.ModelTestScenarios;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.net.URI;

@Path("/test/identification/")
@Singleton
public class IdentificationResourceTestUtils {

    @Inject
    RelatedIdentificationOperator relatedIdentificationOperator;

    @Path("relate-to-tshirt/{resourceId}")
    @GraphTransactional
    @POST
    public Response vertexWithId(@PathParam("resourceId") String resourceId){
        relatedIdentificationOperator.relateResourceToIdentification(
                new FriendlyResourcePojo(
                        URI.create(resourceId)
                ),
                new ModelTestScenarios().tShirt()
        );
        return Response.noContent().build();
    }
}
