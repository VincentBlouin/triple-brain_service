/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources;

import com.sun.jersey.api.client.ClientResponse;
import guru.bubl.module.model.search.VertexSearchResult;
import guru.bubl.service.utils.GraphManipulationRestTestUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;
import guru.bubl.module.model.graph.IdentificationPojo;
import guru.bubl.module.model.graph.IdentificationType;
import guru.bubl.module.model.json.IdentificationJson;
import guru.bubl.module.model.json.LocalizedStringJson;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.net.URI;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class SchemaPropertyResourceTest extends GraphManipulationRestTestUtils {

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
    public void removing_property_returns_no_content_status() {
        assertThat(
                removeProperty(uriOfCreatedProperty()).getStatus(),
                is(Response.Status.NO_CONTENT.getStatusCode())
        );
    }

    @Test
    public void accessing_removed_property_returns_not_found_status() {
        URI propertyUri = uriOfCreatedProperty();
        removeProperty(propertyUri);
        assertThat(
                updateLabel(
                        propertyUri,
                        "new label"
                ).getStatus(),
                is(Response.Status.NOT_FOUND.getStatusCode())
        );
    }

    @Test
    public void non_owner_cannot_access_resource() {
        URI propertyUri = uriOfCreatedProperty();
        assertThat(
                updateLabel(
                        propertyUri,
                        "new label"
                ).getStatus(),
                is(
                        not(
                                Response.Status.FORBIDDEN.getStatusCode()
                        )
                )
        );
        authenticate(createAUser());
        assertThat(
                updateLabel(
                        propertyUri,
                        "new label"
                ).getStatus(),
                is(
                        Response.Status.FORBIDDEN.getStatusCode()
                )
        );
    }

    @Test
    public void adding_identification_returns_created_status() throws Exception {
        IdentificationPojo identification = modelTestScenarios.creatorPredicate();
        identification.setType(
                IdentificationType.same_as
        );
        JSONObject creatorPredicate = IdentificationJson.singleToJson(identification);
        ClientResponse response = graphElementUtils().addIdentificationToGraphElementWithUri(
                creatorPredicate,
                uriOfCreatedProperty()
        );
        assertThat(
                response.getStatus(),
                is(ClientResponse.Status.CREATED.getStatusCode())
        );
    }

    @Test
    public void updating_label_of_property_updates_properties_name_of_schema_in_search() {
        URI schemaUri = schemaUtils().uriOfCreatedSchema();
        //updating schema label so that it gets reindex
        schemaUtils().updateSchemaLabelWithUri(
                schemaUri,
                "schema1"
        );
        URI propertyUri = uriOfCreatedPropertyForSchemaUri(schemaUri);
        updateLabel(propertyUri, "prop1");
        VertexSearchResult result = searchUtils().vertexSearchResultsFromResponse(
                searchUtils().autoCompletionForPublicVertices(
                        "schema1"
                )
        ).iterator().next();
        assertTrue(
                result.getProperties().containsKey(propertyUri)
        );
    }

    @Test
    public void deleting_property_also_removes_it_from_schema_in_search() {
        URI schemaUri = schemaUtils().uriOfCreatedSchema();
        //updating schema label so that it gets reindex
        schemaUtils().updateSchemaLabelWithUri(
                schemaUri,
                "schema1"
        );
        URI propertyUri = uriOfCreatedPropertyForSchemaUri(schemaUri);
        updateLabel(propertyUri, "prop1");
        VertexSearchResult searchResultA = searchUtils().autoCompletionResultsForCurrentUserVerticesOnly(
                "schema1"
        ).get(0);
        assertTrue(
                searchResultA.hasProperties()
        );
        removeProperty(propertyUri);
        searchResultA = searchUtils().autoCompletionResultsForCurrentUserVerticesOnly(
                "schema1"
        ).get(0);
        assertFalse(
                searchResultA.hasProperties()
        );
    }

    @Test
    public void updating_note_returns_correct_status() throws Exception {
        URI schemaUri = schemaUtils().uriOfCreatedSchema();
        URI propertyUri = uriOfCreatedPropertyForSchemaUri(schemaUri);
        ClientResponse response = updateNote(propertyUri, "some note");
        assertThat(
                response.getStatus(),
                is(Response.Status.NO_CONTENT.getStatusCode())
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

    private ClientResponse updateNote(URI propertyUri, String note) {
        return resource
                .path(propertyUri.toString())
                .path("comment")
                .cookie(authCookie)
                .type(MediaType.TEXT_PLAIN)
                .post(ClientResponse.class, note);
    }

    private URI uriOfCreatedProperty() {
        return uriOfCreatedPropertyForSchemaUri(
                schemaUtils().uriOfCreatedSchema()
        );
    }

    private URI uriOfCreatedPropertyForSchemaUri(URI schemaUri) {
        return graphUtils().getElementUriInResponse(
                addProperty(
                        schemaUri
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

    private ClientResponse removeProperty(URI propertyUri) {
        return resource
                .path(propertyUri.toString())
                .cookie(authCookie)
                .delete(ClientResponse.class);
    }

}
