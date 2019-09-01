/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.schema;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import guru.bubl.module.model.UserUris;
import guru.bubl.module.model.graph.GraphElementType;
import guru.bubl.module.model.graph.schema.SchemaJson;
import guru.bubl.module.model.graph.schema.SchemaOperator;
import guru.bubl.module.model.graph.schema.SchemaPojo;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.json.LocalizedStringJson;
import guru.bubl.module.model.search.GraphIndexer;
import guru.bubl.service.resources.GraphElementIdentificationResource;
import guru.bubl.service.resources.vertex.GraphElementIdentificationResourceFactory;
import org.codehaus.jettison.json.JSONObject;

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

    @Inject
    GraphElementIdentificationResourceFactory graphElementIdentificationResourceFactory;

    private UserGraph userGraph;


    @AssistedInject
    public SchemaResource(
            @Assisted UserGraph userGraph
    ) {
        this.userGraph = userGraph;
    }

    @POST
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
    @Path("/{shortId}")
    public Response get(@PathParam("shortId") String shortId) {
        return Response.ok().entity(SchemaJson.toJson(
                userGraph.schemaPojoWithUri(schemaUriFromShortId(
                        shortId
                ))
        )).build();
    }

    @POST
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
        return Response.noContent().build();
    }

    @Path("/{shortId}/identification")
    public GraphElementIdentificationResource getGraphElementIdentificationResource(@PathParam("shortId") String shortId) {
        SchemaOperator schema = userGraph.schemaOperatorWithUri(schemaUriFromShortId(
                shortId
        ));
        return graphElementIdentificationResourceFactory.forGraphElement(
                schema,
                GraphElementType.Schema
        );
    }

    private URI schemaUriFromShortId(String shortId) {
        return new UserUris(userGraph.user()).schemaUriFromShortId(
                shortId
        );
    }
}
