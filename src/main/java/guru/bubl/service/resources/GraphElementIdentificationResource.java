/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import guru.bubl.module.model.graph.GraphElementOperator;
import guru.bubl.module.model.graph.GraphElementType;
import guru.bubl.module.model.graph.identification.IdentificationFactory;
import guru.bubl.module.model.graph.identification.Identifier;
import guru.bubl.module.model.graph.identification.IdentifierPojo;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.meta.MetaJson;
import guru.bubl.module.model.validator.IdentificationValidator;
import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Map;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GraphElementIdentificationResource {

    private GraphElementOperator graphElement;
    private GraphElementType graphElementType;
    private URI schemaUri;
    private UserGraph userGraph;

    @Inject
    IdentificationFactory identificationFactory;

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
                GraphElementType.Property
        );
        this.schemaUri = schemaUri;
        this.userGraph = userGraph;
    }

    @POST
    @Path("/")
    public Response add(JSONObject identificationJson) {
        IdentificationValidator validator = new IdentificationValidator();
        IdentifierPojo identification = MetaJson.singleFromJson(
                identificationJson.toString()
        );
        if (!validator.validate(identification).isEmpty()) {
            throw new WebApplicationException(
                    Response.Status.NOT_ACCEPTABLE
            );
        }
        Map<URI, IdentifierPojo> identifications = graphElement.addMeta(
                identification
        );
        return Response.ok().entity(
                MetaJson.toJson(identifications)
        ).build();
    }

    @DELETE
    @Path("/")
    public Response removeFriendlyResource(
            @QueryParam("uri") String identificationUri
    ) {
        Identifier identification = identificationFactory.withUri(
                URI.create(identificationUri)
        );
        graphElement.removeIdentification(identification);
        return Response.noContent().build();
    }

}
