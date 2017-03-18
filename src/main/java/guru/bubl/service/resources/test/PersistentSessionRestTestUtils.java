/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import guru.bubl.module.model.graph.GraphTransactional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

@Path("/test/persistent-session")
@Singleton
@Produces(MediaType.APPLICATION_JSON)
public class PersistentSessionRestTestUtils {

    @Inject
    @Named("session")
    HashMap session;

    Gson gson = new Gson();

    @Path("/")
    @GraphTransactional
    @GET
    public Response getSession() {
        Type type = new TypeToken<Map<String, String>>(){}.getType();
        return Response.ok(
                gson.toJson(session, type)
        ).build();
    }
}
