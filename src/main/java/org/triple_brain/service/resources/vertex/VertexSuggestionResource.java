package org.triple_brain.service.resources.vertex;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import org.codehaus.jettison.json.JSONArray;
import org.triple_brain.module.model.graph.GraphTransactional;
import org.triple_brain.module.model.graph.vertex.VertexOperator;
import org.triple_brain.module.model.json.SuggestionJson;
import org.triple_brain.module.model.suggestion.SuggestionFactory;
import org.triple_brain.module.model.suggestion.SuggestionOperator;
import org.triple_brain.module.model.suggestion.SuggestionPojo;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashSet;
import java.util.Set;

/*
* Copyright Mozilla Public License 1.1
*/
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VertexSuggestionResource {

    private VertexOperator vertex;

    @Inject
    SuggestionFactory suggestionFactory;

    @AssistedInject
    public VertexSuggestionResource(
            @Assisted VertexOperator vertex
    ) {
        this.vertex = vertex;
    }

    @GET
    @Path("/")
    @GraphTransactional
    public Response getSuggestions() {
        return Response.ok(
                SuggestionJson.inVertex(vertex)
        ).build();
    }

    @POST
    @Path("/")
    @GraphTransactional
    public Response addSuggestions(JSONArray suggestions) {
        vertex.addSuggestions(
                SuggestionJson.fromJsonArray(suggestions.toString())
        );
        return Response.noContent().build();
    }
}
