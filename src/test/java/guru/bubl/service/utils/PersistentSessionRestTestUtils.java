/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.utils;

import com.google.gson.Gson;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import java.util.HashMap;
import java.util.Map;

public class PersistentSessionRestTestUtils {
    private WebResource resource;

    public PersistentSessionRestTestUtils(WebResource resource){
        this.resource = resource;
    }

    Gson gson = new Gson();

    public Map get(){
        String json = resource
                .path("service")
                .path("test")
                .path("persistent-session")
                .get(ClientResponse.class).getEntity(String.class);
        return gson.fromJson(
                json,
                HashMap.class
        );
    }
}
