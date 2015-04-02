/*
 * Copyright Vincent Blouin under the Mozilla Public License 1.1
 */

package org.triple_brain.service.utils;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.triple_brain.module.model.json.UserJson.*;

public class UserRestTestUtils {

    private WebResource resource;

    public static UserRestTestUtils withWebResource(WebResource resource){
        return new UserRestTestUtils(resource);
    }

    protected UserRestTestUtils(WebResource resource){
        this.resource = resource;
    }

    public boolean emailExists(String email){
        ClientResponse response = resource
                .path("service")
                .path("test")
                .path("users")
                .path(email)
                .accept(MediaType.TEXT_PLAIN)
                .get(ClientResponse.class);
        String emailExistsStr = response.getEntity(String.class);
        return Boolean.valueOf(emailExistsStr);
    }

    public void deleteAllUsers(){
        ClientResponse response = resource
                .path("service")
                .path("test")
                .path("users")
                .accept(MediaType.TEXT_PLAIN)
                .delete(ClientResponse.class);
        assertThat(
                response.getStatus(),
                is(Response.Status.NO_CONTENT.getStatusCode())
        );
    }

    public JSONObject validForCreation(){
        JSONObject user = new JSONObject();
        try{
            user.put(EMAIL, randomEmail());
            user.put(PASSWORD, RestTestUtils.DEFAULT_PASSWORD);
        }catch(JSONException e){
            throw new RuntimeException(e);
        }
        return user;
    }

    private String randomEmail(){
        return UUID.randomUUID().toString() + "@example.org";
    }
}
