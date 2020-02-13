/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources;

import com.sun.jersey.api.client.ClientResponse;
import guru.bubl.module.model.graph.ShareLevel;
import guru.bubl.module.model.graph.subgraph.SubGraphPojo;
import guru.bubl.module.model.graph.vertex.Vertex;
import guru.bubl.service.utils.GraphManipulationRestTestUtils;
import org.codehaus.jettison.json.JSONArray;
import org.junit.Test;

import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Arrays;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class GraphElementCollectionResourceTest extends GraphManipulationRestTestUtils {

    @Test
    public void can_set_share_level_of_multiple_graph_elements() {
        assertThat(
                vertexA().getShareLevel(),
                is(ShareLevel.PRIVATE)
        );
        assertThat(
                vertexB().getShareLevel(),
                is(ShareLevel.PRIVATE)
        );
        assertThat(
                vertexC().getShareLevel(),
                is(ShareLevel.PRIVATE)
        );
        ClientResponse response = graphElementUtils().setShareLevelOfCollection(
                ShareLevel.FRIENDS,
                authCookie,
                vertexAUri(),
                vertexBUri()
        );
        assertThat(
                response.getStatus(),
                is(Response.Status.NO_CONTENT.getStatusCode())
        );
        assertThat(
                vertexA().getShareLevel(),
                is(ShareLevel.FRIENDS)
        );
        assertThat(
                vertexB().getShareLevel(),
                is(ShareLevel.FRIENDS)
        );
        assertThat(
                vertexC().getShareLevel(),
                is(ShareLevel.PRIVATE)
        );
    }

    @Test
    public void cannot_set_share_level_of_not_owned_multiple_graph_elements() {
        assertThat(
                vertexA().getShareLevel(),
                is(ShareLevel.PRIVATE)
        );
        assertThat(
                vertexB().getShareLevel(),
                is(ShareLevel.PRIVATE)
        );
        assertThat(
                vertexC().getShareLevel(),
                is(ShareLevel.PRIVATE)
        );
        ClientResponse response = graphElementUtils().setShareLevelOfCollection(
                ShareLevel.PUBLIC,
                authenticate(
                        createAUser()
                ).getCookies().get(0),
                vertexAUri(),
                vertexBUri()
        );
        assertThat(
                response.getStatus(), is(
                        Response.Status.FORBIDDEN.getStatusCode()
                )
        );
        assertThat(
                vertexA().getShareLevel(),
                is(ShareLevel.PRIVATE)
        );
        assertThat(
                vertexB().getShareLevel(),
                is(ShareLevel.PRIVATE)
        );
    }
}
