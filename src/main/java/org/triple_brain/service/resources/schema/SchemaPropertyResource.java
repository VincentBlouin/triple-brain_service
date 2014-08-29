package org.triple_brain.service.resources.schema;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import org.codehaus.jettison.json.JSONObject;
import org.triple_brain.module.model.UserUris;
import org.triple_brain.module.model.graph.*;
import org.triple_brain.module.model.graph.schema.SchemaOperator;
import org.triple_brain.module.model.json.LocalizedStringJson;
import org.triple_brain.service.resources.GraphElementIdentificationResource;
import org.triple_brain.service.resources.vertex.GraphElementIdentificationResourceFactory;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.net.URI;

/**
 * Copyright Mozilla Public License 1.1
 */
public class SchemaPropertyResource {

    private SchemaOperator schemaOperator;
    private GraphElementOperatorFactory graphElementOperatorFactory;

    @Inject
    GraphElementIdentificationResourceFactory graphElementIdentificationResourceFactory;

    @AssistedInject
    public SchemaPropertyResource(
            GraphElementOperatorFactory graphElementOperatorFactory,
            @Assisted SchemaOperator schemaOperator
    ){
        this.graphElementOperatorFactory = graphElementOperatorFactory;
        this.schemaOperator = schemaOperator;
    }

    @POST
    @GraphTransactional
    @Path("/")
    public Response create() {
        GraphElement property = schemaOperator.addProperty();
        return Response.created(
                URI.create(
                        UserUris.graphElementShortId(property.uri())
                )
        ).build();
    }

    @POST
    @GraphTransactional
    @Path("/{shortId}/label")
    public Response updateLabel(@PathParam("shortId") String shortId, JSONObject label) {
        graphElementOperatorFromShortId(shortId).label(
                label.optString(
                        LocalizedStringJson.content.name()
                )
        );
        return Response.noContent().build();
    }

    @GraphTransactional
    @Path("/{shortId}/identification")
    public GraphElementIdentificationResource getGraphElementIdentificationResource(@PathParam("shortId") String shortId) {
        return graphElementIdentificationResourceFactory.forGraphElement(
                graphElementOperatorFromShortId(shortId),
                GraphElementType.SCHEMA_PROPERTY
        );
    }

    private GraphElementOperator graphElementOperatorFromShortId(String shortId){
        return graphElementOperatorFactory.withUri(
                uriFromShortId(shortId)
        );
    }

    private URI uriFromShortId(String shortId){
        return UserUris.schemaPropertyUriFromShortIdAndSchema(
                schemaOperator,
                shortId
        );
    }
}
