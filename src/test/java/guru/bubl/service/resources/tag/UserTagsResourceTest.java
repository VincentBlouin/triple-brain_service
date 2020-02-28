/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.tag;

import com.sun.jersey.api.client.ClientResponse;
import guru.bubl.module.model.User;
import guru.bubl.module.model.graph.tag.TagPojo;
import guru.bubl.module.model.tag.TagJson;
import guru.bubl.service.SessionHandler;
import guru.bubl.service.utils.GraphManipulationRestTestUtils;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class UserTagsResourceTest extends GraphManipulationRestTestUtils {

    @Test
    public void returns_ok_status() {
        assertThat(
                getForUser(currentAuthenticatedUser).getStatus(),
                is(Response.Status.OK.getStatusCode())
        );
    }

    @Test
    public void returns_the_user_metas() {
        Integer nbTags = getDataForUser(currentAuthenticatedUser).size();
        graphElementUtils().addFoafPersonTypeToVertexA();
        assertThat(
                getDataForUser(currentAuthenticatedUser).size(),
                is(nbTags + 1)
        );
    }

    @Test
    public void cannot_get_the_metas_of_another_user() {
        User previousUser = currentAuthenticatedUser;
        authCookie = authenticate(
                createAUser()
        ).getCookies().get(0);
        assertThat(
                getForUser(previousUser).getStatus(),
                is(Response.Status.FORBIDDEN.getStatusCode())
        );
    }


    private ClientResponse getForUser(User user) {
        return resource
                .path("service")
                .path("users")
                .path(user.username())
                .path("metas")
                .cookie(authCookie)
                .header(SessionHandler.X_XSRF_TOKEN, currentXsrfToken)
                .get(ClientResponse.class);
    }

    private Set<TagPojo> getDataForUser(User user) {
        return TagJson.getSetFromJson(
                getForUser(user).getEntity(String.class)
        );
    }
}
