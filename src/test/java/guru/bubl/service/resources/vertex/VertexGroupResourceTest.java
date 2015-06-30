/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.vertex;

import com.sun.jersey.api.client.ClientResponse;
import guru.bubl.service.utils.GraphManipulationRestTestUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;
import guru.bubl.module.model.graph.edge.Edge;
import guru.bubl.module.model.graph.vertex.Vertex;
import guru.bubl.module.model.graph.vertex.VertexInSubGraph;

import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

public class VertexGroupResourceTest extends GraphManipulationRestTestUtils {

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
        VertexInSubGraph newVertex = vertexUtils().vertexWithUriOfCurrentUser(
                groupVertexUri
        );
        Map<URI, ?extends Vertex> includedVertices = newVertex.getIncludedVertices();
        assertThat(
                includedVertices.size(),
                is(2)
        );
        Map<URI, ?extends  Edge> includedEdges = newVertex.getIncludedEdges();
        assertThat(
                includedEdges.size(),
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