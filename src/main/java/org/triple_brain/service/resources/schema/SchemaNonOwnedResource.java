/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package org.triple_brain.service.resources.schema;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import org.triple_brain.module.model.UserUris;
import org.triple_brain.module.model.graph.GraphTransactional;
import org.triple_brain.module.model.graph.UserGraph;
import org.triple_brain.module.model.json.graph.SchemaJson;

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
