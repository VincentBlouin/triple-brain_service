package org.triple_brain.service;

import com.sun.jersey.api.client.ClientResponse;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;
import org.triple_brain.service.utils.RestTest;

import javax.ws.rs.core.MediaType;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/*
* Copyright Mozilla Public License 1.1
*/
public class ServerConfigResourceTest extends RestTest {

    @Test
    public void can_get() throws Exception{
        ClientResponse response = resource
                .path("service")
                .path("server_config")
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get(ClientResponse.class);
        assertThat(response.getStatus(), is(200));
        JSONObject config = response.getEntity(JSONObject.class);
        assertTrue(
                config.getBoolean("isTesting")
        );
    }
}
