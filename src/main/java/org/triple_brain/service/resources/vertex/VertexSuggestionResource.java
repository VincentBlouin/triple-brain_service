/*
 * Copyright Vincent Blouin under the Mozilla Public License 1.1
 */

package org.triple_brain.service.resources.vertex;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import org.codehaus.jettison.json.JSONArray;
import org.triple_brain.module.model.graph.GraphTransactional;
import org.triple_brain.module.model.graph.vertex.VertexOperator;
import org.triple_brain.module.model.json.SuggestionJson;
import org.triple_brain.module.model.suggestion.SuggestionFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VertexSuggestionResource {

    private VertexOperator vertex;

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
                SuggestionJson.multipleToJson(
                        vertex.getSuggestions()
                )
        ).build();
    }

    @POST
    @Path("/")
    @GraphTransactional
    public Response addSuggestions(JSONArray suggestions) {
        vertex.setSuggestions(
                SuggestionJson.fromJsonArray(
                        suggestions.toString()
                )
        );
        return Response.noContent().build();
    }
}
