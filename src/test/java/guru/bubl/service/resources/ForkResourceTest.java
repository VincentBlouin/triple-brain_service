/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources;

import com.sun.jersey.api.client.ClientResponse;
import guru.bubl.module.model.UserUris;
import guru.bubl.module.model.graph.edge.Edge;
import guru.bubl.module.model.graph.edge.EdgeJson;
import guru.bubl.module.model.graph.edge.EdgePojo;
import guru.bubl.module.model.graph.vertex.Vertex;
import guru.bubl.module.model.graph.vertex.VertexJson;
import guru.bubl.module.model.json.StatementJsonFields;
import guru.bubl.service.SessionHandler;
import guru.bubl.service.utils.GraphManipulationRestTestUtils;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertTrue;


public class ForkResourceTest extends GraphManipulationRestTestUtils {
    @Test
    public void adding_a_vertex_returns_correct_status() {
        ClientResponse response = vertexUtils().addAVertexToForkWithUri(
                vertexAUri()
        );
        assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
    }

    @Test
    public void add_vertex_and_relation_to_group_relation_returns_ok_status() {
        ClientResponse response = vertexUtils().addAVertexToForkWithUri(
                graphUtils().getTodoGroupRelation().uri()
        );
        assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
    }

    @Test
    public void can_add_a_vertex_and_relation() {
        int numberOfConnectedEdges = vertexUtils().connectedEdgesOfVertexWithURI(
                vertexAUri()
        ).size();
        vertexUtils().addAVertexToForkWithUri(vertexAUri());
        int updatedNumberOfConnectedEdges = vertexUtils().connectedEdgesOfVertexWithURI(
                vertexAUri()
        ).size();
        assertThat(updatedNumberOfConnectedEdges, is(numberOfConnectedEdges + 1));
    }

    @Test
    public void adding_a_vertex_returns_the_new_edge_and_vertex_id() throws Exception {
        ClientResponse response = vertexUtils().addAVertexToForkWithUri(vertexAUri());
        JSONObject createdStatement = response.getEntity(JSONObject.class);
        Vertex subject = VertexJson.fromJson(
                createdStatement.getJSONObject(
                        StatementJsonFields.source_vertex.name()
                )
        );
        assertThat(
                subject.uri().toString(),
                is(vertexAUri().toString())
        );
        Edge newEdge = EdgeJson.fromJson(
                createdStatement.getJSONObject(
                        StatementJsonFields.edge.name()
                )
        );
        Vertex newVertex = VertexJson.fromJson(
                createdStatement.getJSONObject(
                        StatementJsonFields.end_vertex.name()
                )
        );
        Set<Edge> edgesOfVertexA = vertexUtils().connectedEdgesOfVertexWithURI(
                vertexAUri()
        );
        assertTrue(
                edgesOfVertexA.contains(
                        newEdge
                )
        );
        assertTrue(
                vertexUtils().vertexWithUriHasDestinationVertexWithUri(
                        vertexAUri(),
                        newVertex.uri()
                )
        );
    }

    @Test
    public void adding_a_vertex_returns_the_new_edge_and_vertex_creation_and_last_modification_date() throws Exception {
        ClientResponse response = vertexUtils().addAVertexToForkWithUri(vertexAUri());
        JSONObject createdStatement = response.getEntity(JSONObject.class);
        EdgePojo newEdge = EdgeJson.fromJson(
                createdStatement.getJSONObject(
                        StatementJsonFields.edge.name()
                )
        );
        assertThat(
                newEdge.creationDate(),
                is(not(nullValue()))
        );
        assertThat(
                newEdge.lastModificationDate(),
                is(not(nullValue()))
        );
        Vertex newVertex = VertexJson.fromJson(
                createdStatement.getJSONObject(
                        StatementJsonFields.end_vertex.name()
                )
        );
        assertThat(
                newVertex.creationDate(),
                is(not(nullValue()))
        );
        assertThat(
                newVertex.lastModificationDate(),
                is(not(nullValue()))
        );
    }

    @Test
    public void cannot_add_a_vertex_that_user_doesnt_own(){
        authenticate(createAUser());
        ClientResponse response = resource
                .path(
                        vertexAUri().getPath()
                )
                .cookie(authCookie)
                .post(ClientResponse.class);
        assertThat(response.getStatus(), is(Response.Status.FORBIDDEN.getStatusCode()));
    }

    @Test
    public void can_specify_id_when_adding_a_vertex_and_relation_with() throws Exception {
        UserUris userUris = new UserUris(defaultAuthenticatedUser);
        URI vertexUri = userUris.generateVertexUri();
        URI edgeUri = userUris.generateEdgeUri();
        JSONObject options = new JSONObject().put(
                "vertexId", UserUris.graphElementShortId(vertexUri)
        ).put("edgeId", UserUris.graphElementShortId(edgeUri));
        ClientResponse response = resource
                .path(
                        vertexAUri().getPath()
                )
                .cookie(authCookie)
                .header(SessionHandler.X_XSRF_TOKEN, currentXsrfToken)
                .post(ClientResponse.class, options);
        assertThat(
                response.getStatus(),
                is(Response.Status.OK.getStatusCode())
        );
        JSONObject createdStatement = response.getEntity(JSONObject.class);
        EdgePojo newEdge = EdgeJson.fromJson(
                createdStatement.getJSONObject(
                        StatementJsonFields.edge.name()
                )
        );
        assertThat(
                newEdge.uri(),
                is(edgeUri)
        );
        assertThat(
                newEdge.destinationUri(),
                is(vertexUri)
        );
    }
}

