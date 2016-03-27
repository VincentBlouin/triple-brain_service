/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.schema;

import com.sun.jersey.api.client.ClientResponse;
import guru.bubl.service.utils.GraphManipulationRestTestUtils;
import org.junit.Test;
import guru.bubl.module.model.UserUris;

import javax.ws.rs.core.Response;
import java.net.URI;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class SchemaNonOwnedResourceTest extends GraphManipulationRestTestUtils {

    @Test
    public void getting_not_owned_schema_returns_ok_status(){
        URI schemaUri = schemaUtils().uriOfCreatedSchema();
        authenticate(createAUser());
        assertThat(
                getNotOwnedSchema(schemaUri).getStatus(),
                is(Response.Status.OK.getStatusCode())
        );
    }

    private ClientResponse getNotOwnedSchema(URI schemaUri){
        String shortId = UserUris.graphElementShortId(
                schemaUri
        );
        return resource
                .path(getUsersBaseUri(UserUris.ownerUserNameFromUri(schemaUri)))
                .path("non_owned")
                .path("schema")
                .path(shortId)
                .cookie(authCookie)
                .get(ClientResponse.class);
    }
}
