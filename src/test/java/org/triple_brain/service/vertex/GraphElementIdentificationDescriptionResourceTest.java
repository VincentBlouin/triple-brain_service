package org.triple_brain.service.vertex;

import com.sun.jersey.api.client.ClientResponse;
import org.junit.Test;
import org.triple_brain.module.model.graph.Identification;
import org.triple_brain.service.utils.GraphManipulationRestTest;

import javax.ws.rs.core.MediaType;
import java.net.URI;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GraphElementIdentificationDescriptionResourceTest extends GraphManipulationRestTest {

    @Test
    public void can_set_description() {
        ClientResponse response = graphElementUtils().addFoafPersonTypeToVertexA();
        Identification identification = vertexA().getIdentifications().values().iterator().next();
        assertTrue(
                identification.comment().isEmpty()
        );
        setDescriptionToIdentificationWithUriForVertexA(
                graphElementUtils().identificationUriFromResponse(
                        response
                )
        );
        identification = vertexA().getIdentifications().values().iterator().next();
        assertFalse(
                identification.comment().isEmpty()
        );
    }

    private ClientResponse setDescriptionToIdentificationWithUriForVertexA(URI uri) {

        return resource
                .path(vertexAUri().getPath())
                .path("identification")
                .path("description")
                .queryParam("uri", uri.getPath())
                .cookie(authCookie)
                .type(MediaType.APPLICATION_JSON)
                .put(
                        ClientResponse.class,
                        "Dummy description"
                );

    }
}
