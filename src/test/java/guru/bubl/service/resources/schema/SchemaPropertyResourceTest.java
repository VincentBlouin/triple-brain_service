/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.schema;

import com.sun.jersey.api.client.ClientResponse;
import guru.bubl.module.model.graph.identification.IdentifierPojo;
import guru.bubl.module.model.graph.schema.Schema;
import guru.bubl.module.model.graph.schema.SchemaJson;
import guru.bubl.module.model.json.LocalizedStringJson;
import guru.bubl.module.model.meta.MetaJson;
import guru.bubl.module.model.search.GraphElementSearchResult;
import guru.bubl.service.utils.GraphManipulationRestTestUtils;
import guru.bubl.test.module.utils.ModelTestScenarios;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Ignore;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.*;

@Ignore("schema feature suspended")
public class SchemaPropertyResourceTest extends GraphManipulationRestTestUtils {

    @Test
    public void creation_returns_created_status() {
        assertThat(
                schemaUtils().addProperty(schemaUtils().uriOfCreatedSchema()).getStatus(),
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
    public void adding_identification_returns_ok_status() throws Exception {
        IdentifierPojo identification = modelTestScenarios.creatorPredicate();
        identification.setRelationExternalResourceUri(
                ModelTestScenarios.SAME_AS
        );
        JSONObject creatorPredicate = MetaJson.singleToJson(identification);
        ClientResponse response = graphElementUtils().addIdentificationToGraphElementWithUri(
                creatorPredicate,
                uriOfCreatedProperty()
        );
        assertThat(
                response.getStatus(),
                is(ClientResponse.Status.OK.getStatusCode())
        );
    }

    @Test
    public void deleting_property_also_removes_it_from_schema_in_search() {
        URI schemaUri = schemaUtils().uriOfCreatedSchema();
        schemaUtils().updateSchemaLabelWithUri(
                schemaUri,
                "schema1"
        );
        URI propertyUri = schemaUtils().addPropertyForSchemaUri(schemaUri);
        searchUtils().indexAll();
        GraphElementSearchResult schemaSearchResult = searchUtils().autoCompletionResultsForCurrentUserVerticesOnly(
                "schema1"
        ).get(0);
        searchUtils().indexAll();
        assertFalse(
                schemaSearchResult.getContext().isEmpty()
        );
        removeProperty(propertyUri);
        searchUtils().indexAll();
        schemaSearchResult = searchUtils().autoCompletionResultsForCurrentUserVerticesOnly(
                "schema1"
        ).get(0);
        assertTrue(
                schemaSearchResult.getContext().isEmpty()
        );
    }

    @Test
    public void updating_note_returns_correct_status() throws Exception {
        URI schemaUri = schemaUtils().uriOfCreatedSchema();
        URI propertyUri = schemaUtils().addPropertyForSchemaUri(schemaUri);
        ClientResponse response = updateNote(propertyUri, "some note");
        assertThat(
                response.getStatus(),
                is(Response.Status.NO_CONTENT.getStatusCode())
        );
    }

    @Test
    public void getting_surround_graph_returns_ok_status() {
        URI schemaUri = schemaUtils().uriOfCreatedSchema();
        URI propertyUri = schemaUtils().addPropertyForSchemaUri(schemaUri);
        assertThat(
                getSurroundGraphForPropertyWithUri(
                        propertyUri
                ).getStatus(),
                is(
                        Response.Status.OK.getStatusCode()
                )
        );
    }

    @Test
    public void getting_surround_graph_returns_the_schema_and_its_properties() {
        URI schemaUri = schemaUtils().uriOfCreatedSchema();
        URI propertyUri = schemaUtils().addPropertyForSchemaUri(schemaUri);
        Schema schema = SchemaJson.fromJson(
                getSurroundGraphForPropertyWithUri(propertyUri).getEntity(
                        String.class
                )
        );
        assertThat(
                schema.uri(),
                is(schemaUri)
        );
        assertTrue(
                schema.getProperties().containsKey(
                        propertyUri
                )
        );
    }

    private ClientResponse getSurroundGraphForPropertyWithUri(URI propertyUri) {
        return resource
                .path(propertyUri.toString())
                .path("surround_graph")
                .cookie(authCookie)
                .get(ClientResponse.class);
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
        return schemaUtils().addPropertyForSchemaUri(
                schemaUtils().uriOfCreatedSchema()
        );
    }

    private ClientResponse removeProperty(URI propertyUri) {
        return resource
                .path(propertyUri.toString())
                .cookie(authCookie)
                .delete(ClientResponse.class);
    }

}
