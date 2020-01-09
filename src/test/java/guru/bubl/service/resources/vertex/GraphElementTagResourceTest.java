/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.vertex;

import com.sun.jersey.api.client.ClientResponse;
import guru.bubl.module.common_utils.NoEx;
import guru.bubl.module.model.FriendlyResource;
import guru.bubl.module.model.graph.FriendlyResourcePojo;
import guru.bubl.module.model.graph.edge.Edge;
import guru.bubl.module.model.graph.edge.EdgeJson;
import guru.bubl.module.model.graph.edge.EdgePojo;
import guru.bubl.module.model.graph.tag.Tag;
import guru.bubl.module.model.graph.tag.TagPojo;
import guru.bubl.module.model.tag.TagJson;
import guru.bubl.service.utils.GraphManipulationRestTestUtils;
import guru.bubl.service.utils.RestTestUtils;
import guru.bubl.test.module.utils.ModelTestScenarios;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

public class GraphElementTagResourceTest extends GraphManipulationRestTestUtils {

    @Test
    public void setting_type_of_a_vertex_returns_ok_response_status() {
        ClientResponse response = graphElementUtils().addFoafPersonTypeToVertexA();
        assertThat(
                response.getStatus(),
                is(Response.Status.OK.getStatusCode())
        );
    }

    @Test
    public void status_is_ok_when_adding_identification() {
        ClientResponse clientResponse = graphElementUtils().addFoafPersonTypeToVertexA();
        assertThat(
                clientResponse.getStatus(),
                is(Response.Status.OK.getStatusCode())
        );
    }

    @Test
    public void identifications_are_returned_when_adding() {
        ClientResponse response = graphElementUtils().addFoafPersonTypeToVertexA();
        TagPojo identification = TagJson.fromJson(
                response.getEntity(String.class)
        ).values().iterator().next();
        assertThat(
                identification.getExternalResourceUri(),
                is(URI.create("http://xmlns.com/foaf/0.1/Person"))
        );
    }


    @Test
    public void can_add_an_additional_type_to_vertex() {
        assertThat(
                vertexA().getIdentifications().size(),
                is(0)
        );
        graphElementUtils().addFoafPersonTypeToVertexA();
        assertThat(
                vertexA().getIdentifications().size(),
                is(greaterThan(0))
        );
    }

    @Test
    public void can_remove_the_additional_type_of_vertex() {
        graphElementUtils().addFoafPersonTypeToVertexA();
        assertThat(
                vertexA().getIdentifications().size(),
                is(1)
        );
        Tag addedIdentification = vertexA().getIdentifications().values().iterator().next();
        removeIdentificationToResource(
                addedIdentification,
                vertexA()
        );
        assertThat(
                vertexA().getIdentifications().size(),
                is(0)
        );
    }

    @Test
    public void can_add_same_as_to_an_edge() {
        Edge edgeBetweenAAndB = edgeUtils().edgeBetweenAAndB();
        Map<URI, ? extends FriendlyResource> sameAs = vertexA().getIdentifications();
        assertThat(
                sameAs.size(),
                is(0)
        );
        addCreatorPredicateToEdge(edgeBetweenAAndB);
        sameAs = edgeUtils().edgeBetweenAAndB().getIdentifications();
        assertThat(
                sameAs.size(),
                is(greaterThan(0))
        );
    }

    @Test
    public void if_invalid_identification_it_throws_an_exception() {
        ClientResponse clientResponse = graphElementUtils().addIdentificationToGraphElementWithUri(
                new JSONObject(),
                vertexAUri()
        );
        assertThat(
                clientResponse.getStatus(),
                is(Response.Status.NOT_ACCEPTABLE.getStatusCode())
        );
    }

    @Test
    public void can_remove_self_identifier() throws Exception{
        JSONObject tripleAsJson = vertexUtils().addAVertexToVertexWithUri(
                vertexAUri()
        ).getEntity(
                JSONObject.class
        );
        EdgePojo newEdge = EdgeJson.fromJson(
                tripleAsJson.getJSONObject("edge")
        );
        TagPojo edgeAsIdentifier = new TagPojo(
                newEdge.uri(),
                new FriendlyResourcePojo(
                        newEdge.uri(),
                        "some label"
                )
        );
        Edge edgeBetweenAAndB = edgeUtils().edgeBetweenTwoVerticesUriGivenEdges(
                vertexAUri(),
                vertexBUri(),
                graphUtils().graphWithCenterVertexUri(vertexAUri()).edges()
        );
        TagPojo newEdgeAsMeta = TagJson.fromJson(
                graphElementUtils().addIdentificationToGraphElementWithUri(
                        edgeAsIdentifier,
                        edgeBetweenAAndB.uri()
                ).getEntity(String.class)
        ).values().iterator().next();

        ClientResponse response = removeIdentificationToResource(
                newEdgeAsMeta,
                newEdge
        );
        assertThat(
                response.getStatus(),
                is(Response.Status.NO_CONTENT.getStatusCode())
        );
    }

    private ClientResponse addCreatorPredicateToEdge(Edge edge) {
        TagPojo creatorPredicate = modelTestScenarios.creatorPredicate();
        creatorPredicate.setRelationExternalResourceUri(
                ModelTestScenarios.TYPE
        );
        return graphElementUtils().addIdentificationToGraphElementWithUri(
                creatorPredicate,
                edge.uri()
        );
    }

    private ClientResponse removeIdentificationToResource(Tag identification, FriendlyResource resource) {
        return NoEx.wrap(() -> RestTestUtils.resource
                .path(resource.uri().getPath())
                .path("identification")
                .queryParam(
                        "uri",
                        identification.uri().toString()
                )
                .cookie(authCookie)
                .delete(ClientResponse.class)).get();
    }
}
