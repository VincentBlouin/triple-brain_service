package guru.bubl.service.resources.pattern;

import com.google.gson.reflect.TypeToken;
import com.sun.jersey.api.client.ClientResponse;
import guru.bubl.module.model.graph.GraphElementPojo;
import guru.bubl.module.model.graph.pattern.PatternPojo;
import guru.bubl.module.model.json.JsonUtils;
import guru.bubl.service.utils.GraphManipulationRestTestUtils;
import org.junit.Test;

import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class PatternResourceTest extends GraphManipulationRestTestUtils {

    @Test
    public void getting_list_returns_ok_status() {
        ClientResponse response = getPatternsListResponse();
        assertThat(
                response.getStatus(),
                is(Response.Status.OK.getStatusCode())
        );
    }

    @Test
    public void can_get_list() {
        Set<PatternPojo> patterns = getPatternsList();
        assertThat(
                patterns.size(),
                is(0)
        );
        ClientResponse response = makePattern(vertexAUri(), authCookie);
        assertThat(
                response.getStatus(),
                is(Response.Status.NO_CONTENT.getStatusCode())
        );
        makePattern(vertexCUri(), authCookie);
        patterns = getPatternsList();
        assertThat(
                patterns.size(),
                is(2)
        );
    }

    private ClientResponse getPatternsListResponse() {
        return resource
                .path("service")
                .path("patterns")
                .cookie(authCookie)
                .get(ClientResponse.class);
    }

    public static ClientResponse makePattern(URI uri, NewCookie authCookie) {
        return resource
                .path(uri.getPath())
                .path("pattern")
                .cookie(authCookie)
                .post(ClientResponse.class);
    }

    private Set<PatternPojo> getPatternsList() {
        ClientResponse clientResponse = getPatternsListResponse();
        return JsonUtils.getGson().fromJson(
                clientResponse.getEntity(
                        String.class
                ),
                new TypeToken<Set<PatternPojo>>() {
                }.getType()
        );
    }

}
