/*
 * Copyright Vincent Blouin under the Mozilla Public License 1.1
 */

package org.triple_brain.service.vertex;

import org.junit.Test;
import org.triple_brain.service.utils.GraphManipulationRestTest;

import javax.ws.rs.core.Response;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class VertexCollectionPublicAccessResourceTest extends GraphManipulationRestTest {

    @Test
    public void making_public_returns_correct_status() {
        assertThat(
                vertexUtils().makePublicVerticesWithUri().getStatus(),
                is(Response.Status.NO_CONTENT.getStatusCode())
        );
    }

    @Test
    public void can_make_vertices_collection_public() {
        assertFalse(vertexA().isPublic());
        assertFalse(vertexB().isPublic());
        vertexUtils().makePublicVerticesWithUri(
                vertexAUri(),
                vertexBUri()
        );
        assertTrue(vertexA().isPublic());
        assertTrue(vertexB().isPublic());
    }

    @Test
    public void can_make_vertices_collection_private() {
        vertexUtils().makePublicVerticesWithUri(
                vertexAUri(),
                vertexBUri()
        );
        assertTrue(vertexA().isPublic());
        assertTrue(vertexB().isPublic());
        vertexUtils().makePrivateVerticesWithUri(
                vertexAUri(),
                vertexBUri()
        );
        assertFalse(vertexA().isPublic());
        assertFalse(vertexB().isPublic());
    }

}