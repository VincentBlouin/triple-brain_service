/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.fork;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import guru.bubl.module.model.graph.ShareLevel;
import guru.bubl.module.model.graph.graph_element.ForkCollectionOperatorFactory;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.neo4j_graph_manipulator.graph.graph.graph_element.GraphElementSpecialOperatorFactory;
import guru.bubl.service.resources.CollectionResource;
import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Set;


@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ForkCollectionResource implements CollectionResource {

    @Inject
    private GraphElementSpecialOperatorFactory graphElementSpecialOperatorFactory;

    @Inject
    private ForkCollectionOperatorFactory forkCollectionOperatorFactory;

    private UserGraph userGraph;

    @AssistedInject
    public ForkCollectionResource(
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
        Set<URI> uris = urisFromJsonArray(shareLevelJson.optJSONArray("graphElementsUri").toString());
        if (!areAllUrisOwned(uris)) {
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        }
        for (URI uri : uris) {
            graphElementSpecialOperatorFactory.getForkFromUri(uri).setShareLevel(
                    shareLevel
            );
        }
        return Response.noContent().build();
    }

    @DELETE
    @Path("/")
    public Response deleteGraphElements(String graphElementsUris) {
        Set<URI> uris = urisFromJsonArray(graphElementsUris);
        if (!areAllUrisOwned(uris)) {
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        }
        forkCollectionOperatorFactory.withUris(uris).remove();
        return Response.noContent().build();
    }

    @Override
    public String getUsername() {
        return userGraph.user().username();
    }
}
