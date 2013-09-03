package org.triple_brain.service.vertex;

import com.sun.jersey.api.client.ClientResponse;
import org.junit.Test;
import org.triple_brain.module.model.json.graph.VertexJson;
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
        return vertexUtils().makePublicVertexWithUri(
                vertexAUri()
        );
    }

    private ClientResponse makePrivate(){
        return vertexUtils().makePrivateVertexWithUri(
                vertexAUri()
        );
    }

    private boolean isVertexAPublic(){
        return vertexA().optBoolean(
                VertexJson.IS_PUBLIC
        );
    }

}
