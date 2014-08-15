package org.triple_brain.service.vertex;

import com.sun.jersey.api.client.ClientResponse;
import org.codehaus.jettison.json.JSONArray;
import org.junit.Test;
import org.triple_brain.service.utils.GraphManipulationRestTest;

import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

/**
 * Copyright Mozilla Public License 1.1
 */
public class VertexCollectionPublicAccessResourceTest extends GraphManipulationRestTest {

    @Test
    public void making_public_returns_correct_status() {
        assertThat(
                makePublicVerticesWithUri().getStatus(),
                is(Response.Status.OK.getStatusCode())
        );
    }

    @Test
    public void can_make_vertices_collection_public() {
        assertFalse(vertexA().isPublic());
        assertFalse(vertexB().isPublic());
        makePublicVerticesWithUri(
                vertexAUri(),
                vertexBUri()
        );
        assertTrue(vertexA().isPublic());
        assertTrue(vertexB().isPublic());
    }

    @Test
    public void can_make_vertices_collection_private() {
        makePublicVerticesWithUri(
                vertexAUri(),
                vertexBUri()
        );
        assertTrue(vertexA().isPublic());
        assertTrue(vertexB().isPublic());
        makePrivateVerticesWithUri(
                vertexAUri(),
                vertexBUri()
        );
        assertFalse(vertexA().isPublic());
        assertFalse(vertexB().isPublic());
    }

    private ClientResponse makePublicVerticesWithUri(URI... vertexUri) {
        return makePublicOrPrivateVerticesWithUri(true, vertexUri);
    }

    private ClientResponse makePrivateVerticesWithUri(URI... vertexUri) {
        return makePublicOrPrivateVerticesWithUri(false, vertexUri);
    }

    private ClientResponse makePublicOrPrivateVerticesWithUri(Boolean makePublic, URI... vertexUri) {
        return resource
                .path(getVertexBaseUri())
                .path("collection")
                .path("public_access")
                .queryParam("type", makePublic ? "public" : "private")
                .cookie(authCookie).post(
                        ClientResponse.class,
                        new JSONArray(Arrays.asList(vertexUri))
                );
    }
}