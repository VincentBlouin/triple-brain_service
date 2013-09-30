package org.triple_brain.service.vertex;

import com.sun.jersey.api.client.ClientResponse;
import org.codehaus.jettison.json.JSONArray;
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
    public void can_list_included_vertices_when_getting_group_vertex(){
        URI groupVertexUri = URI.create(createVertexBAndCGroup()
                .getHeaders()
                .get("Location")
                .get(0));
        JSONObject newVertex = vertexUtils().vertexWithUriOfCurrentUser(
                groupVertexUri
        );
        JSONArray includedVerticesUri = newVertex.optJSONArray(
                VertexJson.INCLUDED_VERTICES_URI
        );
        assertThat(
                includedVerticesUri.length(),
                is(2)
        );
    }

    private ClientResponse createVertexBAndCGroup(){
        JSONArray bAndCUris = new JSONArray().put(
                vertexBUri().toString()
        ).put(
                vertexCUri().toString()
        );
        return resource
                .path(groupVerticesUri())
                .cookie(authCookie)
                .post(ClientResponse.class, bAndCUris);
    }

    private String groupVerticesUri(){
        return vertexUtils().baseVertexUri().getPath() + "/group";
    }
}
