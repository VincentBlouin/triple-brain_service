/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.vertex;

import com.sun.jersey.api.client.ClientResponse;
import guru.bubl.module.model.center_graph_element.CenterGraphElementPojo;
import guru.bubl.module.model.graph.SubGraph;
import guru.bubl.module.model.json.graph.SubGraphJson;
import guru.bubl.service.utils.GraphManipulationRestTestUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;

import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class VertexOwnedSurroundGraphResouceTest extends GraphManipulationRestTestUtils {

    @Test
    public void response_status_is_ok_for_getting_graph() {
        assertThat(
                getGraph().getStatus(),
                is(Response.Status.OK.getStatusCode())
        );
    }

    @Test
    public void can_get_graph() {
        SubGraph graph = SubGraphJson.fromJson(
                getGraph().getEntity(JSONObject.class)
        );
        assertThat(
                graph.vertices().size(),
                is(2)
        );
    }

    @Test
    public void cant_get_graph_using_central_vertex_of_another_user() {
        NewCookie newCookie = authenticate(
                createAUser()
        ).getCookies().get(0);
        JSONArray vertices = graphUtils().makeGraphHave3SerialVerticesWithLongLabelsUsingCookie(
                newCookie
        );
        URI anotherUserVertexUri = vertexUtils().uriOfVertex(
                vertices.optJSONObject(0)
        );
        authenticate(defaultAuthenticatedUser);
        ClientResponse response = getGraphOfCentralVertexUri(
                anotherUserVertexUri
        );
        assertThat(
                response.getStatus(),
                is(Response.Status.FORBIDDEN.getStatusCode())
        );
    }

    @Test
    public void can_get_sub_graph() throws Exception {
        SubGraph graph = SubGraphJson.fromJson(getGraphOfCentralVertexUri(
                vertexBUri()
        ).getEntity(JSONObject.class));
        assertThat(
                graph.vertices().size(),
                is(3)
        );
        graph = SubGraphJson.fromJson(getGraphOfCentralVertexUri(
                vertexAUri()
        ).getEntity(JSONObject.class));
        assertThat(
                graph.vertices().size(),
                is(2)
        );
    }

    @Test
    public void cannot_get_surround_graph_of_a_vertex_after_its_been_removed() throws Exception {
        ClientResponse response = getGraphOfCentralVertexUri(vertexBUri());
        assertThat(
                response.getStatus(),
                is(Response.Status.OK.getStatusCode())
        );
        vertexUtils().removeVertexB();
        response = getGraphOfCentralVertexUri(vertexBUri());
        assertThat(
                response.getStatus(),
                is(Response.Status.NOT_FOUND.getStatusCode())
        );
    }

    @Test
    public void getting_graph_increments_number_of_visits_for_center_vertex() {
        Set<CenterGraphElementPojo> centerGraphElements = graphUtils().getCenterGraphElements();
        assertThat(
                centerGraphElements.size(),
                is(1)
        );
        assertThat(
                centerGraphElements.iterator().next().getNumberOfVisits(),
                is(1)
        );
        getGraphOfCentralVertexUri(vertexAUri());
        centerGraphElements = graphUtils().getCenterGraphElements();
        assertThat(
                centerGraphElements.size(),
                is(1)
        );
        assertThat(
                centerGraphElements.iterator().next().getNumberOfVisits(),
                is(2)
        );
    }

    private ClientResponse getGraph() {
        return getGraphOfCentralVertexUri(
                vertexAUri()
        );
    }

    private ClientResponse getGraphOfCentralVertexUri(URI centralVertexUri) {
        return resource
                .path(centralVertexUri.getPath())
                .path("surround_graph")
                .cookie(authCookie)
                .get(ClientResponse.class);
    }
}
