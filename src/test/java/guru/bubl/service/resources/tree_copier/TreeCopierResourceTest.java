package guru.bubl.service.resources.tree_copier;

import com.google.gson.JsonObject;
import com.sun.jersey.api.client.ClientResponse;
import guru.bubl.module.model.graph.Tree;
import guru.bubl.module.model.json.JsonUtils;
import guru.bubl.module.model.json.UserJson;
import guru.bubl.service.SessionHandler;
import guru.bubl.service.utils.GraphManipulationRestTestUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;

import javax.ws.rs.core.Response;

import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static guru.bubl.module.model.test.scenarios.TestScenarios.tagFromFriendlyResource;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TreeCopierResourceTest extends GraphManipulationRestTestUtils {

    @Test
    public void returns_created_status() {
        graphElementUtils().makePublicGraphElementsWithUri(vertexA().uri());
        String originalUsername = defaultAuthenticatedUser.username();
        JSONObject newUser = createAUser();
        authenticate(newUser);
        ClientResponse response = copy(
                newUser.optString(UserJson.USER_NAME),
                originalUsername,
                Tree.withUrisOfGraphElementsAndRootUriAndTag(
                        Stream.of(
                                vertexA().uri()
                        ).collect(Collectors.toCollection(HashSet::new)),
                        vertexA().uri(),
                        tagFromFriendlyResource(vertexA())
                )
        );
        assertThat(
                response.getStatus(),
                is(Response.Status.OK.getStatusCode())
        );
    }


    private ClientResponse copy(String copierUsername, String copiedUsername, Tree tree) {
        try {
            JSONObject options = new JSONObject();
            options.put(
                    "copiedTree",
                    JsonUtils.getGson().toJson(tree)
            );
            return resource
                    .path("service")
                    .path("users")
                    .path(copierUsername)
                    .path("tree_copy")
                    .path(copiedUsername)
                    .cookie(authCookie)
                    .header(SessionHandler.X_XSRF_TOKEN, currentXsrfToken)
                    .post(ClientResponse.class, options);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

}
