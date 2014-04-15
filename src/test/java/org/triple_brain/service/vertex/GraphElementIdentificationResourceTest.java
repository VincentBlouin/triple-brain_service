package org.triple_brain.service.vertex;

import com.sun.jersey.api.client.ClientResponse;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;
import org.triple_brain.module.model.FriendlyResource;
import org.triple_brain.module.model.graph.edge.Edge;
import org.triple_brain.service.resources.GraphElementIdentificationResource;
import org.triple_brain.service.utils.GraphManipulationRestTest;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

/*
* Copyright Mozilla Public License 1.1
*/
public class GraphElementIdentificationResourceTest extends GraphManipulationRestTest {

    @Test
    public void setting_type_of_a_vertex_returns_correct_response_status() throws Exception {
        ClientResponse response = addFoafPersonTypeToVertexA();
        assertThat(
                response.getStatus(),
                is(Response.Status.NO_CONTENT.getStatusCode())
        );
    }

    @Test
    public void status_is_no_content_when_adding_identification()throws Exception{
        ClientResponse clientResponse = addFoafPersonTypeToVertexA();
        assertThat(
                clientResponse.getStatus(),
                is(Response.Status.NO_CONTENT.getStatusCode())
        );
    }

    @Test
    public void can_add_an_additional_type_to_vertex() throws Exception {
        assertThat(
                vertexA().getAdditionalTypes().size(),
                is(0)
        );
        addFoafPersonTypeToVertexA();
        assertThat(
                vertexA().getAdditionalTypes().size(),
                is(greaterThan(0))
        );
    }

    @Test
    public void can_remove_the_additional_type_of_vertex() throws Exception {
        addFoafPersonTypeToVertexA();
        assertThat(
                vertexA().getAdditionalTypes().size(),
                is(greaterThan(0))
        );
        removeFoafPersonIdentificationToVertexA();
        assertThat(
                vertexA().getAdditionalTypes().size(),
                is(0)
        );
    }

    @Test
    public void can_add_same_as_to_an_edge() throws Exception {
        Edge edgeBetweenAAndB = edgeUtils().edgeBetweenAAndB();
        Map<URI, ?extends FriendlyResource> sameAs = vertexA().getSameAs();
        assertThat(
                sameAs.size(),
                is(0)
        );
        addCreatorPredicateToEdge(edgeBetweenAAndB);
        sameAs = edgeUtils().edgeBetweenAAndB().getSameAs();
        assertThat(
                sameAs.size(),
                is(greaterThan(0))
        );
    }

    @Test
    public void if_invalid_identification_it_throws_an_exception() throws Exception {
        ClientResponse clientResponse = addIdentificationToGraphElementWithUri(
                new JSONObject(),
                vertexAUri()
        );
        assertThat(
                clientResponse.getStatus(),
                is(Response.Status.NOT_ACCEPTABLE.getStatusCode())
        );
    }

    private ClientResponse addCreatorPredicateToEdge(Edge edge) throws Exception {
        JSONObject creatorPredicate = new JSONObject()
                .put(
                        "uri",
                        "http://purl.org/dc/terms/creator"
                )
                .put(
                        GraphElementIdentificationResource.IDENTIFICATION_TYPE_STRING,
                        GraphElementIdentificationResource.identification_types.SAME_AS
                );
        return addIdentificationToGraphElementWithUri(
                creatorPredicate,
                URI.create(edge.uri().toString())
        );
    }

    private ClientResponse addFoafPersonTypeToVertexA() throws Exception {
        JSONObject personType = new JSONObject()
                .put(
                        "uri",
                        "http://xmlns.com/foaf/0.1/Person"
                )
                .put(
                        GraphElementIdentificationResource.IDENTIFICATION_TYPE_STRING,
                        GraphElementIdentificationResource.identification_types.TYPE
                );
        return addIdentificationToGraphElementWithUri(
                personType,
                vertexAUri()
        );
    }

    private ClientResponse addIdentificationToGraphElementWithUri(JSONObject identification, URI graphElementUri){
        ClientResponse response = resource
                .path(graphElementUri.getPath())
                .path("identification")
                .cookie(authCookie)
                .type(MediaType.APPLICATION_JSON)
                .post(ClientResponse.class, identification);
        return response;
    }

    private ClientResponse removeFoafPersonIdentificationToVertexA() throws Exception {
        ClientResponse response = resource
                .path(vertexAUri().getPath())
                .path("identification")
                .queryParam(
                        "uri",
                        "http://xmlns.com/foaf/0.1/Person"
                )
                .cookie(authCookie)
                .delete(ClientResponse.class);
        return response;
    }
}
