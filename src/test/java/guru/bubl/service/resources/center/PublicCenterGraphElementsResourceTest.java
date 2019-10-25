/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.center;

import com.sun.jersey.api.client.ClientResponse;
import guru.bubl.module.model.center_graph_element.CenterGraphElementPojo;
import guru.bubl.service.utils.GraphManipulationRestTestUtils;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

public class PublicCenterGraphElementsResourceTest extends GraphManipulationRestTestUtils {

    @Test
    public void getting_public_center_graph_elements_returns_ok_status() {
        assertThat(
                graphUtils().getPublicCenterGraphElementsResponse().getStatus(),
                is(
                        Response.Status.OK.getStatusCode()
                )
        );
    }

    @Test
    public void returns_only_public_centers() {
        graphUtils().graphWithCenterVertexUri(vertexA().uri());
        List<CenterGraphElementPojo> centerElements = graphUtils().getCenterGraphElements();
        assertFalse(
                centerElements.isEmpty()
        );
        List<CenterGraphElementPojo> centers = graphUtils().getCenterGraphElementsFromClientResponse(
                graphUtils().getPublicCenterGraphElementsResponse()
        );
        assertThat(
                centers.size(),
                is(0)
        );
        vertexUtils().makePublicVertexWithUri(
                vertexAUri()
        );
        centers = graphUtils().getCenterGraphElementsFromClientResponse(
                graphUtils().getPublicCenterGraphElementsResponse()
        );
        assertThat(
                centers.size(),
                is(1)
        );
    }


    @Test
    public void can_get_public_centers_of_another_user() {
        graphUtils().graphWithCenterVertexUri(vertexA().uri());
        vertexUtils().makePublicVertexWithUri(
                vertexAUri()
        );
        createAUser();
        ClientResponse response = graphUtils().getPublicCenterGraphElementsResponseForUser(
                defaultAuthenticatedUser
        );
        assertThat(
                response.getStatus(),
                is(
                        Response.Status.OK.getStatusCode()
                )
        );
        List<CenterGraphElementPojo> centers = graphUtils().getCenterGraphElementsFromClientResponse(
                response
        );
        assertThat(
                centers.size(),
                is(1)
        );
    }
}
