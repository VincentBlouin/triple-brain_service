/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.schema;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import guru.bubl.module.model.UserUris;
import guru.bubl.module.model.graph.GraphTransactional;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.graph.schema.SchemaJson;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SchemaNonOwnedResource {

    private UserGraph userGraph;

    @AssistedInject
    public SchemaNonOwnedResource(
            @Assisted UserGraph userGraph
    ){
        this.userGraph = userGraph;
    }

    @GET
    @GraphTransactional
    @Path("/{shortId}")
    public Response get(@PathParam("shortId") String shortId){
        return Response.ok().entity(SchemaJson.toJson(
                userGraph.schemaPojoWithUri(schemaUriFromShortId(
                        shortId
                ))
        )).build();
    }

    private URI schemaUriFromShortId(String shortId){
        return new UserUris(userGraph.user()).schemaUriFromShortId(
                shortId
        );
    }
}
