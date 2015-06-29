/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.vertex;

import com.sun.jersey.api.client.ClientResponse;
import guru.bubl.service.utils.GraphManipulationRestTestUtils;
import org.junit.Ignore;
import org.junit.Test;

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
