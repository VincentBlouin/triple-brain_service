package guru.bubl.service.resources;

import com.sun.jersey.api.client.ClientResponse;
import guru.bubl.module.model.User;
import guru.bubl.module.model.center_graph_element.CenterGraphElementPojo;
import guru.bubl.service.utils.GraphManipulationRestTestUtils;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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

    private ClientResponse removeCenter(User user, CenterGraphElementPojo center) {
        return resource
                .path(center.getGraphElement().uri().getPath())
                .path("center")
                .cookie(authCookie)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .delete(ClientResponse.class);
    }
}
