/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import guru.bubl.module.model.graph.*;
import guru.bubl.module.model.search.GraphIndexer;
import org.codehaus.jettison.json.JSONObject;
import guru.bubl.module.model.UserUris;
import guru.bubl.module.model.graph.edge.Edge;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import guru.bubl.module.model.json.IdentificationJson;
import guru.bubl.module.model.validator.IdentificationValidator;

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
                GraphElementType.property
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
        Identification identification = graphElement.getIdentificationHavingInternalUri(
                URI.create(identificationUri)
        );
        graphElement.removeIdentification(identification);
        reindexGraphElement();
        return Response.noContent().build();
    }

    private void reindexGraphElement() {
        if (GraphElementType.vertex == graphElementType) {
            graphIndexer.indexVertex(
                    (VertexOperator) graphElement
            );
            graphIndexer.commit();
        } else if (GraphElementType.edge == graphElementType) {
            graphIndexer.indexRelation(
                    (Edge) graphElement
            );
            graphIndexer.commit();
        } else if (GraphElementType.property == graphElementType) {
            graphIndexer.indexSchema(
                    userGraph.schemaPojoWithUri(
                            schemaUri
                    )
            );
            graphIndexer.commit();
        }
    }
}
