/*
 * Copyright Vincent Blouin under the Mozilla Public License 1.1
 */

package org.triple_brain.service.resources;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import org.codehaus.jettison.json.JSONObject;
import org.triple_brain.module.model.UserUris;
import org.triple_brain.module.model.graph.*;
import org.triple_brain.module.model.graph.edge.Edge;
import org.triple_brain.module.model.graph.vertex.VertexOperator;
import org.triple_brain.module.model.json.IdentificationJson;
import org.triple_brain.module.model.validator.IdentificationValidator;
import org.triple_brain.module.search.GraphIndexer;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GraphElementIdentificationResource {

    @Inject
    GraphIndexer graphIndexer;

    private GraphElementOperator graphElement;
    private GraphElementType graphElementType;
    private URI schemaUri;
    private UserGraph userGraph;

    @AssistedInject
    public GraphElementIdentificationResource(
            @Assisted GraphElementOperator graphElement,
            @Assisted GraphElementType graphElementType
    ) {
        this.graphElement = graphElement;
        this.graphElementType = graphElementType;
    }

    @AssistedInject
    public GraphElementIdentificationResource(
            @Assisted GraphElementOperator graphElement,
            @Assisted URI schemaUri,
            @Assisted UserGraph userGraph
    ) {
        this(
                graphElement,
                GraphElementType.SCHEMA_PROPERTY
        );
        this.schemaUri = schemaUri;
        this.userGraph = userGraph;
    }

    @POST
    @GraphTransactional
    @Path("/")
    public Response add(JSONObject identificationJson) {
        IdentificationValidator validator = new IdentificationValidator();
        IdentificationPojo identification = IdentificationJson.singleFromJson(
                identificationJson.toString()
        );
        if (!validator.validate(identification).isEmpty()) {
            throw new WebApplicationException(
                    Response.Status.NOT_ACCEPTABLE
            );
        }
        switch (identification.getType()) {
            case same_as:
                identification = graphElement.addSameAs(
                        identification
                );
                break;
            case type:
                identification = graphElement.addType(
                        identification
                );
                break;
            case generic:
                identification = graphElement.addGenericIdentification(
                        identification
                );
                break;
            default:
                throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        reindexGraphElement();
        return Response.created(URI.create(
                UserUris.graphElementShortId(identification.uri())
        )).entity(
                IdentificationJson.singleToJson(identification)
        ).build();
    }


    @DELETE
    @GraphTransactional
    @Path("/")
    public Response removeFriendlyResource(
            @QueryParam("uri") String identificationUri
    ) {
        Identification friendlyResource = new IdentificationPojo(
                new FriendlyResourcePojo(
                        URI.create(identificationUri)
                )
        );
        graphElement.removeIdentification(friendlyResource);
        reindexGraphElement();
        return Response.noContent().build();
    }

    private void reindexGraphElement() {
        if (GraphElementType.VERTEX == graphElementType) {
            graphIndexer.indexVertex(
                    (VertexOperator) graphElement
            );
            graphIndexer.commit();
        } else if (GraphElementType.EDGE == graphElementType) {
            graphIndexer.indexRelation(
                    (Edge) graphElement
            );
            graphIndexer.commit();
        } else if (GraphElementType.SCHEMA_PROPERTY == graphElementType) {
            graphIndexer.indexSchema(
                    userGraph.schemaPojoWithUri(
                            schemaUri
                    )
            );
            graphIndexer.commit();
        }
    }
}
