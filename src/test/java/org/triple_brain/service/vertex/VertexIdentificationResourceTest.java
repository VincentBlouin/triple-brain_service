package org.triple_brain.service.vertex;

import com.sun.jersey.api.client.ClientResponse;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;
import org.triple_brain.module.model.ExternalFriendlyResource;
import org.triple_brain.module.model.ModelTestScenarios;
import org.triple_brain.module.model.json.ExternalResourceJson;
import org.triple_brain.module.model.json.graph.VertexJsonFields;
import org.triple_brain.service.resources.vertex.VertexIdentificationResource;
import org.triple_brain.service.utils.GraphManipulationRestTest;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.triple_brain.module.common_utils.Uris.encodeURL;

/*
* Copyright Mozilla Public License 1.1
*/
public class VertexIdentificationResourceTest extends GraphManipulationRestTest {

    @Test
    public void setting_type_of_a_vertex_returns_correct_response_status() throws Exception {
        ClientResponse response = addFoafPersonTypeToVertexA();
        assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
    }

    @Test
    public void can_add_an_additional_type_to_vertex() throws Exception {
        JSONArray additionalTypes = vertexA().getJSONArray(VertexJsonFields.TYPES);
        assertThat(
                additionalTypes.length(),
                is(0)
        );
        addFoafPersonTypeToVertexA();
        additionalTypes = vertexA().getJSONArray(VertexJsonFields.TYPES);
        assertThat(
                additionalTypes.length(),
                is(greaterThan(0))
        );
    }

    @Test
    public void can_remove_the_additional_type_of_vertex() throws Exception {
        addFoafPersonTypeToVertexA();
        JSONArray additionalTypes = vertexA().getJSONArray(VertexJsonFields.TYPES);
        assertThat(
                additionalTypes.length(),
                is(greaterThan(0))
        );
        removeFoafPersonIdentificationToVertexA();
        additionalTypes = vertexA().getJSONArray(VertexJsonFields.TYPES);
        assertThat(
                additionalTypes.length(),
                is(0)
        );
    }

    private ClientResponse addFoafPersonTypeToVertexA() throws Exception {
        JSONObject personType = ExternalResourceJson.get(
                ModelTestScenarios.personType()
        );
        personType.put(
                VertexIdentificationResource.IDENTIFICATION_TYPE_STRING,
                VertexIdentificationResource.identification_types.TYPE
        );
        ClientResponse response = resource
                .path(vertexAUri().getPath())
                .path("identification")
                .cookie(authCookie)
                .type(MediaType.APPLICATION_JSON)
                .post(ClientResponse.class, personType);
        return response;
    }

    private ClientResponse removeFoafPersonIdentificationToVertexA() throws Exception {
        ExternalFriendlyResource personType = ModelTestScenarios.personType();
        ClientResponse response = resource
                .path(vertexAUri().getPath())
                .path("identification")
                .path(encodeURL(personType.uri().toString()))
                .cookie(authCookie)
                .type(MediaType.APPLICATION_JSON)
                .delete(ClientResponse.class);
        return response;
    }
}
