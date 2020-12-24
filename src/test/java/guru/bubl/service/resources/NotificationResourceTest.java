package guru.bubl.service.resources;

import com.sun.jersey.api.client.ClientResponse;
import guru.bubl.service.SessionHandler;
import guru.bubl.service.utils.GraphManipulationRestTestUtils;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class NotificationResourceTest extends GraphManipulationRestTestUtils {

    @Test
    public void get_returns_ok_status() {
        ClientResponse response = get();
        assertThat(
                response.getStatus(),
                is(Response.Status.OK.getStatusCode())
        );
    }

    private ClientResponse get() {
        return resource
                .path("service")
                .path("users")
                .path(defaultAuthenticatedUser.username())
                .path("notification")
                .cookie(authCookie)
                .header(SessionHandler.X_XSRF_TOKEN, currentXsrfToken)
                .get(ClientResponse.class);

    }

}
