/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.vertex;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import guru.bubl.module.model.graph.GraphTransactional;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VertexCollectionResource {

    @Inject
    VertexCollectionPublicAccessResourceFactory vertexCollectionPublicAccessResourceFactory;

    private UserGraph userGraph;

    @AssistedInject
    public VertexCollectionResource(
            @Assisted UserGraph userGraph
    ) {
        this.userGraph = userGraph;
    }

    @DELETE
    @Path("/")
    @GraphTransactional
    public Response deleteVerticesRequest(JSONArray verticesUri) {
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

    /*
    * public_access should be in the vertex collection
    * resource but it doesn't work. I get 415 http status
    * Unsupported media type. I don't understand.
    */
//    @Path("public_access")
//    @GraphTransactional
//    public VertexCollectionPublicAccessResource getCollectionPublicAccessResource() {
//        return vertexCollectionPublicAccessResourceFactory.withUserGraph(
//                userGraph
//        );
//    }
}
