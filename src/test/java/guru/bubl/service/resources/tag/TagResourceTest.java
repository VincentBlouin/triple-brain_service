/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.tag;

import com.sun.jersey.api.client.ClientResponse;
import guru.bubl.module.model.center_graph_element.CenterGraphElementPojo;
import guru.bubl.module.model.graph.tag.Tag;
import guru.bubl.module.model.graph.tag.TagPojo;
import guru.bubl.module.model.json.LocalizedStringJson;
import guru.bubl.module.model.tag.TagJson;
import guru.bubl.service.utils.GraphManipulationRestTestUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hamcrest.core.Is;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TagResourceTest extends GraphManipulationRestTestUtils {

    @Test
    public void updating_label_returns_no_content_status() {
        assertThat(
                vertexA().getIdentifications().size(),
                is(0)
        );

        graphElementUtils().addFoafPersonTypeToVertexA();
        Tag identification = vertexA().getIdentifications().values().iterator().next();
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
                vertexA().getIdentifications().size(),
                is(0)
        );
        graphElementUtils().addFoafPersonTypeToVertexA();
        Tag identification = vertexA().getIdentifications().values().iterator().next();
        assertFalse(
                identification.label().equals("new label")
        );
        updateIdentificationLabel(
                identification,
                "new label"
        );
        identification = vertexA().getIdentifications().values().iterator().next();
        assertTrue(
                identification.label().equals(
                        "new label"
                )
        );
    }

    @Test
    public void cannot_update_identification_label_of_another_user() {
        graphElementUtils().addFoafPersonTypeToVertexA();
        Tag identification = vertexA().getIdentifications().values().iterator().next();
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
        identification = vertexA().getIdentifications().values().iterator().next();
        assertFalse(
                identification.label().equals(
                        "new label"
                )
        );
    }

    @Test
    public void updating_note_returns_ok_status() {
        graphElementUtils().addFoafPersonTypeToVertexA();
        Tag identification = vertexA().getIdentifications().values().iterator().next();
        ClientResponse response = updateIdentificationNote(identification, "some note");
        assertThat(
                response.getStatus(),
                is(Response.Status.NO_CONTENT.getStatusCode())
        );
    }

    @Test
    public void can_update_note() {
        graphElementUtils().addFoafPersonTypeToVertexA();
        Tag identification = vertexA().getIdentifications().values().iterator().next();
        String identificationNote = identification.comment();
        assertThat(identificationNote, is(not("some note")));
        updateIdentificationNote(identification, "some note");
        identification = vertexA().getIdentifications().values().iterator().next();
        assertThat(identification.comment(), is("some note"));
    }

    @Test
    public void get_meta_returns_ok_status() {
        graphElementUtils().addFoafPersonTypeToVertexA();
        Tag meta = vertexA().getIdentifications().values().iterator().next();
        assertThat(
                getMeta(meta).getStatus(),
                is(Response.Status.OK.getStatusCode())
        );
    }

    @Test
    public void can_get_meta() {
        graphElementUtils().addFoafPersonTypeToVertexA();
        Tag meta = vertexA().getIdentifications().values().iterator().next();
        TagPojo person = TagJson.singleFromJson(
                getMeta(meta).getEntity(String.class)
        );
        assertThat(
                person.label(),
                is("Person")
        );
    }

    @Test
    public void updates_last_visit_date_when_getting_surround_graph() {
        graphElementUtils().addFoafPersonTypeToVertexA();
        Tag meta = vertexA().getIdentifications().values().iterator().next();
        List<CenterGraphElementPojo> centerElements = graphUtils().getCenterGraphElements();
        Integer numberOfVisitedElements = centerElements.size();
        graphUtils().graphWithCenterVertexUri(meta.uri());
        centerElements = graphUtils().getCenterGraphElements();
        assertThat(
                centerElements.size(),
                Is.is(numberOfVisitedElements + 1)
        );
    }

    @Test
    public void increments_number_of_visits_when_getting_surround_graph() {
        graphElementUtils().addFoafPersonTypeToVertexA();
        Tag meta = vertexA().getIdentifications().values().iterator().next();
        graphUtils().graphWithCenterVertexUri(meta.uri());
        CenterGraphElementPojo centerMeta = graphUtils().getCenterWithUri(
                graphUtils().getCenterGraphElements(),
                meta.uri()
        );
        assertThat(
                centerMeta.getNumberOfVisits(),
                is(1)
        );
        graphUtils().graphWithCenterVertexUri(meta.uri());
        centerMeta = graphUtils().getCenterWithUri(
                graphUtils().getCenterGraphElements(),
                meta.uri()
        );
        assertThat(
                centerMeta.getNumberOfVisits(),
                is(2)
        );
    }

    private ClientResponse updateIdentificationLabel(Tag identification, String label) {
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

    private ClientResponse updateIdentificationNote(Tag identification, String note) {
        return resource
                .path(identification.uri().getPath())
                .path("comment")
                .cookie(authCookie)
                .type(MediaType.TEXT_PLAIN)
                .post(ClientResponse.class, note);
    }

    private ClientResponse getMeta(Tag tag) {
        return resource
                .path(tag.uri().getPath())
                .cookie(authCookie)
                .get(ClientResponse.class);
    }
}
