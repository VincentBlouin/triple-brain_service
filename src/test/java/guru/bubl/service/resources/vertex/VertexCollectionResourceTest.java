/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.vertex;

import com.sun.jersey.api.client.ClientResponse;
import guru.bubl.module.common_utils.NoEx;
import guru.bubl.module.model.graph.ShareLevel;
import guru.bubl.module.model.graph.subgraph.SubGraphPojo;
import guru.bubl.module.model.graph.vertex.Vertex;
import guru.bubl.service.utils.GraphManipulationRestTestUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;

import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Arrays;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class VertexCollectionResourceTest extends GraphManipulationRestTestUtils {

    @Test
    public void removing_vertices_returns_no_content_status() {
        ClientResponse response = removeCollection(
                authCookie,
                vertexBUri(),
                vertexCUri()
        );
        assertThat(
                response.getStatus(),
                is(ClientResponse.Status.NO_CONTENT.getStatusCode())
        );
    }

    @Test
    public void can_remove_multiple_vertices_at_once() {
        Vertex vertexA = vertexA();
        Vertex vertexC = vertexC();
        SubGraphPojo subGraph = graphUtils().graphWithCenterVertexUri(
                vertexBUri()
        );
        assertTrue(
                subGraph.containsVertex(vertexA)
        );
        assertTrue(
                subGraph.containsVertex(vertexC)
        );
        removeCollection(
                authCookie,
                vertexAUri(),
                vertexCUri()
        );
        subGraph = graphUtils().graphWithCenterVertexUri(
                vertexBUri()
        );
        assertFalse(
                subGraph.containsVertex(vertexA)
        );
        assertFalse(
                subGraph.containsVertex(vertexC)
        );
    }

    @Test
    public void prevents_remove_not_owned_multiple_vertices_at_once() {
        Vertex vertexA = vertexA();
        Vertex vertexC = vertexC();
        SubGraphPojo subGraph = graphUtils().graphWithCenterVertexUri(
                vertexBUri()
        );
        assertTrue(
                subGraph.containsVertex(vertexA)
        );
        assertTrue(
                subGraph.containsVertex(vertexC)
        );
        ClientResponse response = removeCollection(
                authenticate(
                        createAUser()
                ).getCookies().get(0),
                vertexAUri(),
                vertexCUri()
        );
        assertThat(
                response.getStatus(),
                is(Response.Status.FORBIDDEN.getStatusCode())
        );
        authenticate(defaultAuthenticatedUser);
        subGraph = graphUtils().graphWithCenterVertexUri(
                vertexBUri()
        );
        assertTrue(
                subGraph.containsVertex(vertexA)
        );
        assertTrue(
                subGraph.containsVertex(vertexC)
        );
    }

    private ClientResponse removeCollection(NewCookie cookie, URI... vertexUri) {
        return resource
                .path(vertexUtils().getVertexBaseUri())
                .path("collection")
                .cookie(cookie)
                .delete(
                        ClientResponse.class,
                        new JSONArray(Arrays.asList(vertexUri))
                );
    }
}
