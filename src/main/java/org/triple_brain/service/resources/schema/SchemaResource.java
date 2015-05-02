/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package org.triple_brain.service.resources.schema;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import org.codehaus.jettison.json.JSONObject;
import org.triple_brain.module.model.UserUris;
import org.triple_brain.module.model.graph.GraphTransactional;
import org.triple_brain.module.model.graph.UserGraph;
import org.triple_brain.module.model.graph.schema.SchemaOperator;
import org.triple_brain.module.model.graph.schema.SchemaPojo;
import org.triple_brain.module.model.json.LocalizedStringJson;
import org.triple_brain.module.model.json.graph.SchemaJson;
import org.triple_brain.module.search.GraphIndexer;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SchemaResource {

    @Inject
    GraphIndexer graphIndexer;

    @Inject
    SchemaPropertyResourceFactory schemaPropertyResourceFactory;

    private UserGraph userGraph;


    @AssistedInject
    public SchemaResource(
            @Assisted UserGraph userGraph
    ){
        this.userGraph = userGraph;
    }

    @POST
    @GraphTransactional
    @Path("/")
    public Response create() {
        SchemaPojo schema = userGraph.createSchema();
        return Response.created(
                URI.create(
                        UserUris.graphElementShortId(schema.uri())
                )
        ).entity(SchemaJson.toJson(
                schema
        )).build();
    }

    @GET
    @GraphTransactional
    @Path("/{shortId}")
    public Response get(@PathParam("shortId") String shortId) {
        return Response.ok().entity(SchemaJson.toJson(
                userGraph.schemaPojoWithUri(schemaUriFromShortId(
                        shortId
                ))
        )).build();
    }

    @POST
    @GraphTransactional
    @Path("/{shortId}/label")
    public Response updateLabel(@PathParam("shortId") String shortId, JSONObject label) {
        SchemaOperator schema = userGraph.schemaOperatorWithUri(schemaUriFromShortId(
                shortId
        ));
        schema.label(
                label.optString(
                        LocalizedStringJson.content.name()
                )
        );
        graphIndexer.indexSchema(
                userGraph.schemaPojoWithUri(
                        schema.uri()
                )
        );
        graphIndexer.commit();
        return Response.noContent().build();
    }

    @GraphTransactional
    @Path("/{shortId}/property")
    public SchemaPropertyResource getPropertyResource(@PathParam("shortId") String shortId) {
        SchemaOperator schema = userGraph.schemaOperatorWithUri(schemaUriFromShortId(
                shortId
        ));
        return schemaPropertyResourceFactory.forSchema(
                schema,
                userGraph
        );
    }

    @POST
    @GraphTransactional
    @Path("{shortId}/comment")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response updateDescription(
            @PathParam("shortId") String shortId,
            String comment
    ) {
        SchemaOperator schema = userGraph.schemaOperatorWithUri(schemaUriFromShortId(
                shortId
        ));
        schema.comment(comment);
        graphIndexer.indexSchema(
                userGraph.schemaPojoWithUri(
                        schema.uri()
                )
        );
        graphIndexer.commit();
        return Response.noContent().build();
    }

    private URI schemaUriFromShortId(String shortId){
        return new UserUris(userGraph.user()).schemaUriFromShortId(
                shortId
        );
    }
}
