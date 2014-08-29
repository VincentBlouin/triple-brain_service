package org.triple_brain.service;

import com.sun.jersey.api.client.ClientResponse;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.eclipse.jetty.util.ajax.JSON;
import org.junit.Test;
import org.triple_brain.module.model.graph.schema.Schema;
import org.triple_brain.module.model.graph.schema.SchemaPojo;
import org.triple_brain.module.model.json.LocalizedStringJson;
import org.triple_brain.module.model.json.graph.SchemaJson;
import org.triple_brain.service.utils.GraphManipulationRestTest;

import javax.ws.rs.core.Response;

import java.net.URI;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Copyright Mozilla Public License 1.1
 */
public class SchemaResourceTest extends GraphManipulationRestTest {

    @Test
    public void correct_status_is_returned_upon_creation() {
        assertThat(
                schemaUtils().createSchema().getStatus(),
                is(Response.Status.CREATED.getStatusCode())
        );
    }

    @Test
    public void uri_is_returned_upon_creation() {
        assertTrue(
                schemaUtils().uriOfCreatedSchema().toString().contains(
                        graphUtils().getCurrentGraphUri()
                )
        );
    }

    @Test
    public void getting_schema_returns_correct_status() {
        URI uri = schemaUtils().uriOfCreatedSchema();
        assertThat(
                getSchemaWithUri(uri).getStatus(),
                is(
                        Response.Status.OK.getStatusCode()
                )
        );
    }

    @Test
    public void can_get_schema() {
        URI uri = schemaUtils().uriOfCreatedSchema();
        SchemaPojo schemaPojo = SchemaJson.fromJson(
                getSchemaWithUri(uri).getEntity(String.class)
        );
        assertThat(
                schemaPojo.uri(),
                is(uri)
        );
    }

    @Test
    public void updating_label_returns_no_content_status() {
        URI uri = schemaUtils().uriOfCreatedSchema();
        SchemaPojo schemaPojo = SchemaJson.fromJson(
                getSchemaWithUri(uri).getEntity(String.class)
        );
        ClientResponse response = updateSchemaLabel(schemaPojo, "patate");
        assertThat(
                response.getStatus(),
                is(Response.Status.NO_CONTENT.getStatusCode())
        );
    }

    @Test
    public void can_update_schema_label() {
        URI uri = schemaUtils().uriOfCreatedSchema();
        SchemaPojo schemaPojo = SchemaJson.fromJson(
                getSchemaWithUri(uri).getEntity(String.class)
        );
        assertThat(
                schemaPojo.label(),
                is(not("patate"))
        );
        updateSchemaLabel(schemaPojo, "patate");
        schemaPojo = SchemaJson.fromJson(
                getSchemaWithUri(uri).getEntity(String.class)
        );
        assertThat(
                schemaPojo.label(),
                is("patate")
        );
    }


    private ClientResponse updateSchemaLabel(Schema schema, String newLabel) {
        try {
            JSONObject localizedLabel = new JSONObject().put(
                    LocalizedStringJson.content.name(),
                    newLabel
            );
            return resource
                    .path(schema.uri().toString())
                    .path("label")
                    .cookie(authCookie)
                    .post(ClientResponse.class, localizedLabel);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private ClientResponse getSchemaWithUri(URI uri) {
        return resource
                .path(uri.toString())
                .cookie(authCookie)
                .get(ClientResponse.class);
    }
}
