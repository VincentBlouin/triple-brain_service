/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.center;

import com.sun.jersey.api.client.ClientResponse;
import guru.bubl.module.model.User;
import guru.bubl.module.model.center_graph_element.CenterGraphElementPojo;
import guru.bubl.module.model.json.JsonUtils;
import guru.bubl.service.utils.GraphManipulationRestTestUtils;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class CenterGraphElementResourceTest extends GraphManipulationRestTestUtils {

    @Test
    public void getting_center_graph_elements_returns_ok_status(){
        assertThat(
                graphUtils().getCenterGraphElementsResponse().getStatus(),
                is(Response.Status.OK.getStatusCode())
        );
    }
    @Test
    public void cannot_get_private_center_elements_of_another_user(){
        assertThat(
                graphUtils().getCenterGraphElementsResponseForGraphElementTypeAndUser(defaultAuthenticatedUser).getStatus(),
                is(
                        Response.Status.OK.getStatusCode()
                )
        );
        createAUser();
        assertThat(
                graphUtils().getCenterGraphElementsResponseForGraphElementTypeAndUser(defaultAuthenticatedUser).getStatus(),
                is(Response.Status.FORBIDDEN.getStatusCode())
        );
    }

    @Test
    public void returns_center_elements(){
        graphUtils().graphWithCenterVertexUri(vertexA().uri());

        assertFalse(
                graphUtils().getCenterGraphElements().isEmpty()
        );
    }

    @Test
    public void remove_center_returns_no_content_status(){
        graphUtils().graphWithCenterVertexUri(vertexA().uri());
        Set<CenterGraphElementPojo> centerElements = graphUtils().getCenterGraphElements();
        assertFalse(
                centerElements.isEmpty()
        );
        assertThat(
                removeCenters(defaultAuthenticatedUser, centerElements).getStatus(),
                is(Response.Status.NO_CONTENT.getStatusCode())
        );
    }

    @Test
    public void can_remove_centers(){
        graphUtils().graphWithCenterVertexUri(vertexA().uri());
        Set<CenterGraphElementPojo> centerElements = graphUtils().getCenterGraphElements();
        assertFalse(
                centerElements.isEmpty()
        );
        removeCenters(defaultAuthenticatedUser, centerElements);
        centerElements = graphUtils().getCenterGraphElements();
        assertTrue(
                centerElements.isEmpty()
        );
    }

    @Test
    public void cannot_remove_centers_of_another_user(){
        graphUtils().graphWithCenterVertexUri(vertexA().uri());
        Set<CenterGraphElementPojo> centerElements = graphUtils().getCenterGraphElements();
        Integer nbCenters = centerElements.size();
        createAUser();
        ClientResponse response = removeCenters(defaultAuthenticatedUser, centerElements);
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

    private ClientResponse removeCenters(User user, Set<CenterGraphElementPojo> centers) {
        Set<URI> uris = new HashSet<>();
        for(CenterGraphElementPojo centerGraphElementPojo: centers){
            uris.add(
                    centerGraphElementPojo.getGraphElement().uri()
            );
        }
        return resource
                .path(user.id())
                .path("center-elements")
                .cookie(authCookie)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .delete(ClientResponse.class, JsonUtils.getGson().toJson(uris));
    }
}
