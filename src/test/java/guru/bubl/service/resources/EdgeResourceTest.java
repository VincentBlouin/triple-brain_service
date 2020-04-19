package guru.bubl.service.resources;

import com.sun.jersey.api.client.ClientResponse;
import guru.bubl.module.model.UserUris;
import guru.bubl.module.model.graph.graph_element.GraphElementType;
import guru.bubl.module.model.graph.ShareLevel;
import guru.bubl.module.model.graph.relation.Relation;
import guru.bubl.module.model.graph.subgraph.SubGraph;
import guru.bubl.module.model.graph.subgraph.SubGraphPojo;
import guru.bubl.service.SessionHandler;
import guru.bubl.service.utils.GraphManipulationRestTestUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;

import javax.ws.rs.core.Response;

import java.net.URI;

import static junit.framework.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;

public class EdgeResourceTest extends GraphManipulationRestTestUtils {
    @Test
    public void changing_source_vertex_returns_correct_status() {
        Relation relationBC = edgeUtils().edgeBetweenTwoVerticesUriGivenEdges(
                vertexBUri(),
                vertexCUri(),
                graphUtils().graphWithCenterVertexUri(vertexBUri()).edges()
        );
        ClientResponse response = changeSource(
                relationBC.uri(),
                vertexA().uri(),
                GraphElementType.Vertex,
                ShareLevel.PRIVATE,
                ShareLevel.PRIVATE,
                ShareLevel.PRIVATE
        );
        assertThat(
                response.getStatus(),
                is(
                        Response.Status.NO_CONTENT.getStatusCode()
                )
        );
    }

    @Test
    public void can_change_source_vertex() {
        Relation relation = edgeUtils().edgeBetweenTwoVerticesUriGivenEdges(
                vertexBUri(),
                vertexCUri(),
                graphUtils().graphWithCenterVertexUri(vertexBUri()).edges()

        );
        SubGraph graph = graphUtils().graphWithCenterVertexUri(
                vertexAUri()
        );
        assertFalse(
                graph.containsEdge(relation)
        );
        changeSource(
                relation.uri(),
                vertexA().uri(),
                GraphElementType.Vertex,
                ShareLevel.PRIVATE,
                ShareLevel.PRIVATE,
                ShareLevel.PRIVATE
        );
        graph = graphUtils().graphWithCenterVertexUri(
                vertexAUri()
        );
        assertTrue(
                graph.containsEdge(relation)
        );
    }

    @Test
    public void can_change_source_fork_to_a_group_relation() {
        Relation relationAB = edgeUtils().edgeBetweenTwoVerticesUriGivenEdges(
                vertexAUri(),
                vertexBUri(),
                graphUtils().graphWithCenterVertexUri(vertexBUri()).edges()
        );
        SubGraphPojo graph = graphUtils().graphWithCenterVertexUri(
                graphUtils().getTodoGroupRelation().uri()
        );
        assertFalse(
                graph.containsEdge(relationAB)
        );
        changeSource(
                relationAB.uri(),
                graphUtils().getTodoGroupRelation().uri(),
                GraphElementType.GroupRelation,
                ShareLevel.PRIVATE,
                ShareLevel.PRIVATE,
                ShareLevel.PRIVATE
        );
        graph = graphUtils().graphWithCenterVertexUri(
                graphUtils().getTodoGroupRelation().uri()
        );
        assertTrue(
                graph.containsEdge(relationAB)
        );
    }

    @Test
    public void change_source_of_group_relation_returns_no_content_status() {
        ClientResponse response = changeSource(
                graphUtils().getTodoGroupRelation().uri(),
                vertexA().uri(),
                GraphElementType.Vertex,
                ShareLevel.PRIVATE,
                ShareLevel.PRIVATE,
                ShareLevel.PRIVATE
        );
        assertThat(
                response.getStatus(),
                is(
                        Response.Status.NO_CONTENT.getStatusCode()
                )
        );
    }


    @Test
    public void changing_destination_vertex_returns_correct_status() {
        Relation relationBetweenAAndB = edgeUtils().edgeBetweenTwoVerticesUriGivenEdges(
                vertexAUri(),
                vertexBUri(),
                graphUtils().graphWithCenterVertexUri(vertexAUri()).edges()
        );
        ClientResponse response = changeDestination(
                relationBetweenAAndB,
                vertexC().uri(),
                GraphElementType.Vertex,
                ShareLevel.PRIVATE,
                ShareLevel.PRIVATE,
                ShareLevel.PRIVATE
        );
        assertThat(
                response.getStatus(),
                is(
                        Response.Status.NO_CONTENT.getStatusCode()
                )
        );
    }

    @Test
    public void can_change_destination_vertex() {
        Relation relation = edgeUtils().edgeBetweenTwoVerticesUriGivenEdges(
                vertexAUri(),
                vertexBUri(),
                graphUtils().graphWithCenterVertexUri(vertexAUri()).edges()

        );
        SubGraph graph = graphUtils().graphWithCenterVertexUri(
                vertexCUri()
        );
        assertFalse(
                graph.containsEdge(relation)
        );
        changeDestination(
                relation,
                vertexC().uri(),
                GraphElementType.Vertex,
                ShareLevel.PRIVATE,
                ShareLevel.PRIVATE,
                ShareLevel.PRIVATE
        );
        graph = graphUtils().graphWithCenterVertexUri(
                vertexCUri()
        );
        assertTrue(
                graph.containsEdge(relation)
        );
    }

    private ClientResponse changeSource(URI edgeUri, URI newSourceUri, GraphElementType sourceType, ShareLevel oldEndShareLevel, ShareLevel keptEndShareLevel, ShareLevel newEndShareLevel) {
        try {
            return resource
                    .path(edgeUri.toString())
                    .path("source")
                    .path(UserUris.graphElementShortId(newSourceUri))
                    .cookie(authCookie)
                    .header(SessionHandler.X_XSRF_TOKEN, currentXsrfToken)
                    .put(ClientResponse.class, new JSONObject().put(
                            "forkType",
                            sourceType.name()
                    ).put(
                            "oldEndShareLevel", oldEndShareLevel.name().toUpperCase()
                    ).put(
                            "keptEndShareLevel", keptEndShareLevel.name().toUpperCase()
                    ).put(
                            "newEndShareLevel", newEndShareLevel.name().toUpperCase()
                    ));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private ClientResponse changeDestination(Relation relation, URI destinationUri, GraphElementType destinationType, ShareLevel oldEndShareLevel, ShareLevel keptEndShareLevel, ShareLevel newEndShareLevel) {
        try {
            return resource
                    .path(relation.uri().toString())
                    .path("destination")
                    .path(UserUris.graphElementShortId(destinationUri))
                    .cookie(authCookie)
                    .header(SessionHandler.X_XSRF_TOKEN, currentXsrfToken)
                    .put(ClientResponse.class, new JSONObject().put(
                            "forkType",
                            destinationType
                    ).put(
                            "oldEndShareLevel", oldEndShareLevel.name().toUpperCase()
                    ).put(
                            "keptEndShareLevel", keptEndShareLevel.name().toUpperCase()
                    ).put(
                            "newEndShareLevel", newEndShareLevel.name().toUpperCase()
                    ));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
