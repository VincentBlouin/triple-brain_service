/*
 * Copyright Vincent Blouin under the Mozilla Public License 1.1
 */

package org.triple_brain.service.vertex;

import com.sun.jersey.api.client.ClientResponse;
import org.junit.Ignore;
import org.junit.Test;

import org.triple_brain.service.utils.GraphManipulationRestTestUtils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class VertexPublicAccessResourceTest extends GraphManipulationRestTestUtils {

    @Test
    public void can_make_public(){
        assertFalse(vertexB().isPublic());
        makePublic();
        assertTrue(vertexB().isPublic());
    }

    @Test
    @Ignore
    public void can_make_private_again(){
        makePublic();
        assertTrue(vertexB().isPublic());
        makePrivate();
        assertFalse(vertexB().isPublic());
    }

    private ClientResponse makePublic(){
        return vertexUtils().makePublicVertexWithUri(
                vertexBUri()
        );
    }

    private ClientResponse makePrivate(){
        return vertexUtils().makePrivateVertexWithUri(
                vertexBUri()
        );
    }
}
