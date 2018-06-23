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

import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Arrays;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class VertexCollectionResourceTest extends GraphManipulationRestTestUtils {

    @Test
    public void removing_vertices_returns_no_content_status() {
        ClientResponse response = removeCollection(
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
    public void can_set_share_level_of_multiple_vertices() {
        assertThat(
                vertexA().getShareLevel(),
                is(ShareLevel.PRIVATE)
        );
        assertThat(
                vertexB().getShareLevel(),
                is(ShareLevel.PRIVATE)
        );
        assertThat(
                vertexC().getShareLevel(),
                is(ShareLevel.PRIVATE)
        );
        ClientResponse response = setShareLevelOfCollection(
                ShareLevel.FRIENDS,
                vertexAUri(),
                vertexBUri()
        );
        assertThat(
                response.getStatus(),
                is(Response.Status.NO_CONTENT.getStatusCode())
        );
        assertThat(
                vertexA().getShareLevel(),
                is(ShareLevel.FRIENDS)
        );
        assertThat(
                vertexB().getShareLevel(),
                is(ShareLevel.FRIENDS)
        );
        assertThat(
                vertexC().getShareLevel(),
                is(ShareLevel.PRIVATE)
        );
    }

    private ClientResponse setShareLevelOfCollection(ShareLevel shareLevel, URI... vertexUri) {
        return NoEx.wrap(() ->
                resource
                        .path(vertexUtils().getVertexBaseUri())
                        .path("collection")
                        .path("share-level")
                        .cookie(authCookie)
                        .post(
                                ClientResponse.class,
                                new JSONObject().put(
                                        "shareLevel",
                                        shareLevel.name()
                                ).put(
                                        "verticesUri",
                                        new JSONArray(Arrays.asList(vertexUri))
                                )
                        )
        ).get();

    }

    private ClientResponse removeCollection(URI... vertexUri) {
        return resource
                .path(vertexUtils().getVertexBaseUri())
                .path("collection")
                .cookie(authCookie)
                .delete(
                        ClientResponse.class,
                        new JSONArray(Arrays.asList(vertexUri))
                );
    }
}
