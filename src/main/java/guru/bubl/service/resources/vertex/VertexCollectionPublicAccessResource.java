/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.vertex;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import guru.bubl.module.model.search.GraphIndexer;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VertexCollectionPublicAccessResource {

    @Inject
    GraphIndexer graphIndexer;

    private UserGraph userGraph;

    @AssistedInject
    public VertexCollectionPublicAccessResource(
            @Assisted UserGraph userGraph
    ) {
        this.userGraph = userGraph;
    }

    @POST
    @Path("/")
    public Response makePublic(JSONArray verticesUri) {
        return makePublicOrPrivate(true, verticesUri);

    }

    @DELETE
    @Path("/")
    public Response makePrivate(JSONArray verticesUri) {
        return makePublicOrPrivate(false, verticesUri);

    }

    private Response makePublicOrPrivate(Boolean makePublic, JSONArray verticesUri) {
        try {
            for (int i = 0; i < verticesUri.length(); i++) {
                VertexOperator vertex = userGraph.vertexWithUri(
                        URI.create(
                                verticesUri.getString(i)
                        )
                );
                if (makePublic) {
                    vertex.makePublic();
                } else {
                    vertex.makePrivate();
                }
            }
        } catch (JSONException e) {
            throw new WebApplicationException(
                    Response.Status.BAD_REQUEST
            );
        }
        return Response.noContent().build();
    }
}
