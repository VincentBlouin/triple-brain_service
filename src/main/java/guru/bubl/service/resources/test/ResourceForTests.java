/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.test;

import guru.bubl.module.model.User;
import guru.bubl.module.model.admin.WholeGraphAdmin;
import guru.bubl.module.model.graph.GraphFactory;
import guru.bubl.module.model.graph.vertex.VertexJson;
import guru.bubl.module.model.graph.vertex.VertexPojo;
import guru.bubl.module.model.json.UserJson;
import guru.bubl.module.model.test.GraphComponentTest;
import guru.bubl.module.model.test.scenarios.TestScenarios;
import guru.bubl.module.model.test.scenarios.GraphElementsOfTestScenario;
import guru.bubl.module.repository.user.UserRepository;
import guru.bubl.service.SessionHandler;
import org.codehaus.jettison.json.JSONArray;

import javax.inject.Inject;
import javax.inject.Singleton;
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
    SessionHandler sessionHandler;

    @Inject
    WholeGraphAdmin wholeGraphAdmin;

    @Inject
    private GraphFactory graphFactory;

    @Inject
    TestScenarios testScenarios;

    @Inject
    GraphComponentTest graphComponentTest;

    @Path("login")
    @GET
    public Response createUserAuthenticateAndRedirectToHomePage(@Context HttpServletRequest request) throws Exception {
        User user = User.withEmail(
                "test@triple_brain.org"
        ).password("password");
        if (!userRepository.emailExists(user.email())) {
            userRepository.createUser(
                    user
            );
        }
        graphFactory.loadForUser(user).createVertex();
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

    @Path("search/delete_all_documents")
    @Produces(MediaType.TEXT_PLAIN)
    @GET
    public Response deleteAllUserDocuments(@Context HttpServletRequest request) {
        return Response.ok().build();
    }

    @Path("search/index_graph")
    @Produces(MediaType.TEXT_PLAIN)
    @GET
    public Response reindexAll(@Context HttpServletRequest request) {
        wholeGraphAdmin.reindexAll();
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
    @GET
    public Response makeGraphHave3SerialVerticesWithLongLabels(@Context HttpServletRequest request) throws Exception {
        User currentUser = sessionHandler.userFromSession(
                request.getSession()
        );
        graphComponentTest.user(currentUser);
        GraphElementsOfTestScenario graphElementsOfTestScenario = testScenarios.changeTestScenarioVerticesToLongLabels(
                graphFactory.loadForUser(currentUser)
        );
        JSONArray verticesOfTestScenarioAsJsonArray = new JSONArray();
        verticesOfTestScenarioAsJsonArray
                .put(
                        VertexJson.toJson(
                                new VertexPojo(graphElementsOfTestScenario.getVertexA())
                        )
                )
                .put(
                        VertexJson.toJson(
                                new VertexPojo(graphElementsOfTestScenario.getVertexB())
                        ))
                .put(
                        VertexJson.toJson(
                                new VertexPojo(graphElementsOfTestScenario.getVertexC())
                        ));

        return Response.ok(verticesOfTestScenarioAsJsonArray).build();
    }
}
