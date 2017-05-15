/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.meta;

import com.sun.jersey.api.client.ClientResponse;
import guru.bubl.module.model.graph.identification.Identifier;
import guru.bubl.module.model.graph.identification.IdentifierPojo;
import guru.bubl.module.model.json.LocalizedStringJson;
import guru.bubl.module.model.meta.MetaJson;
import guru.bubl.service.utils.GraphManipulationRestTestUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IdentifierResourceTest extends GraphManipulationRestTestUtils {

    @Test
    public void updating_label_returns_no_content_status() {
        assertThat(
                vertexA().getIdentifications().size(),
                is(0)
        );

        graphElementUtils().addFoafPersonTypeToVertexA();
        Identifier identification = vertexA().getIdentifications().values().iterator().next();
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
        Identifier identification = vertexA().getIdentifications().values().iterator().next();
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
        Identifier identification = vertexA().getIdentifications().values().iterator().next();
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
    public void updating_note_returns_ok_status(){
        graphElementUtils().addFoafPersonTypeToVertexA();
        Identifier identification = vertexA().getIdentifications().values().iterator().next();
        ClientResponse response = updateIdentificationNote(identification, "some note");
        assertThat(
                response.getStatus(),
                is(Response.Status.NO_CONTENT.getStatusCode())
        );
    }

    @Test
    public void can_update_note(){
        graphElementUtils().addFoafPersonTypeToVertexA();
        Identifier identification = vertexA().getIdentifications().values().iterator().next();
        String identificationNote = identification.comment();
        assertThat(identificationNote, is(not("some note")));
        updateIdentificationNote(identification, "some note");
        identification = vertexA().getIdentifications().values().iterator().next();
        assertThat(identification.comment(), is("some note"));
    }

    @Test
    public void get_meta_returns_ok_status(){
        graphElementUtils().addFoafPersonTypeToVertexA();
        Identifier meta = vertexA().getIdentifications().values().iterator().next();
        assertThat(
                getMeta(meta).getStatus(),
                is(Response.Status.OK.getStatusCode())
        );
    }

    @Test
    public void can_get_meta(){
        graphElementUtils().addFoafPersonTypeToVertexA();
        Identifier meta = vertexA().getIdentifications().values().iterator().next();
        IdentifierPojo person = MetaJson.singleFromJson(
                getMeta(meta).getEntity(String.class)
        );
        assertThat(
                person.label(),
                is("Person")
        );
    }

    private ClientResponse updateIdentificationLabel(Identifier identification, String label) {
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

    private ClientResponse updateIdentificationNote(Identifier identification, String note) {
        return resource
                .path(identification.uri().getPath())
                .path("comment")
                .cookie(authCookie)
                .type(MediaType.TEXT_PLAIN)
                .post(ClientResponse.class, note);
    }

    private ClientResponse getMeta(Identifier identifier){
        return resource
                .path(identifier.uri().getPath())
                .cookie(authCookie)
                .get(ClientResponse.class);
    }
}
