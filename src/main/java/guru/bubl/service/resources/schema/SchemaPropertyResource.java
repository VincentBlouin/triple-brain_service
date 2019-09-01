/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.schema;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import guru.bubl.module.model.UserUris;
import guru.bubl.module.model.graph.GraphElement;
import guru.bubl.module.model.graph.GraphElementOperator;
import guru.bubl.module.model.graph.GraphElementOperatorFactory;
import guru.bubl.module.model.graph.schema.SchemaOperator;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.json.LocalizedStringJson;
import guru.bubl.module.model.search.GraphIndexer;
import guru.bubl.service.resources.GraphElementIdentificationResource;
import guru.bubl.service.resources.vertex.GraphElementIdentificationResourceFactory;
import org.codehaus.jettison.json.JSONObject;

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

    @Inject
    SchemaResourceFactory schemaResourceFactory;

    @AssistedInject
    public SchemaPropertyResource(
            @Assisted SchemaOperator schemaOperator,
            @Assisted UserGraph userGraph
    ) {
        this.schemaOperator = schemaOperator;
        this.userGraph = userGraph;
    }

    @POST
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
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{shortId}")
    public Response delete(@PathParam("shortId") String shortId) {
        graphElementOperatorFromShortId(shortId).remove();
        return Response.noContent().build();
    }

    @POST
    @Path("{shortId}/comment")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response updateComment(
            @PathParam("shortId") String shortId,
            String comment
    ) {
        graphElementOperatorFromShortId(shortId).comment(
                comment
        );
        return Response.noContent().build();
    }

    @GET
    @Path("{shortId}/surround_graph")
    public Response getSurroundGraph(
            @PathParam("shortId") String shortId
    ) {
        return schemaResourceFactory.fromUserGraph(
                userGraph
        ).get(
                UserUris.graphElementShortId(
                        schemaOperator.uri()
                )
        );
    }

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
