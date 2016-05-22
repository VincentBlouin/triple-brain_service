/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.vertex;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import guru.bubl.module.model.search.GraphIndexer;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import guru.bubl.module.model.graph.GraphTransactional;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.graph.vertex.VertexOperator;

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
    @GraphTransactional
    public Response makePublicOrPrivateOperation(@QueryParam("type") String type, JSONArray verticesUri) {
        /*
        * @DELETE should be used instead of having a type queryparam but data cannot be sent to a DELETE operation
        * because of a java bug fixed in version 8.
        * see https://bugs.openjdk.java.net/browse/JDK-7157360
        * todo Refactor once running on java 8 and greater
        * */
        return makePublicOrPrivate(type.equals("public"), verticesUri);

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
                graphIndexer.indexVertex(
                        vertex
                );
            }
        } catch (JSONException e) {
            throw new WebApplicationException(
                    Response.Status.BAD_REQUEST
            );
        }
        graphIndexer.commit();
        return Response.noContent().build();
    }
}
