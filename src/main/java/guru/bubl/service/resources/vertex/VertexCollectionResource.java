/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.vertex;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import guru.bubl.module.model.UserUris;
import guru.bubl.module.model.graph.ShareLevel;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

import static guru.bubl.service.resources.GraphElementCollectionResource.areAllUrisOwned;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VertexCollectionResource {

    private UserGraph userGraph;

    @AssistedInject
    public VertexCollectionResource(
            @Assisted UserGraph userGraph
    ) {
        this.userGraph = userGraph;
    }

    @DELETE
    @Path("/")
    public Response deleteVerticesRequest(JSONArray verticesUri) {
        if (!areAllUrisOwned(verticesUri, userGraph.user().username())) {
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        }
        deleteVertices(
                verticesUri
        );
        return Response.noContent().build();
    }

    private void deleteVertices(JSONArray verticesUri) {
        try {
            for (int i = 0; i < verticesUri.length(); i++) {
                VertexOperator vertex = userGraph.vertexWithUri(
                        URI.create(
                                verticesUri.getString(i)
                        )
                );
                vertex.remove();
            }
        } catch (JSONException e) {
            throw new WebApplicationException(
                    Response.Status.BAD_REQUEST
            );
        }
    }

}
