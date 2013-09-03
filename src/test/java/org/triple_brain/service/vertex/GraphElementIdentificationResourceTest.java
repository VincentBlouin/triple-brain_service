package org.triple_brain.service.vertex;

import com.sun.jersey.api.client.ClientResponse;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;
import org.triple_brain.module.model.json.FriendlyResourceJson;
import org.triple_brain.module.model.json.graph.EdgeJson;
import org.triple_brain.module.model.json.graph.GraphElementJson;
import org.triple_brain.module.model.json.graph.VertexJson;
import org.triple_brain.service.resources.GraphElementIdentificationResource;
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
public class GraphElementIdentificationResourceTest extends GraphManipulationRestTest {

    @Test
    public void setting_type_of_a_vertex_returns_correct_response_status() throws Exception {
        ClientResponse response = addFoafPersonTypeToVertexA();
        assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
    }

    @Test
    public void can_add_an_additional_type_to_vertex() throws Exception {
        JSONArray additionalTypes = vertexA().getJSONArray(VertexJson.TYPES);
        assertThat(
                additionalTypes.length(),
                is(0)
        );
        addFoafPersonTypeToVertexA();
        additionalTypes = vertexA().getJSONArray(VertexJson.TYPES);
        assertThat(
                additionalTypes.length(),
                is(greaterThan(0))
        );
    }

    @Test
    public void can_remove_the_additional_type_of_vertex() throws Exception {
        addFoafPersonTypeToVertexA();
        JSONArray additionalTypes = vertexA().getJSONArray(VertexJson.TYPES);
        assertThat(
                additionalTypes.length(),
                is(greaterThan(0))
        );
        removeFoafPersonIdentificationToVertexA();
        additionalTypes = vertexA().getJSONArray(VertexJson.TYPES);
        assertThat(
                additionalTypes.length(),
                is(0)
        );
    }

    @Test
    public void can_add_same_as_to_an_edge() throws Exception {
        JSONObject edgeBetweenAAndB = edgeUtils().edgeBetweenAAndB();
        JSONArray sameAs = vertexA().getJSONArray(GraphElementJson.SAME_AS);
        assertThat(
                sameAs.length(),
                is(0)
        );
        addCreatorPredicateToEdge(edgeBetweenAAndB);
        sameAs = edgeUtils().edgeBetweenAAndB().getJSONArray(
                EdgeJson.SAME_AS
        );
        assertThat(
                sameAs.length(),
                is(greaterThan(0))
        );
    }

    private ClientResponse addCreatorPredicateToEdge(JSONObject edge) throws Exception {
        JSONObject creatorPredicate = new JSONObject()
                .put(
                        FriendlyResourceJson.URI,
                        "http://purl.org/dc/terms/creator"
                )
                .put(
                        GraphElementIdentificationResource.IDENTIFICATION_TYPE_STRING,
                        GraphElementIdentificationResource.identification_types.SAME_AS
                );
        ClientResponse response = resource
                .path(edge.optString(EdgeJson.URI))
                .path("identification")
                .cookie(authCookie)
                .type(MediaType.APPLICATION_JSON)
                .post(ClientResponse.class, creatorPredicate);
        return response;
    }

    private ClientResponse addFoafPersonTypeToVertexA() throws Exception {
        JSONObject personType = new JSONObject()
                .put(
                        FriendlyResourceJson.URI,
                        "http://xmlns.com/foaf/0.1/Person"
                )
                .put(
                        GraphElementIdentificationResource.IDENTIFICATION_TYPE_STRING,
                        GraphElementIdentificationResource.identification_types.TYPE
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
        ClientResponse response = resource
                .path(vertexAUri().getPath())
                .path("identification")
                .path(encodeURL("http://xmlns.com/foaf/0.1/Person"))
                .cookie(authCookie)
                .type(MediaType.APPLICATION_JSON)
                .delete(ClientResponse.class);
        return response;
    }
}
