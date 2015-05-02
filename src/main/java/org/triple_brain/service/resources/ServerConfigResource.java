/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package org.triple_brain.service.resources;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/server_config")
@Produces(MediaType.APPLICATION_JSON)
@Singleton
public class ServerConfigResource {

    @Inject
    @Named("isTesting")
    Boolean isTesting;

    @GET
    @Path("/")
    public Response get() {
        try {
            return Response.ok(
                    new JSONObject()
                            .put("isTesting", isTesting)
            ).build();
        }catch(JSONException e){
            throw new RuntimeException(e);
        }
    }

}
