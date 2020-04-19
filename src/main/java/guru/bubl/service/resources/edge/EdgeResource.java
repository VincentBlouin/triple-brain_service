package guru.bubl.service.resources.edge;

import guru.bubl.module.model.UserUris;
import guru.bubl.module.model.graph.graph_element.GraphElementType;
import guru.bubl.module.model.graph.ShareLevel;
import guru.bubl.module.model.graph.edge.EdgeOperator;
import guru.bubl.service.resources.GraphElementResource;
import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.net.URI;

public interface EdgeResource extends GraphElementResource {

    @PUT
    @Path("{shortId}/source/{sourceVertexShortId}")
    default Response changeSource(
            @PathParam("shortId") String shortId,
            @PathParam("sourceVertexShortId") String sourceShortId,
            JSONObject params
    ) {
        GraphElementType forkType = GraphElementType.valueOf(
                params.optString("forkType", GraphElementType.Vertex.name())
        );
        UserUris userUris = new UserUris(getUser());
        URI forkUri = forkType == GraphElementType.GroupRelation ?
                userUris.groupRelationUriFromShortId(sourceShortId) :
                userUris.vertexUriFromShortId(sourceShortId);
        getEdgeOperatorFromShortId(shortId).changeSource(
                forkUri,
                ShareLevel.valueOf(
                        params.optString("oldEndShareLevel", ShareLevel.PRIVATE.name()).toUpperCase()
                ),
                ShareLevel.valueOf(
                        params.optString("keptEndShareLevel", ShareLevel.PRIVATE.name()).toUpperCase()
                ),
                ShareLevel.valueOf(
                        params.optString("newEndShareLevel", ShareLevel.PRIVATE.name()).toUpperCase()
                )
        );
        return Response.noContent().build();
    }

    @PUT
    @Path("{shortId}/destination/{destinationShortId}")
    default Response changeDestination(
            @PathParam("shortId") String shortId,
            @PathParam("destinationShortId") String destinationShortId,
            JSONObject params
    ) {
        GraphElementType forkType = GraphElementType.valueOf(
                params.optString("forkType", GraphElementType.Vertex.name())
        );
        UserUris userUris = new UserUris(getUser());
        URI forkUri = forkType == GraphElementType.GroupRelation ?
                userUris.groupRelationUriFromShortId(destinationShortId) :
                userUris.vertexUriFromShortId(destinationShortId);
        getEdgeOperatorFromShortId(shortId).changeDestination(
                forkUri,
                ShareLevel.valueOf(
                        params.optString("oldEndShareLevel", ShareLevel.PRIVATE.name()).toUpperCase()
                ),
                ShareLevel.valueOf(
                        params.optString("keptEndShareLevel", ShareLevel.PRIVATE.name()).toUpperCase()
                ),
                ShareLevel.valueOf(
                        params.optString("newEndShareLevel", ShareLevel.PRIVATE.name()).toUpperCase()
                )
        );
        return Response.noContent().build();
    }

    EdgeOperator getEdgeOperatorFromShortId(String shortId);
}
