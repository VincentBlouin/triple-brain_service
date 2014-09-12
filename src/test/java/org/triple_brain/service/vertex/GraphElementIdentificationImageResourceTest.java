/*
 * Copyright Vincent Blouin under the Mozilla Public License 1.1
 */

package org.triple_brain.service.vertex;

import com.sun.jersey.api.client.ClientResponse;
import org.junit.Test;
import org.triple_brain.module.model.Image;
import org.triple_brain.module.model.graph.Identification;
import org.triple_brain.module.model.json.ImageJson;
import org.triple_brain.service.utils.GraphManipulationRestTest;

import javax.ws.rs.core.MediaType;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GraphElementIdentificationImageResourceTest extends GraphManipulationRestTest {

    @Test
    public void can_add_image() {
        ClientResponse response = graphElementUtils().addFoafPersonTypeToVertexA();
        Identification identification = vertexA().getIdentifications().values().iterator().next();
        assertTrue(
                identification.images().isEmpty()
        );
        addImageToIdentificationWithUriForVertexA(
                graphElementUtils().identificationUriFromResponse(
                        response
                )
        );
        identification = vertexA().getIdentifications().values().iterator().next();
        assertFalse(
                identification.images().isEmpty()
        );
    }

    private ClientResponse addImageToIdentificationWithUriForVertexA(URI uri) {
        Image image = Image.withBase64ForSmallAndUriForBigger(
                "dummy base 64",
                URI.create("http://example.org/big_image")
        );
        Set<Image> images = new HashSet<>();
        images.add(image);
        return resource
                .path(vertexAUri().getPath())
                .path("identification")
                .path("image")
                .queryParam("uri", uri.getPath())
                .cookie(authCookie)
                .type(MediaType.APPLICATION_JSON)
                .post(
                        ClientResponse.class,
                        ImageJson.toJsonArray(images)
                );
    }

}
