package org.triple_brain.service.vertex;

import com.sun.jersey.api.client.ClientResponse;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;
import org.triple_brain.module.model.json.graph.VertexJson;
import org.triple_brain.service.utils.GraphManipulationRestTest;

import javax.ws.rs.core.Response;
import java.net.URI;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

/*
* Copyright Mozilla Public License 1.1
*/
public class VertexGroupResourceTest extends GraphManipulationRestTest{

    @Test
    public void create_group_returns_correct_status(){
        assertThat(
                createVertexBAndCGroup().getStatus(),
                is(Response.Status.CREATED.getStatusCode())
        );
    }

    @Test
    public void creating_group_returns_uri_of_created_group(){
        assertFalse(
                null == createVertexBAndCGroup().getHeaders().get("Location").get(0)
        );
    }

    @Test
    public void can_list_included_graph_elements_when_getting_group_vertex(){
        URI groupVertexUri = URI.create(createVertexBAndCGroup()
                .getHeaders()
                .get("Location")
                .get(0));
        JSONObject newVertex = vertexUtils().vertexWithUriOfCurrentUser(
                groupVertexUri
        );
        JSONObject includedVerticesUri = newVertex.optJSONObject(
                VertexJson.INCLUDED_VERTICES
        );
        assertThat(
                includedVerticesUri.length(),
                is(2)
        );
        JSONArray includedEdges = newVertex.optJSONArray(
                VertexJson.INCLUDED_EDGES
        );
        assertThat(
                includedEdges.length(),
                is(0)
        );
    }

    private ClientResponse createVertexBAndCGroup(){
        try{
            JSONObject vertices = new JSONObject().put(
                    vertexBUri().toString(),
                    ""
            ).put(
                    vertexCUri().toString(),
                    ""
            );
            JSONObject uris = new JSONObject().put(
                    "vertices",
                    vertices
            ).put(
                    "edges",
                    new JSONObject()
            );
            return resource
                    .path(groupVerticesUri())
                    .cookie(authCookie)
                    .post(ClientResponse.class, uris);
        }catch(JSONException e){
            throw new RuntimeException(e);
        }
    }

    private String groupVerticesUri(){
        return vertexUtils().baseVertexUri().getPath() + "/group";
    }
}
