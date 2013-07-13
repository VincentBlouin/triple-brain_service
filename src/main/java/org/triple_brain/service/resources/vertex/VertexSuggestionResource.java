package org.triple_brain.service.resources.vertex;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.triple_brain.module.model.graph.Vertex;
import org.triple_brain.module.model.json.graph.VertexJsonFields;
import org.triple_brain.module.model.suggestion.Suggestion;
import org.triple_brain.module.model.suggestion.SuggestionOrigin;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

import static org.triple_brain.module.model.json.SuggestionJsonFields.*;

/*
* Copyright Mozilla Public License 1.1
*/
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VertexSuggestionResource {

    private Vertex vertex;

    @AssistedInject
    public VertexSuggestionResource(
            @Assisted Vertex vertex
    ){
        this.vertex = vertex;
    }

    @GET
    @Path("/")
    public Response getSuggestions() {
        try {
            JSONArray suggestions = VertexJsonFields.toJson(vertex)
                    .getJSONArray(VertexJsonFields.SUGGESTIONS);
            return Response.ok(suggestions).build();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/")
    public Response addSuggestions(JSONArray suggestions) {
        vertex.addSuggestions(
                suggestionsSetFromJSONArray(suggestions)
        );
        return Response.ok().build();
    }

    private Suggestion[] suggestionsSetFromJSONArray(JSONArray jsonSuggestions) {
        Suggestion[] suggestions = new Suggestion[jsonSuggestions.length()];
        for (int i = 0; i < jsonSuggestions.length(); i++) {
            try {
                JSONObject jsonSuggestion = jsonSuggestions.getJSONObject(i);
                suggestions[i] = Suggestion.withSameAsDomainLabelAndOrigins(
                        URI.create(jsonSuggestion.getString(TYPE_URI)),
                        URI.create(jsonSuggestion.getString(DOMAIN_URI)),
                        jsonSuggestion.getString(LABEL),
                        SuggestionOrigin.valueOf(jsonSuggestion.getString(ORIGIN))
                );
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        return suggestions;
    }
}
