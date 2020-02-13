package guru.bubl.service.resources;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import guru.bubl.module.model.UserUris;
import guru.bubl.module.model.graph.ShareLevel;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GraphElementCollectionResource {
    private UserGraph userGraph;

    @AssistedInject
    public GraphElementCollectionResource(
            @Assisted UserGraph userGraph
    ) {
        this.userGraph = userGraph;
    }

    @Path("/share-level")
    @POST
    public Response setShareLevel(JSONObject shareLevelJson) {
        ShareLevel shareLevel = ShareLevel.valueOf(
                shareLevelJson.optString("shareLevel", ShareLevel.PRIVATE.name()).toUpperCase()
        );
        JSONArray graphElementsUri = shareLevelJson.optJSONArray("graphElementsUri");
        if (!areAllUrisOwned(graphElementsUri, userGraph.user().username())) {
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        }
        for (int i = 0; i < graphElementsUri.length(); i++) {
            VertexOperator vertex = userGraph.vertexWithUri(
                    URI.create(
                            graphElementsUri.optString(i)
                    )
            );
            vertex.setShareLevel(shareLevel);
        }
        return Response.noContent().build();
    }

    public static Boolean areAllUrisOwned(JSONArray uris, String authenticatedUsername) {
        Boolean isAllOwned = true;
        for (int i = 0; i < uris.length() && isAllOwned; i++) {
            URI uri = URI.create(
                    uris.optString(i)
            );
            if (!UserUris.ownerUserNameFromUri(uri).equals(authenticatedUsername)) {
                isAllOwned = false;
            }
        }
        return isAllOwned;
    }
}
