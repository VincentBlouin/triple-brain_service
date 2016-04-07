/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.center;

import guru.bubl.service.utils.GraphManipulationRestTestUtils;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

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
                graphUtils().getCenterGraphElementsResponseForUser(defaultAuthenticatedUser).getStatus(),
                is(
                        Response.Status.OK.getStatusCode()
                )
        );
        createAUser();
        assertThat(
                graphUtils().getCenterGraphElementsResponseForUser(defaultAuthenticatedUser).getStatus(),
                is(Response.Status.FORBIDDEN.getStatusCode())
        );
    }

    @Test
    public void returns_center_elements(){
        assertFalse(
                graphUtils().getCenterGraphElements().isEmpty()
        );
    }
}
