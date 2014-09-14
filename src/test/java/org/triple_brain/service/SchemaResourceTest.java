/*
 * Copyright Vincent Blouin under the Mozilla Public License 1.1
 */

package org.triple_brain.service;

import com.sun.jersey.api.client.ClientResponse;
import org.junit.Test;
import org.triple_brain.module.model.graph.schema.SchemaPojo;
import org.triple_brain.module.model.json.graph.SchemaJson;
import org.triple_brain.module.search.VertexSearchResult;
import org.triple_brain.service.utils.GraphManipulationRestTest;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

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
    public void non_owner_cannot_access_resource() {
        URI uri = schemaUtils().uriOfCreatedSchema();
        assertThat(
                getSchemaWithUri(uri).getStatus(),
                is(
                        Response.Status.OK.getStatusCode()
                )
        );
        authenticate(createAUser());
        assertThat(
                getSchemaWithUri(uri).getStatus(),
                is(
                        Response.Status.FORBIDDEN.getStatusCode()
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
        ClientResponse response = schemaUtils().updateSchemaLabel(
                schemaPojo,
                "patate"
        );
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
        schemaUtils().updateSchemaLabel(
                schemaPojo,
                "patate"
        );
        schemaPojo = SchemaJson.fromJson(
                getSchemaWithUri(uri).getEntity(String.class)
        );
        assertThat(
                schemaPojo.label(),
                is("patate")
        );
    }

    @Test
    public void can_get_schema_by_searching_with_its_label() {
        URI schemaUri = schemaUtils().uriOfCreatedSchema();
        schemaUtils().updateSchemaLabelWithUri(
                schemaUri,
                "schema1"
        );
        List<VertexSearchResult> results = searchUtils().autoCompletionResultsForPublicAndUserVertices(
                "schema1",
                defaultAuthenticatedUserAsJson
        );
        assertThat(
                results.size(),
                is(
                        1
                )
        );
        VertexSearchResult result = results.iterator().next();
        assertThat(
                result.getGraphElement().uri(),
                is(
                        schemaUri
                )
        );
    }

    @Test
    public void updating_description_returns_no_content_status() {
        assertThat(
                updateDescription(
                        schemaUtils().uriOfCreatedSchema(),
                        "dummy description"
                ).getStatus(),
                is(Response.Status.NO_CONTENT.getStatusCode())
        );
    }

    @Test
    public void can_update_description() {
        URI uri = schemaUtils().uriOfCreatedSchema();
        SchemaPojo schemaPojo = SchemaJson.fromJson(
                getSchemaWithUri(uri).getEntity(String.class)
        );
        assertThat(
                schemaPojo.comment(),
                is(not("dummy description"))
        );
        updateDescription(
                uri,
                "dummy description"
        );
        schemaPojo = SchemaJson.fromJson(
                getSchemaWithUri(uri).getEntity(String.class)
        );
        assertThat(
                schemaPojo.comment(),
                is("dummy description")
        );
    }

    private ClientResponse getSchemaWithUri(URI uri) {
        return resource
                .path(uri.toString())
                .cookie(authCookie)
                .get(ClientResponse.class);
    }

    private ClientResponse updateDescription(URI uri, String description) {
        return resource
                .path(uri.toString())
                .path("comment")
                .cookie(authCookie)
                .post(
                        ClientResponse.class,
                        description
                );
    }
}
