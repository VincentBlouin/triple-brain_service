package org.triple_brain.service.resources.vertex;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.triple_brain.module.model.graph.Vertex;
import org.triple_brain.module.model.json.graph.VertexJson;
import org.triple_brain.module.model.suggestion.Suggestion;
import org.triple_brain.module.model.suggestion.SuggestionFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/*
* Copyright Mozilla Public License 1.1
*/
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VertexSuggestionResource {

    private Vertex vertex;

    @Inject
    SuggestionFactory suggestionFactory;

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
            JSONArray suggestions = VertexJson.toJson(vertex)
                    .getJSONArray(VertexJson.SUGGESTIONS);
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
        Suggestion[] suggestionImpls = new Suggestion[
                jsonSuggestions.length()
                ];
        for (int i = 0; i < jsonSuggestions.length(); i++) {
            try {
                JSONObject jsonSuggestion = jsonSuggestions.getJSONObject(i);
                suggestionImpls[i] = suggestionFactory.createFromJsonObject(
                    jsonSuggestion
                );
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        return suggestionImpls;
    }
}
