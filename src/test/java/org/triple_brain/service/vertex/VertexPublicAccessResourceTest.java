package org.triple_brain.service.vertex;

import com.sun.jersey.api.client.ClientResponse;
import org.junit.Test;
import org.triple_brain.module.model.json.graph.VertexJsonFields;
import org.triple_brain.service.utils.GraphManipulationRestTest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/*
* Copyright Mozilla Public License 1.1
*/
public class VertexPublicAccessResourceTest extends GraphManipulationRestTest{

    @Test
    public void can_make_public(){
        assertFalse(isVertexAPublic());
        makePublic();
        assertTrue(isVertexAPublic());
    }

    @Test
    public void can_make_private_again(){
        makePublic();
        assertTrue(isVertexAPublic());
        makePrivate();
        assertFalse(isVertexAPublic());
    }

    private ClientResponse makePublic(){
        return resource
                .path(vertexAUri().getPath())
                .path("public_access")
                .cookie(authCookie)
                .post(ClientResponse.class);
    }

    private ClientResponse makePrivate(){
        return resource
                .path(vertexAUri().getPath())
                .path("public_access")
                .cookie(authCookie)
                .delete(ClientResponse.class);
    }

    private boolean isVertexAPublic(){
        return vertexA().optBoolean(
                VertexJsonFields.IS_PUBLIC
        );
    }

}
