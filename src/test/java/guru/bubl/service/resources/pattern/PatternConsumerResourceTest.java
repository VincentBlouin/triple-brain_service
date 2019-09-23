package guru.bubl.service.resources.pattern;

import com.sun.jersey.api.client.ClientResponse;
import guru.bubl.service.utils.GraphManipulationRestTestUtils;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.net.URI;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class PatternConsumerResourceTest extends GraphManipulationRestTestUtils {

    @Test
    public void returns_created_status() {
        PatternResourceTest.makePattern(
                vertexBUri(),
                authCookie
        );
        ClientResponse response = consumePattern(
                currentAuthenticatedUser.username(),
                vertexBUri()
        );
        assertThat(
                response.getStatus(),
                is(Response.Status.CREATED.getStatusCode())
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
}
