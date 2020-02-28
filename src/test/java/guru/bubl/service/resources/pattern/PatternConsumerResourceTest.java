package guru.bubl.service.resources.pattern;

import com.sun.jersey.api.client.ClientResponse;
import guru.bubl.service.SessionHandler;
import guru.bubl.service.utils.GraphManipulationRestTestUtils;
import guru.bubl.service.utils.GraphRestTestUtils;
import org.junit.Test;

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
                authCookie,
                currentXsrfToken,
                false
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
                .header(SessionHandler.X_XSRF_TOKEN, currentXsrfToken)
                .post(ClientResponse.class);
    }

    public static ClientResponse makePattern(URI uri, NewCookie authCookie, String xsrfToken, Boolean preventCenter) {
        if(!preventCenter){
            GraphRestTestUtils.graphWithCenterVertexUri(uri, authCookie, xsrfToken);
        }
        return resource
                .path(uri.getPath())
                .path("pattern")
                .cookie(authCookie)
                .header(SessionHandler.X_XSRF_TOKEN, xsrfToken)
                .post(ClientResponse.class);
    }
}
