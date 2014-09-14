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
    public static enum identification_types {
        SAME_AS, TYPE, GENERIC
    }

    public static final String IDENTIFICATION_TYPE_STRING = "type";

    @Inject
    IdentificationFactory identificationFactory;

    @Inject
    GraphIndexer graphIndexer;

    private GraphElementOperator graphElement;
    private GraphElementType graphElementType;

    @AssistedInject
    public GraphElementIdentificationResource(
            @Assisted GraphElementOperator graphElement,
            @Assisted GraphElementType graphElementType
    ) {
        this.graphElement = graphElement;
        this.graphElementType = graphElementType;
    }

    @POST
    @GraphTransactional
    @Path("/")
    public Response add(JSONObject identificationJson) {
        IdentificationValidator validator = new IdentificationValidator();
        IdentificationPojo identification = IdentificationJson.fromJson(
                identificationJson
        );
        if (!validator.validate(identification).isEmpty()) {
            throw new WebApplicationException(
                    Response.Status.NOT_ACCEPTABLE
            );
        }
        String type = identificationJson.optString("type");
        if (type.equalsIgnoreCase(identification_types.SAME_AS.name())) {
            identification = graphElement.addSameAs(
                    identification
            );
        } else if (type.equalsIgnoreCase(identification_types.TYPE.name())) {
            identification = graphElement.addType(
                    identification
            );
        } else if (type.equalsIgnoreCase(identification_types.GENERIC.name())) {
            identification = graphElement.addGenericIdentification(
                    identification
            );
        } else {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        reindexGraphElement();
        return Response.created(URI.create(
                UserUris.graphElementShortId(identification.uri())
        )).entity(
                IdentificationJson.toJson(identification)
        ).build();
    }

    @DELETE
    @GraphTransactional
    @Path("/")
    public Response removeFriendlyResource(
            @QueryParam("uri") String identificationUri
    ) {
        Identification friendlyResource = identificationFactory.withUri(
                URI.create(identificationUri)
        );
        graphElement.removeIdentification(friendlyResource);
        reindexGraphElement();
        return Response.noContent().build();
    }

    @GraphTransactional
    @Path("image")
    public GraphElementIdentificationImageResource images(@QueryParam("uri") String identificationUri) {
        return new GraphElementIdentificationImageResource(
                identificationFactory.withUri(
                        URI.create(identificationUri)
                )
        );
    }

    @GraphTransactional
    @Path("description")
    public FriendlyResourceDescriptionService description(@QueryParam("uri") String identificationUri) {
        return new FriendlyResourceDescriptionService(
                identificationFactory.withUri(
                        URI.create(identificationUri)
                )
        );
    }

    private void reindexGraphElement() {
        if (GraphElementType.VERTEX == graphElementType) {
            graphIndexer.indexVertex(
                    (VertexOperator) graphElement
            );
            graphIndexer.commit();
        } else if(GraphElementType.EDGE == graphElementType){
            graphIndexer.indexRelation(
                    (Edge) graphElement
            );
            graphIndexer.commit();
        }
    }
}
