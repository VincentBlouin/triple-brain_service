/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources;

import com.sun.jersey.api.client.ClientResponse;
import guru.bubl.module.model.graph.identification.IdentificationPojo;
import guru.bubl.service.utils.GraphManipulationRestTestUtils;
import guru.bubl.test.module.utils.ModelTestScenarios;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;

import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class AdminResourceTest extends GraphManipulationRestTestUtils {

    @Test
    public void cant_perform_admin_tasks_if_logged_in_as_vince_with_capital_letter() {
        assertThat(
                reindexAll(
                        loginAsVinceWithCapitalLetter()
                ).getStatus(),
                is(
                        Response.Status.FORBIDDEN.getStatusCode()
                )
        );
    }

    @Test
    public void indexing_whole_graph_returns_no_content_status() {
        assertThat(
                reindexAll(
                        loginAsVince()
                ).getStatus(),
                is(
                        Response.Status.NO_CONTENT.getStatusCode()
                )
        );
    }

    @Test
    public void can_refresh_number_of_connected_edges() throws Exception {
        setNumberOfConnectedOfAllVerticesToZero();
        assertThat(
                vertexB().getNumberOfConnectedEdges(),
                is(0)
        );
        refreshNumberOfConnectedEdges(
                loginAsVince()
        );
        authenticate(defaultAuthenticatedUserAsJson);
        assertThat(
                vertexB().getNumberOfConnectedEdges(),
                is(2)
        );
    }


    @Test
    public void refreshing_identifications_number_of_references_returns_ok_status() throws Exception {
        graphElementUtils().addFoafPersonTypeToVertexA();
        IdentificationPojo possession = new ModelTestScenarios().possessionIdentification();
        possession.setRelationExternalResourceUri(
                ModelTestScenarios.SAME_AS
        );
        graphElementUtils().addIdentificationToGraphElementWithUri(
                possession,
                edgeUtils().edgeBetweenAAndB().uri()
        );
        ClientResponse response = resource.path("service")
                .path("users")
                .path("vince")
                .path("admin")
                .path("refresh_identifications_nb_references")
                .cookie(loginAsVince())
                .post(ClientResponse.class);
        assertThat(
                response.getStatus(),
                is(Response.Status.OK.getStatusCode())
        );
    }

    @Test
    public void re_adding_identifications_returns_ok_status() throws Exception {
        graphElementUtils().addFoafPersonTypeToVertexA();
        IdentificationPojo possession = new ModelTestScenarios().possessionIdentification();
        possession.setRelationExternalResourceUri(
                ModelTestScenarios.SAME_AS
        );
        graphElementUtils().addIdentificationToGraphElementWithUri(
                possession,
                edgeUtils().edgeBetweenAAndB().uri()
        );
        ClientResponse response = resource.path("service")
                .path("users")
                .path("vince")
                .path("admin")
                .path("re_add_identifications")
                .cookie(loginAsVince())
                .post(ClientResponse.class);
        assertThat(
                response.getStatus(),
                is(Response.Status.OK.getStatusCode())
        );
    }


    private ClientResponse setNumberOfConnectedOfAllVerticesToZero() {
        return resource
                .path("service")
                .path("test")
                .path("graph")
                .path("set_all_number_of_connected_edges_to_zero")
                .cookie(authCookie)
                .post(ClientResponse.class);
    }

    private ClientResponse refreshNumberOfConnectedEdges(NewCookie vinceCookie) {
        return resource
                .path("service")
                .path("users")
                .path("vince")
                .path("admin")
                .path("refresh_number_of_connected_edges")
                .cookie(vinceCookie)
                .post(ClientResponse.class);
    }

    private ClientResponse reindexAll(NewCookie vinceCookie) {
        return resource
                .path("service")
                .path("users")
                .path("vince")
                .path("admin")
                .path("reindex")
                .cookie(vinceCookie)
                .post(ClientResponse.class);
    }

    private NewCookie loginAsVinceWithCapitalLetter() {
        return authenticate(
                createUserVinceWithCapitalLetter().getEntity(JSONObject.class)
        ).getCookies().get(0);
    }

    private NewCookie loginAsVince() {
        return authenticate(
                createUserVince().getEntity(JSONObject.class)
        ).getCookies().get(0);
    }

    private ClientResponse createUserVince() {
        return resource
                .path("service")
                .path("test")
                .path("users")
                .path("vince")
                .cookie(authCookie)
                .post(ClientResponse.class);
    }

    private ClientResponse createUserVinceWithCapitalLetter() {
        ClientResponse response = resource
                .path("service")
                .path("test")
                .path("users")
                .path("Vince")
                .cookie(authCookie)
                .post(ClientResponse.class);
        assertThat(
                response.getStatus(),
                is(Response.Status.OK.getStatusCode())
        );
        return response;
    }
}
