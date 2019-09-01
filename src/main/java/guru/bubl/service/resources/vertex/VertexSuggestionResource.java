/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.vertex;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import guru.bubl.module.model.graph.edge.Edge;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import guru.bubl.module.model.json.SuggestionJson;
import guru.bubl.module.model.search.GraphIndexer;
import guru.bubl.module.model.suggestion.SuggestionPojo;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Map;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VertexSuggestionResource {

    @Inject
    GraphIndexer graphIndexer;

    private VertexOperator vertex;

    @AssistedInject
    public VertexSuggestionResource(
            @Assisted VertexOperator vertex
    ) {
        this.vertex = vertex;
    }

    @GET
    @Path("/")
    public Response getSuggestions() {
        return Response.ok(
                SuggestionJson.multipleToJson(
                        vertex.getSuggestions()
                )
        ).build();
    }

    @POST
    @Path("/")
    public Response addSuggestions(JSONObject suggestions) {
        vertex.addSuggestions(
                SuggestionJson.fromJsonArray(
                        suggestions.toString()
                )
        );
        return Response.noContent().build();
    }

    @POST
    @Path("/accept")
    public Response acceptSuggestion(String suggestionJson) {
        Edge newEdge = vertex.acceptSuggestion(
                SuggestionJson.fromJson(suggestionJson)
        );
        try {
            return Response.ok().entity(
                    new JSONObject().put(
                            "edge_uri", newEdge.uri()
                    ).put(
                            "vertex_uri", newEdge.destinationVertex().uri()
                    )
            ).build();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @DELETE
    @Path("/")
    public Response deleteSuggestions(JSONArray uris) {
        Map<URI, SuggestionPojo> suggestions = vertex.getSuggestions();
        for(int i = 0; i < uris.length(); i++){
            try {
                URI uriToRemove = URI.create(
                        uris.getString(i)
                );
                if(suggestions.containsKey(uriToRemove)){
                    suggestions.remove(uriToRemove);
                }
            }catch(JSONException e){
                throw new RuntimeException(e);
            }
        }
        vertex.setSuggestions(suggestions);
        return Response.ok().build();
    }
}
