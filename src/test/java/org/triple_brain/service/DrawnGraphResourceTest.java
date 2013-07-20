package org.triple_brain.service;

import com.sun.jersey.api.client.ClientResponse;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;
import org.triple_brain.module.model.json.drawn_graph.DrawnGraphJSONFields;
import org.triple_brain.module.model.json.drawn_graph.DrawnVertexJSONFields;
import org.triple_brain.service.utils.GraphManipulationRestTest;

import java.net.URI;

import static org.junit.Assert.assertTrue;

/**
 * Copyright Mozilla Public License 1.1
 */

public class DrawnGraphResourceTest extends GraphManipulationRestTest {

    @Test
    public void can_get_drawn_graph(){
        JSONObject drawnGraph = getDrawnGraphOfCentralVertexUriAtDepth(
                vertexAUri(),
                5
        ).getEntity(JSONObject.class);
        JSONObject vertex = drawnGraph.optJSONObject(
                DrawnGraphJSONFields.VERTICES
        ).optJSONObject(vertexAUri().toString());
        assertTrue(vertex.has(
                DrawnVertexJSONFields.POSITION
        ));
    }

    private ClientResponse getDrawnGraphOfCentralVertexUriAtDepth(URI centralVertexUri, Integer depth){
        return resource
                .path(centralVertexUri.getPath())
                .path("surround_graph")
                .path(depth.toString())
                .path("drawn")
                .cookie(authCookie)
                .get(ClientResponse.class);
    }

}
