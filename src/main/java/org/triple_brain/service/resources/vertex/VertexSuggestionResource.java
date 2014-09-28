/*
 * Copyright Vincent Blouin under the Mozilla Public License 1.1
 */

package org.triple_brain.service.resources.vertex;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.triple_brain.module.model.graph.GraphTransactional;
import org.triple_brain.module.model.graph.edge.Edge;
import org.triple_brain.module.model.graph.vertex.VertexOperator;
import org.triple_brain.module.model.json.SuggestionJson;
import org.triple_brain.module.search.GraphIndexer;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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

    @POST
    @Path("/accept")
    @GraphTransactional
    public Response acceptSuggestion(String suggestionJson) {
        Edge newEdge = vertex.acceptSuggestion(
                SuggestionJson.fromJson(suggestionJson)
        );
        graphIndexer.indexRelation(newEdge);
        graphIndexer.commit();
        try {
            return Response.ok().entity(
                    new JSONObject().put(
                            "edge_uri", newEdge.uri()
                    ).put(
                            "vertex_uri", newEdge.destinationVertex().uri()
                    )
            ).build();
        }catch(JSONException e){
            throw new RuntimeException(e);
        }
    }
}
