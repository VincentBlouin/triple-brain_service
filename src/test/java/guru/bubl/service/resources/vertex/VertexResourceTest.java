/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.vertex;

import com.sun.jersey.api.client.ClientResponse;
import guru.bubl.module.model.center_graph_element.CenterGraphElementPojo;
import guru.bubl.module.model.graph.edge.EdgePojo;
import guru.bubl.module.model.json.StatementJsonFields;
import guru.bubl.module.model.search.EdgeSearchResult;
import guru.bubl.module.model.search.VertexSearchResult;
import guru.bubl.service.utils.GraphManipulationRestTestUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import guru.bubl.module.model.UserUris;
import guru.bubl.module.model.graph.GraphElement;
import guru.bubl.module.model.graph.edge.Edge;
import guru.bubl.module.model.graph.vertex.Vertex;
import guru.bubl.module.model.json.graph.EdgeJson;
import guru.bubl.module.model.json.graph.VertexInSubGraphJson;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import static junit.framework.Assert.assertFalse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class VertexResourceTest extends GraphManipulationRestTestUtils {

    @Test
    public void adding_a_vertex_returns_correct_status() throws Exception {
        ClientResponse response = vertexUtils().addAVertexToVertexWithUri(
                vertexAUri()
        );
        assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
    }

    @Test
    public void can_add_a_vertex_and_relation() throws Exception {
        int numberOfConnectedEdges = vertexUtils().connectedEdgesOfVertexWithURI(
                vertexAUri()
        ).size();
        vertexUtils().addAVertexToVertexWithUri(vertexAUri());
        int updatedNumberOfConnectedEdges = vertexUtils().connectedEdgesOfVertexWithURI(
                vertexAUri()
        ).size();
        assertThat(updatedNumberOfConnectedEdges, is(numberOfConnectedEdges + 1));
    }

    @Test
    public void adding_a_vertex_returns_the_new_edge_and_vertex_id() throws Exception {
        ClientResponse response = vertexUtils().addAVertexToVertexWithUri(vertexAUri());
        JSONObject createdStatement = response.getEntity(JSONObject.class);
        Vertex subject = VertexInSubGraphJson.fromJson(
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
        Vertex newVertex = VertexInSubGraphJson.fromJson(
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
        ClientResponse response = vertexUtils().addAVertexToVertexWithUri(vertexAUri());
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
        Vertex newVertex = VertexInSubGraphJson.fromJson(
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
    public void cannot_add_a_vertex_that_user_doesnt_own() throws Exception {
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
    public void can_remove_a_vertex() throws Exception {
        assertTrue(graphElementWithIdExistsInCurrentGraph(
                vertexBUri()
        ));
        vertexUtils().removeVertexB();
        assertFalse(graphElementWithIdExistsInCurrentGraph(
                vertexBUri()
        ));
    }

    @Test
    public void removing_vertex_returns_correct_response_status() throws Exception {
        ClientResponse response = vertexUtils().removeVertexB();
        assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
    }

    @Test
    public void can_update_label() throws Exception {
        String vertexALabel = vertexA().label();
        assertThat(vertexALabel, is(not("new vertex label")));
        vertexUtils().updateVertexLabelUsingRest(
                vertexAUri(),
                "new vertex label"
        );
        vertexALabel = vertexA().label();
        assertThat(vertexALabel, is("new vertex label"));
    }

    @Test
    public void label_can_have_special_characters() {
        String vertexALabel = vertexA().label();
        assertThat(vertexALabel, is(not("a(test*)")));
        vertexUtils().updateVertexLabelUsingRest(
                vertexAUri(),
                "a(test*)"
        );
        vertexALabel = vertexA().label();
        assertThat(vertexALabel, is("a(test*)"));
    }

    @Test
    public void can_update_note() throws Exception {
        String vertexANote = vertexA().comment();
        assertThat(vertexANote, is(not("some note")));
        vertexUtils().updateVertexANote("some note");
        vertexANote = vertexA().comment();
        assertThat(vertexANote, is("some note"));
    }

    @Test
    public void updating_label_returns_correct_status() throws Exception {
        ClientResponse response = vertexUtils().updateVertexLabelUsingRest(
                vertexAUri(),
                "new vertex label"
        );
        assertThat(response.getStatus(), is(Response.Status.NO_CONTENT.getStatusCode()));
    }

    @Test
    public void updating_note_updates_search() throws Exception {
        indexGraph();
        GraphElement resultsForA = searchUtils().autoCompletionResultsForCurrentUserVerticesOnly(
                vertexA().label()
        ).get(0).getGraphElement();
        assertThat(resultsForA.comment(), is(""));
        vertexUtils().updateVertexANote(
                "A description"
        );
        resultsForA = searchUtils().searchDetailsByUri(vertexAUri()).getGraphElement();
        assertThat(
                resultsForA.comment(), is("A description")
        );
    }

    @Test
    public void when_deleting_a_vertex_its_relations_are_also_removed_from_search() {
        indexGraph();
        List<EdgeSearchResult> relations = searchUtils().searchForRelations(
                "between",
                defaultAuthenticatedUserAsJson
        );
        assertThat(
                relations.size(),
                is(2)
        );
        vertexUtils().removeVertexB();
        relations = searchUtils().searchForRelations(
                "between",
                defaultAuthenticatedUserAsJson
        );
        assertThat(
                relations.size(),
                is(0)
        );
    }

    @Test
    public void making_vertex_public_re_indexes_it() throws Exception {
        indexGraph();
        JSONObject anotherUser = createAUser();
        authenticate(
                anotherUser
        );
        List<VertexSearchResult> results = searchUtils().autoCompletionResultsForPublicAndUserVertices(
                vertexA().label(),
                anotherUser
        );
        assertThat(
                results.size(), is(0)
        );
        authenticate(defaultAuthenticatedUser);
        vertexUtils().makePublicVertexWithUri(
                vertexAUri()
        );
        authenticate(anotherUser);
        results = searchUtils().autoCompletionResultsForPublicAndUserVertices(
                vertexA().label(),
                anotherUser
        );
        assertThat(
                results.size(),
                is(1)
        );
    }

    @Test
    public void making_vertex_private_re_indexes_it() throws Exception {
        vertexUtils().makePublicVertexWithUri(
                vertexAUri()
        );
        indexGraph();
        JSONObject anotherUser = createAUser();
        authenticate(anotherUser);
        JSONArray results = searchUtils().clientResponseOfAutoCompletionForPublicAndUserOwnedVertices(
                vertexA().label(),
                anotherUser
        ).getEntity(JSONArray.class);
        Assert.assertThat(results.length(), Is.is(greaterThan(0)));
        authenticate(defaultAuthenticatedUser);
        vertexUtils().makePrivateVertexWithUri(
                vertexAUri()
        );
        authenticate(anotherUser);
        results = searchUtils().clientResponseOfAutoCompletionForPublicAndUserOwnedVertices(
                vertexA().label(),
                anotherUser
        ).getEntity(JSONArray.class);
        Assert.assertThat(results.length(), Is.is(0));
    }

    @Test
    public void number_of_connected_vertices_are_included() throws Exception {
        assertThat(
                vertexB().getNumberOfConnectedEdges(),
                is(2)
        );
    }

    @Test
    public void can_get_random_surround_graph() throws Exception {
        ClientResponse response = getAnyVertexUri();
        assertThat(
                response.getStatus(),
                is(Response.Status.OK.getStatusCode())
        );
        try {
            URI.create(
                    response.getEntity(String.class)
            );
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void creating_a_single_vertex_increment_its_number_of_visits() {
        Set<CenterGraphElementPojo> centerElements = graphUtils().getCenterGraphElements();
        Integer numberOfVisitedElements = centerElements.size();
        createSingleVertex();
        centerElements = graphUtils().getCenterGraphElements();
        Assert.assertThat(
                centerElements.size(),
                Is.is(numberOfVisitedElements + 1)
        );
    }

    @Test
    @Ignore(
            "Using measures on the client side to avoid fast addition of multiple childs. " +
                    "Also I consider it not dramatic that the number of connected edges is not totally accurate."
    )
    public void number_of_connected_edges_is_ok_after_adding_vertices_concurrently() throws Exception {
        assertThat(
                vertexA().getNumberOfConnectedEdges(),
                is(1)
        );
        Integer numberOfVerticesToAdd = 5;
        CountDownLatch latch = new CountDownLatch(numberOfVerticesToAdd);
        for (int i = 0; i < numberOfVerticesToAdd; i++) {
            new Thread(new AddChildToVertexARunner(latch)).start();
//            Thread.sleep(50);
        }
        latch.await();
        assertThat(
                vertexA().getNumberOfConnectedEdges(),
                is(6)
        );

    }

    private Vertex createSingleVertex(){
        return VertexInSubGraphJson.fromJson(
                responseForCreateSingleVertex().getEntity(
                        JSONObject.class
                )
        );
    }

    private ClientResponse responseForCreateSingleVertex(){
        return resource
                .path(
                        new UserUris(defaultAuthenticatedUser).baseVertexUri().getPath()
                )
                .cookie(authCookie)
                .post(ClientResponse.class);
    }

    private ClientResponse getAnyVertexUri() {
        return resource
                .path(
                        new UserUris(defaultAuthenticatedUser).baseVertexUri().getPath()
                )
                .path("any")
                .cookie(authCookie)
                .get(ClientResponse.class);
    }

    private class AddChildToVertexARunner implements Runnable {
        CountDownLatch latch = null;

        public AddChildToVertexARunner(CountDownLatch latch) {
            this.latch = latch;
        }

        public void run() {
            resource.path(
                    vertexAUri().getPath()
            ).cookie(
                    authCookie
            ).post();
            latch.countDown();
        }
    }

}
