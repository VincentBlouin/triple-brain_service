/*
 * Copyright Vincent Blouin under the Mozilla Public License 1.1
 */

package org.triple_brain.service.vertex;

import com.sun.jersey.api.client.ClientResponse;
import org.junit.Test;

import org.triple_brain.service.utils.GraphManipulationRestTest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class VertexPublicAccessResourceTest extends GraphManipulationRestTest{

    @Test
    public void can_make_public(){
        assertFalse(vertexA().isPublic());
        makePublic();
        assertTrue(vertexA().isPublic());
    }

    @Test
    public void can_make_private_again(){
        makePublic();
        assertTrue(vertexA().isPublic());
        makePrivate();
        assertFalse(vertexA().isPublic());
    }

    private ClientResponse makePublic(){
        return vertexUtils().makePublicVertexWithUri(
                vertexAUri()
        );
    }

    private ClientResponse makePrivate(){
        return vertexUtils().makePrivateVertexWithUri(
                vertexAUri()
        );
    }
}
