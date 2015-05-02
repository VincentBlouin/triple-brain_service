/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package org.triple_brain.service.vertex;

import org.junit.Test;
import org.triple_brain.service.utils.GraphManipulationRestTestUtils;

import javax.ws.rs.core.Response;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class VertexCollectionPublicAccessResourceTest extends GraphManipulationRestTestUtils {

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