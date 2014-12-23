/*
 * Copyright Vincent Blouin under the Mozilla Public License 1.1
 */

package org.triple_brain.service.vertex;

import com.sun.jersey.api.client.ClientResponse;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;
import org.triple_brain.module.model.FriendlyResource;
import org.triple_brain.module.model.UserUris;
import org.triple_brain.module.model.graph.Identification;
import org.triple_brain.module.model.graph.IdentificationPojo;
import org.triple_brain.module.model.graph.IdentificationType;
import org.triple_brain.module.model.graph.edge.Edge;
import org.triple_brain.module.model.json.IdentificationJson;
import org.triple_brain.service.utils.GraphManipulationRestTestUtils;

import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

public class GraphElementIdentificationResourceTest extends GraphManipulationRestTestUtils {

    @Test
    public void setting_type_of_a_vertex_returns_correct_response_status() throws Exception {
        ClientResponse response = graphElementUtils().addFoafPersonTypeToVertexA();
        assertThat(
                response.getStatus(),
                is(Response.Status.CREATED.getStatusCode())
        );
    }

    @Test
    public void status_is_created_when_adding_identification() {
        ClientResponse clientResponse = graphElementUtils().addFoafPersonTypeToVertexA();
        assertThat(
                clientResponse.getStatus(),
                is(Response.Status.CREATED.getStatusCode())
        );
    }

    @Test
    public void identification_is_returned_when_adding() {
        ClientResponse response = graphElementUtils().addFoafPersonTypeToVertexA();
        IdentificationPojo identification = IdentificationJson.singleFromJson(
                response.getEntity(String.class)
        );
        assertThat(
                identification.getExternalResourceUri(),
                is(URI.create("http://xmlns.com/foaf/0.1/Person"))
        );
    }

    @Test
    public void uri_is_returned_when_adding_identification() {
        assertThat(
                vertexA().getAdditionalTypes().size(),
                is(0)
        );
        ClientResponse response = graphElementUtils().addFoafPersonTypeToVertexA();
        String shortId = UserUris.graphElementShortId(
                vertexA().getAdditionalTypes().values().iterator().next().uri()
        );
        String responseShortId = UserUris.graphElementShortId(URI.create(
                response.getHeaders().get("Location").get(0)
        ));
        assertThat(
                responseShortId,
                is(
                        shortId
                )
        );
    }

    @Test
    public void can_add_an_additional_type_to_vertex() throws Exception {
        assertThat(
                vertexA().getAdditionalTypes().size(),
                is(0)
        );
        graphElementUtils().addFoafPersonTypeToVertexA();
        assertThat(
                vertexA().getAdditionalTypes().size(),
                is(greaterThan(0))
        );
    }

    @Test
    public void can_remove_the_additional_type_of_vertex() throws Exception {
        graphElementUtils().addFoafPersonTypeToVertexA();
        assertThat(
                vertexA().getAdditionalTypes().size(),
                is(1)
        );
        Identification addedIdentification = vertexA().getAdditionalTypes().values().iterator().next();
        removeIdentificationOfVertexA(addedIdentification);
        assertThat(
                vertexA().getAdditionalTypes().size(),
                is(0)
        );
    }

    @Test
    public void can_add_same_as_to_an_edge() throws Exception {
        Edge edgeBetweenAAndB = edgeUtils().edgeBetweenAAndB();
        Map<URI, ? extends FriendlyResource> sameAs = vertexA().getSameAs();
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
        ClientResponse clientResponse = graphElementUtils().addIdentificationToGraphElementWithUri(
                new JSONObject(),
                vertexAUri()
        );
        assertThat(
                clientResponse.getStatus(),
                is(Response.Status.NOT_ACCEPTABLE.getStatusCode())
        );
    }

    private ClientResponse addCreatorPredicateToEdge(Edge edge) throws Exception {
        IdentificationPojo identification = modelTestScenarios.creatorPredicate();
        identification.setType(
                IdentificationType.same_as
        );
        JSONObject creatorPredicate = IdentificationJson.singleToJson(identification);

        return graphElementUtils().addIdentificationToGraphElementWithUri(
                creatorPredicate,
                URI.create(edge.uri().toString())
        );
    }

    private ClientResponse removeIdentificationOfVertexA(Identification identification) throws Exception {
        return resource
                .path(vertexAUri().getPath())
                .path("identification")
                .queryParam(
                        "uri",
                        identification.uri().toString()
                )
                .cookie(authCookie)
                .delete(ClientResponse.class);
    }
}
