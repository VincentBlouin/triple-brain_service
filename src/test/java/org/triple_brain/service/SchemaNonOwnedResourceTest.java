/*
 * Copyright Vincent Blouin under the Mozilla Public License 1.1
 */

package org.triple_brain.service;

import com.sun.jersey.api.client.ClientResponse;
import org.junit.Test;
import org.triple_brain.module.model.UserUris;
import org.triple_brain.service.utils.GraphManipulationRestTest;

import javax.ws.rs.core.Response;
import java.net.URI;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class SchemaNonOwnedResourceTest extends GraphManipulationRestTest {

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
