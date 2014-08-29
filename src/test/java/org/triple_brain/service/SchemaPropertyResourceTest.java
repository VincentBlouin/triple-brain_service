package org.triple_brain.service;

import com.sun.jersey.api.client.ClientResponse;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;
import org.triple_brain.module.model.json.IdentificationJson;
import org.triple_brain.module.model.json.LocalizedStringJson;
import org.triple_brain.service.resources.GraphElementIdentificationResource;
import org.triple_brain.service.utils.GraphManipulationRestTest;

import javax.ws.rs.core.Response;

import java.net.URI;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Copyright Mozilla Public License 1.1
 */
public class SchemaPropertyResourceTest extends GraphManipulationRestTest {

    @Test
    public void creation_returns_created_status() {
        assertThat(
                addProperty(schemaUtils().uriOfCreatedSchema()).getStatus(),
                is(Response.Status.CREATED.getStatusCode())
        );
    }

    @Test
    public void updating_label_returns_no_content_status() {
        ClientResponse response = updateLabel(
                uriOfCreatedProperty(),
                "new label"
        );
        assertThat(
                response.getStatus(),
                is(
                        Response.Status.NO_CONTENT.getStatusCode()
                )
        );
    }

    @Test
    public void adding_identification_returns_created_status() throws Exception{
        JSONObject creatorPredicate = IdentificationJson.toJson(modelTestScenarios.creatorPredicate()).put(
                GraphElementIdentificationResource.IDENTIFICATION_TYPE_STRING,
                GraphElementIdentificationResource.identification_types.SAME_AS
        );
        ClientResponse response = graphElementUtils().addIdentificationToGraphElementWithUri(
                creatorPredicate,
                uriOfCreatedProperty()
        );
        assertThat(
                response.getStatus(),
                is(ClientResponse.Status.CREATED.getStatusCode())
        );
    }

    private ClientResponse updateLabel(URI propertyUri, String label) {
        try {
            JSONObject localizedLabel = new JSONObject().put(
                    LocalizedStringJson.content.name(),
                    label
            );
            return resource
                    .path(propertyUri.toString())
                    .path("label")
                    .cookie(authCookie)
                    .post(ClientResponse.class, localizedLabel);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private URI uriOfCreatedProperty() {
        return graphUtils().getElementUriInResponse(
                addProperty(
                        schemaUtils().uriOfCreatedSchema()
                )
        );
    }

    private ClientResponse addProperty(URI schemaUri) {
        return resource
                .path(schemaUri.toString())
                .path("property")
                .cookie(authCookie)
                .post(ClientResponse.class);
    }

}
