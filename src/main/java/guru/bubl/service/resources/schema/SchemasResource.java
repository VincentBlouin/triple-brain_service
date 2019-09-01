/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.schema;

import guru.bubl.module.model.graph.schema.SchemaJson;
import guru.bubl.module.model.graph.schema.SchemaList;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/schemas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Singleton
public class SchemasResource {

    @Inject
    SchemaList schemaList;

    @GET
    public Response list() {
        return Response.ok(
                SchemaJson.jsonFromList(
                        schemaList.get()
                )
        ).build();
    }
}
