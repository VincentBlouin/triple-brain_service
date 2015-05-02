/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package org.triple_brain.service;

import com.sun.jersey.api.client.ClientResponse;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;
import org.triple_brain.service.utils.RestTestUtils;

import javax.ws.rs.core.MediaType;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class ServerConfigResourceTest extends RestTestUtils {

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
