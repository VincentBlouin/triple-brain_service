/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package org.triple_brain.service.resources.schema;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import org.codehaus.jettison.json.JSONObject;
import org.triple_brain.module.model.UserUris;
import org.triple_brain.module.model.graph.*;
import org.triple_brain.module.model.graph.edge.EdgeOperator;
import org.triple_brain.module.model.graph.schema.SchemaOperator;
import org.triple_brain.module.model.json.LocalizedStringJson;
import org.triple_brain.module.search.GraphIndexer;
import org.triple_brain.service.resources.GraphElementIdentificationResource;
import org.triple_brain.service.resources.vertex.GraphElementIdentificationResourceFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

public class SchemaPropertyResource {

    private SchemaOperator schemaOperator;
    private UserGraph userGraph;

    @Inject
    GraphIndexer graphIndexer;

    @Inject
    GraphElementIdentificationResourceFactory graphElementIdentificationResourceFactory;

    @Inject
    GraphElementOperatorFactory graphElementOperatorFactory;

    @AssistedInject
    public SchemaPropertyResource(
            @Assisted SchemaOperator schemaOperator,
            @Assisted UserGraph userGraph
    ) {
        this.schemaOperator = schemaOperator;
        this.userGraph = userGraph;
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
        URI uri = uriFromShortId(shortId);
        if (!userGraph.haveElementWithId(uri)) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        graphElementOperatorFromShortId(shortId).label(
                label.optString(
                        LocalizedStringJson.content.name()
                )
        );
        graphIndexer.indexSchema(
                userGraph.schemaPojoWithUri(
                        schemaOperator.uri()
                )
        );
        graphIndexer.commit();
        return Response.noContent().build();
    }

    @DELETE
    @GraphTransactional
    @Path("/{shortId}")
    public Response delete(@PathParam("shortId") String shortId) {
        graphElementOperatorFromShortId(shortId).remove();
        graphIndexer.indexSchema(
                userGraph.schemaPojoWithUri(
                        schemaOperator.uri()
                )
        );
        graphIndexer.commit();
        return Response.noContent().build();
    }

    @POST
    @GraphTransactional
    @Path("{shortId}/comment")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response updateComment(
            @PathParam("shortId") String shortId,
            String comment
    ) {
        graphElementOperatorFromShortId(shortId).comment(
                comment
        );
        graphIndexer.indexSchema(
                userGraph.schemaPojoWithUri(
                        schemaOperator.uri()
                )
        );
        graphIndexer.commit();
        return Response.noContent().build();
    }

    @GraphTransactional
    @Path("/{shortId}/identification")
    public GraphElementIdentificationResource getGraphElementIdentificationResource(@PathParam("shortId") String shortId) {
        return graphElementIdentificationResourceFactory.forSchemaProperty(
                graphElementOperatorFromShortId(shortId),
                schemaOperator.uri(),
                userGraph
        );
    }

    private GraphElementOperator graphElementOperatorFromShortId(String shortId) {
        return graphElementOperatorFactory.withUri(
                uriFromShortId(shortId)
        );
    }

    private URI uriFromShortId(String shortId) {
        return UserUris.schemaPropertyUriFromShortIdAndSchema(
                schemaOperator,
                shortId
        );
    }
}
