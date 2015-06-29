/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources;

import guru.bubl.module.model.graph.FriendlyResourcePojo;
import guru.bubl.module.model.graph.ModelTestScenarios;
import guru.bubl.service.utils.GraphManipulationRestTestUtils;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertFalse;
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
}
