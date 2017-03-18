/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.test;

import guru.bubl.module.model.search.GraphIndexer;
import guru.bubl.service.SessionHandler;
import org.codehaus.jettison.json.JSONArray;
import guru.bubl.module.model.User;
import guru.bubl.module.model.graph.GraphFactory;
import guru.bubl.module.model.graph.GraphTransactional;
import guru.bubl.module.model.graph.subgraph.SubGraph;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.graph.edge.Edge;
import guru.bubl.module.model.graph.vertex.Vertex;
import guru.bubl.module.model.graph.vertex.VertexFactory;
import guru.bubl.module.model.graph.vertex.VertexInSubGraphPojo;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import guru.bubl.module.model.json.UserJson;
import guru.bubl.module.model.json.graph.VertexInSubGraphJson;
import guru.bubl.module.model.test.GraphComponentTest;
import guru.bubl.module.model.test.scenarios.TestScenarios;
import guru.bubl.module.model.test.scenarios.VerticesCalledABAndC;
import guru.bubl.module.repository.user.UserRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.mail.Session;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.UUID;

import static guru.bubl.service.SecurityInterceptor.AUTHENTICATED_USER_KEY;
import static guru.bubl.service.SecurityInterceptor.AUTHENTICATION_ATTRIBUTE_KEY;

@Path("/test")
@Produces(MediaType.APPLICATION_JSON)
@Singleton
public class ResourceForTests {

    @Inject
    UserRepository userRepository;

    @Inject
    GraphIndexer graphIndexer;

    @Inject
    SessionHandler sessionHandler;

//    @Inject
//    SearchUtils searchUtils;

    @Inject
    private GraphFactory graphFactory;

    @Inject
    TestScenarios testScenarios;

    @Inject
    GraphComponentTest graphComponentTest;

    @Inject
    VertexFactory vertexFactory;

    @Path("login")
    @GraphTransactional
    @GET
    public Response createUserAuthenticateAndRedirectToHomePage(@Context HttpServletRequest request) throws Exception {
        User user = User.withEmail(
                "test@triple_brain.org"
        ).password("password");
        if(!userRepository.emailExists(user.email())){
            userRepository.createUser(
                    user
            );
        }
        graphFactory.createForUser(user);
//        deleteAllUserDocumentsForSearch(user);
        UserGraph userGraph = graphFactory.loadForUser(
                user
        );
        graphIndexer.indexVertex(
                userGraph.defaultVertex()
        );
        graphIndexer.commit();
//        addALotOfVerticesToVertex(
//                userGraph.defaultVertex()
//        );
        request.getSession().setAttribute(AUTHENTICATION_ATTRIBUTE_KEY, true);
        request.getSession().setAttribute(AUTHENTICATED_USER_KEY, user);
        return Response.temporaryRedirect(
                new URI(
                        request.getScheme() + "://"
                                + request.getLocalAddr()
                                + ":" + request.getServerPort()
                                + "/"
                )
        ).build();
    }

    private void addALotOfVerticesToVertex(VertexOperator vertex){
        VertexOperator destinationVertex = vertex;
        for (int i = 0; i < 100; i++) {
            Edge edge = destinationVertex.addVertexAndRelation();
            destinationVertex = vertexFactory.withUri(
                    edge.destinationVertex().uri()
            );
        }
    }

//    @Path("search/close")
//    @Produces(MediaType.TEXT_PLAIN)
//    @GET
//    public Response closeSearchEngine() {
//        searchUtils.close();
//        return Response.ok().build();
//    }

    @Path("search/delete_all_documents")
    @Produces(MediaType.TEXT_PLAIN)
    @GET
    public Response deleteAllUserDocuments(@Context HttpServletRequest request) {
//        removeSearchIndex();
        return Response.ok().build();
    }
//    public void removeSearchIndex() {
//        SolrServer solrServer = searchUtils.getServer();
//        try {
//            solrServer.deleteByQuery("*:*");
//            solrServer.commit();
//        } catch (SolrServerException | IOException e) {
//            throw new RuntimeException(e);
//        }
//    }


//    private void deleteAllUserDocumentsForSearch(User user) {
//        SolrServer solrServer = searchUtils.getServer();
//        try {
//            solrServer.deleteByQuery("owner_username:" + user.username());
//            solrServer.commit();
//        } catch (SolrServerException | IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

    @Path("search/index_graph")
    @Produces(MediaType.TEXT_PLAIN)
    @GraphTransactional
    @GET
    public Response indexSessionUserVertices(@Context HttpServletRequest request) {
        User currentUser = sessionHandler.userFromSession(
                request.getSession()
        );
        UserGraph userGraph = graphFactory.loadForUser(
                currentUser
        );
        SubGraph subGraph = userGraph.graphWithAnyVertexAndDepth(10);
        for (Vertex vertex : subGraph.vertices().values()) {
            graphIndexer.indexVertex(
                    vertexFactory.withUri(
                            vertex.uri()
                    )
            );
        }
        for (Edge edge : subGraph.edges().values()) {
            graphIndexer.indexRelation(
                    edge
            );
        }
        graphIndexer.commit();
        return Response.ok().build();
    }

    @Path("create_user")
    @POST
    public Response createUserWithDefaultPassword() throws Exception {
        User user = User.withEmail(
                UUID.randomUUID().toString() + "@triplebrain.org"
        ).password("password");
        userRepository.createUser(user);
        return Response.ok(
                UserJson.toJson(user)
        ).build();
    }


    @Path("make_graph_have_3_serial_vertices_with_long_labels")
    @GraphTransactional
    @GET
    public Response makeGraphHave3SerialVerticesWithLongLabels(@Context HttpServletRequest request) throws Exception {
        User currentUser = sessionHandler.userFromSession(
                request.getSession()
        );
        graphComponentTest.user(currentUser);
        VerticesCalledABAndC verticesCalledABAndC = testScenarios.makeGraphHave3SerialVerticesWithLongLabels(
                graphFactory.loadForUser(currentUser)
        );
        JSONArray verticesCalledABAndCAsJsonArray = new JSONArray();
        verticesCalledABAndCAsJsonArray
                .put(
                        VertexInSubGraphJson.toJson(
                                new VertexInSubGraphPojo(verticesCalledABAndC.vertexA())
                        )
                )
                .put(
                        VertexInSubGraphJson.toJson(
                                new VertexInSubGraphPojo(verticesCalledABAndC.vertexB())
                        ))
                .put(
                        VertexInSubGraphJson.toJson(
                                new VertexInSubGraphPojo(verticesCalledABAndC.vertexC())
                        ));

        return Response.ok(verticesCalledABAndCAsJsonArray).build();
    }





}
