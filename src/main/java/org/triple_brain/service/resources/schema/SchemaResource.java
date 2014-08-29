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

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

/**
 * Copyright Mozilla Public License 1.1
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SchemaResource {

    private UserGraph userGraph;
    private SchemaPropertyResourceFactory schemaPropertyResourceFactory;

    @AssistedInject
    public SchemaResource(
            SchemaPropertyResourceFactory schemaPropertyResourceFactory,
            @Assisted UserGraph userGraph
    ){
        this.schemaPropertyResourceFactory = schemaPropertyResourceFactory;
        this.userGraph = userGraph;
    }

    @POST
    @GraphTransactional
    @Path("/")
    public Response createSchema() {
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
    public Response createSchema(@PathParam("shortId") String shortId) {
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
        return Response.noContent().build();
    }

    @GraphTransactional
    @Path("/{shortId}/property")
    public SchemaPropertyResource getPropertyResource(@PathParam("shortId") String shortId) {
        SchemaOperator schema = userGraph.schemaOperatorWithUri(schemaUriFromShortId(
                shortId
        ));
        return schemaPropertyResourceFactory.forSchema(
                schema
        );
    }

    private URI schemaUriFromShortId(String shortId){
        return new UserUris(userGraph.user()).schemaUriFromShortId(
                shortId
        );
    }
}
