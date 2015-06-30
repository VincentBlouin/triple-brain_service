/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources;

import com.sun.jersey.api.client.ClientResponse;
import guru.bubl.module.model.graph.FriendlyResourcePojo;
import guru.bubl.module.model.graph.ModelTestScenarios;
import guru.bubl.module.model.graph.vertex.Vertex;
import guru.bubl.service.utils.GraphManipulationRestTestUtils;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class IdentificationResourceTest extends GraphManipulationRestTestUtils{

    @Test
    public void can_get_related_resources(){
        Set<FriendlyResourcePojo> relatedResources = identificationUtils().getRelatedResourcesForIdentification(
                new ModelTestScenarios().tShirt()
        );
        assertTrue(
                relatedResources.isEmpty()
        );
        identificationUtils().relateResourceToTshirt(
                vertexA()
        );
        relatedResources = identificationUtils().getRelatedResourcesForIdentification(
                new ModelTestScenarios().tShirt()
        );
        assertFalse(
                relatedResources.isEmpty()
        );
    }

    @Test
    public void cannot_get_related_resources_of_another_user(){
        Vertex aVertex = vertexA();
        identificationUtils().relateResourceToTshirt(
                aVertex
        );
        String defaultAuthenticatedUserUsername = defaultAuthenticatedUser.username();
        ClientResponse response = identificationUtils().getRelatedResourcesForIdentificationClientResponseForUsername(
                new ModelTestScenarios().tShirt(),
                defaultAuthenticatedUserUsername
        );
        assertThat(
                response.getStatus(),
                is(Response.Status.OK.getStatusCode())
        );
        authenticate(
                createAUser()
        );
        response = identificationUtils().getRelatedResourcesForIdentificationClientResponseForUsername(
                new ModelTestScenarios().tShirt(),
                defaultAuthenticatedUserUsername
        );
        assertThat(
                response.getStatus(),
                is(Response.Status.FORBIDDEN.getStatusCode())
        );
    }
}
