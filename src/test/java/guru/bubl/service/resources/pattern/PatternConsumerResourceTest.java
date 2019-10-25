package guru.bubl.service.resources.pattern;

import com.sun.jersey.api.client.ClientResponse;
import guru.bubl.service.utils.GraphManipulationRestTestUtils;
import guru.bubl.service.utils.GraphRestTestUtils;
import org.junit.Test;
import org.parboiled.trees.GraphUtils;

import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.net.URI;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class PatternConsumerResourceTest extends GraphManipulationRestTestUtils {

    @Test
    public void returns_ok_status() {
        makePattern(
                vertexBUri(),
                authCookie
        );
        ClientResponse response = consumePattern(
                currentAuthenticatedUser.username(),
                vertexBUri()
        );
        assertThat(
                response.getStatus(),
                is(Response.Status.OK.getStatusCode())
        );
    }

    private ClientResponse consumePattern(String username, URI uri) {
        return resource
                .path("service")
                .path("users")
                .path(username)
                .path("patterns")
                .path(uri.getPath().replaceFirst("/service", ""))
                .cookie(authCookie)
                .post(ClientResponse.class);
    }

    public static ClientResponse makePattern(URI uri, NewCookie authCookie) {
        GraphRestTestUtils.graphWithCenterVertexUri(uri, authCookie);
        return resource
                .path(uri.getPath())
                .path("pattern")
                .cookie(authCookie)
                .post(ClientResponse.class);
    }
}
