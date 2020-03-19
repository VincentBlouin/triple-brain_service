/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import guru.bubl.module.model.graph.GraphElementOperator;
import guru.bubl.module.model.graph.GraphElementType;
import guru.bubl.module.model.graph.tag.TagFactory;
import guru.bubl.module.model.graph.tag.Tag;
import guru.bubl.module.model.graph.tag.TagPojo;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.tag.TagJson;
import guru.bubl.module.model.validator.TagValidator;
import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Map;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GraphElementTagResource {

    private GraphElementOperator graphElement;
    private GraphElementType graphElementType;
    private URI schemaUri;
    private UserGraph userGraph;

    @Inject
    TagFactory tagFactory;

    @AssistedInject
    public GraphElementTagResource(
            @Assisted GraphElementOperator graphElement,
            @Assisted GraphElementType graphElementType
    ) {
        this.graphElement = graphElement;
        this.graphElementType = graphElementType;
    }

    @AssistedInject
    public GraphElementTagResource(
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
        TagValidator validator = new TagValidator();
        TagPojo identification = TagJson.singleFromJson(
                identificationJson.toString()
        );
        if (!validator.validate(identification).isEmpty()) {
            throw new WebApplicationException(
                    Response.Status.NOT_ACCEPTABLE
            );
        }
        Map<URI, TagPojo> identifications = graphElement.addTag(
                identification
        );
        return Response.ok().entity(
                TagJson.toJson(identifications)
        ).build();
    }

    @DELETE
    @Path("/")
    public Response removeFriendlyResource(
            @QueryParam("uri") String identificationUri
    ) {
        Tag identification = tagFactory.withUri(
                URI.create(identificationUri)
        );
        graphElement.removeTag(identification);
        return Response.noContent().build();
    }
}
