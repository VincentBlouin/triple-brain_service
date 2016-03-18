/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources;

import com.sun.jersey.api.client.ClientResponse;
import guru.bubl.module.common_utils.Uris;
import guru.bubl.module.model.graph.Identification;
import guru.bubl.module.model.json.LocalizedStringJson;
import guru.bubl.service.utils.GraphManipulationRestTestUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Ignore;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IdentificationResourceTest extends GraphManipulationRestTestUtils {

    @Test
    public void updating_label_returns_no_content_status() {
        assertThat(
                vertexA().getAdditionalTypes().size(),
                is(0)
        );

        graphElementUtils().addFoafPersonTypeToVertexA();
        Identification identification = vertexA().getAdditionalTypes().values().iterator().next();
        ClientResponse clientResponse = updateIdentificationLabel(
                identification,
                "new label"
        );
        assertThat(
                clientResponse.getStatus(),
                is(Response.Status.NO_CONTENT.getStatusCode())
        );
    }

    @Test
    public void can_update_identification_label() {
        assertThat(
                vertexA().getAdditionalTypes().size(),
                is(0)
        );
        graphElementUtils().addFoafPersonTypeToVertexA();
        Identification identification = vertexA().getAdditionalTypes().values().iterator().next();
        assertFalse(
                identification.label().equals("new label")
        );
        updateIdentificationLabel(
                identification,
                "new label"
        );
        identification = vertexA().getAdditionalTypes().values().iterator().next();
        assertTrue(
                identification.label().equals(
                        "new label"
                )
        );
    }

    @Test
    public void cannot_update_identification_label_of_another_user() {
        graphElementUtils().addFoafPersonTypeToVertexA();
        Identification identification = vertexA().getAdditionalTypes().values().iterator().next();
        JSONObject anotherUser = createAUser();
        authenticate(
                anotherUser
        );
        ClientResponse response = updateIdentificationLabel(
                identification,
                "new label"
        );
        assertThat(
                response.getStatus(),
                is(Response.Status.FORBIDDEN.getStatusCode())
        );
        authenticate(
                defaultAuthenticatedUser
        );
        identification = vertexA().getAdditionalTypes().values().iterator().next();
        assertFalse(
                identification.label().equals(
                        "new label"
                )
        );
    }

    private ClientResponse updateIdentificationLabel(Identification identification, String label) {
        try {
            JSONObject localizedLabel = new JSONObject().put(
                    LocalizedStringJson.content.name(),
                    label
            );
            return resource
                    .path(identification.uri().getPath())
                    .path("label")
                    .cookie(authCookie)
                    .post(ClientResponse.class, localizedLabel);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
