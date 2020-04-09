package guru.bubl.service.resources;

import com.sun.jersey.api.client.ClientResponse;
import guru.bubl.module.model.User;
import guru.bubl.module.model.center_graph_element.CenterGraphElementPojo;
import guru.bubl.module.model.graph.subgraph.SubGraphPojo;
import guru.bubl.module.model.graph.tag.TagPojo;
import guru.bubl.module.model.graph.fork.NbNeighbors;
import guru.bubl.module.model.tag.TagJson;
import guru.bubl.service.SessionHandler;
import guru.bubl.service.utils.GraphManipulationRestTestUtils;
import guru.bubl.test.module.utils.ModelTestScenarios;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class GraphResourceTest extends GraphManipulationRestTestUtils {
    @Test
    public void remove_center_returns_no_content_status() {
        graphUtils().graphWithCenterVertexUri(vertexA().uri());
        List<CenterGraphElementPojo> centerElements = graphUtils().getCenterGraphElements();
        assertFalse(
                centerElements.isEmpty()
        );
        assertThat(
                removeCenter(defaultAuthenticatedUser, centerElements.iterator().next()).getStatus(),
                is(Response.Status.NO_CONTENT.getStatusCode())
        );
    }

    @Test
    public void can_remove_centers() {
        graphUtils().graphWithCenterVertexUri(vertexA().uri());
        List<CenterGraphElementPojo> centerElements = graphUtils().getCenterGraphElements();
        assertFalse(
                centerElements.isEmpty()
        );
        removeCenter(defaultAuthenticatedUser, centerElements.iterator().next());
        centerElements = graphUtils().getCenterGraphElements();
        assertTrue(
                centerElements.isEmpty()
        );
    }

    @Test
    public void cannot_remove_centers_of_another_user() {
        graphUtils().graphWithCenterVertexUri(vertexA().uri());
        List<CenterGraphElementPojo> centerElements = graphUtils().getCenterGraphElements();
        Integer nbCenters = centerElements.size();
        createAUser();
        ClientResponse response = removeCenter(defaultAuthenticatedUser, centerElements.iterator().next());
        assertThat(
                response.getStatus(),
                is(Response.Status.FORBIDDEN.getStatusCode())
        );
        authenticate(defaultAuthenticatedUser);
        centerElements = graphUtils().getCenterGraphElements();
        assertThat(
                centerElements.size(),
                is(nbCenters)
        );
    }


    @Test
    public void set_nb_neighbors_returns_no_content_status() throws JSONException {
        ClientResponse response = setNbNeighbors(
                vertexAUri(),
                new JSONObject().put(
                        "private_", 1
                ).put(
                        "friend", 2
                ).put(
                        "public_", 3
                )
        );
        assertThat(
                response.getStatus(),
                is(Response.Status.NO_CONTENT.getStatusCode())
        );
    }

    @Test
    public void set_nb_neighbors_does_the_job() throws JSONException {
        SubGraphPojo subGraph = graphUtils().graphWithCenterVertexUri(
                vertexAUri()
        );
        NbNeighbors nbNeighbors = subGraph.vertexWithIdentifier(vertexAUri()).getNbNeighbors();
        assertThat(
                nbNeighbors.getPrivate(),
                is(1)
        );
        assertThat(
                nbNeighbors.getFriend(),
                is(0)
        );
        assertThat(
                nbNeighbors.getPublic(),
                is(0)
        );
        setNbNeighbors(
                vertexAUri(),
                new JSONObject().put(
                        "private_", 3
                ).put(
                        "friend", 2
                ).put(
                        "public_", 1
                )
        );
        subGraph = graphUtils().graphWithCenterVertexUri(
                vertexAUri()
        );
        nbNeighbors = subGraph.vertexWithIdentifier(vertexAUri()).getNbNeighbors();
        assertThat(
                nbNeighbors.getPrivate(),
                is(3)
        );
        assertThat(
                nbNeighbors.getFriend(),
                is(2)
        );
        assertThat(
                nbNeighbors.getPublic(),
                is(1)
        );
    }

    @Test
    public void can_set_nb_neighbors_of_tags() throws JSONException {
        TagPojo tag = TagJson.fromJson(
                graphElementUtils().addTagToGraphElementWithUri(
                        new ModelTestScenarios().person(),
                        vertexAUri()
                ).getEntity(String.class)
        ).values().iterator().next();
        SubGraphPojo subGraph = graphUtils().graphWithCenterVertexUri(
                tag.uri()
        );
        NbNeighbors nbNeighbors = subGraph.getCenterMeta().getNbNeighbors();
        assertThat(
                nbNeighbors.getPrivate(),
                is(1)
        );
        assertThat(
                nbNeighbors.getFriend(),
                is(0)
        );
        assertThat(
                nbNeighbors.getPublic(),
                is(0)
        );
        ClientResponse response = setNbNeighbors(
                tag.uri(),
                new JSONObject().put(
                        "private_", 3
                ).put(
                        "friend", 2
                ).put(
                        "public_", 1
                )
        );
        assertThat(
                response.getStatus(),
                is(Response.Status.NO_CONTENT.getStatusCode())
        );
        subGraph = graphUtils().graphWithCenterVertexUri(
                tag.uri()
        );
        nbNeighbors = subGraph.getCenterMeta().getNbNeighbors();
        assertThat(
                nbNeighbors.getPrivate(),
                is(3)
        );
        assertThat(
                nbNeighbors.getFriend(),
                is(2)
        );
        assertThat(
                nbNeighbors.getPublic(),
                is(1)
        );
    }

    private ClientResponse removeCenter(User user, CenterGraphElementPojo center) {
        return resource
                .path(center.getGraphElement().uri().getPath())
                .path("center")
                .cookie(authCookie)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .header(SessionHandler.X_XSRF_TOKEN, currentXsrfToken)
                .delete(ClientResponse.class);
    }


    private ClientResponse setNbNeighbors(URI uri, JSONObject nbNeighbors) {
        return resource
                .path(uri.getPath())
                .path("nbNeighbors")
                .cookie(authCookie)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .header(SessionHandler.X_XSRF_TOKEN, currentXsrfToken)
                .post(ClientResponse.class, nbNeighbors);
    }
}
