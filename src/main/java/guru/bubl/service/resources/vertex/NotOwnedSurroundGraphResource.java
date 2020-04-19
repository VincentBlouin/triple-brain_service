/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.vertex;

import guru.bubl.module.model.graph.ShareLevel;
import guru.bubl.module.model.graph.subgraph.SubGraphJson;
import guru.bubl.module.model.graph.subgraph.SubGraphPojo;
import guru.bubl.module.model.graph.subgraph.UserGraph;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NotOwnedSurroundGraphResource {
    private URI centerUri;
    private UserGraph userGraph;
    private Boolean skipVerification;
    private Boolean isFriend;

    public NotOwnedSurroundGraphResource(
            UserGraph userGraph,
            URI centerUri,
            Boolean skipVerification,
            Boolean isFriend
    ) {
        this.userGraph = userGraph;
        this.centerUri = centerUri;
        this.skipVerification = skipVerification;
        this.isFriend = isFriend;
    }

    @GET
    @Path("/")
    public Response get(@QueryParam("depth") Integer depth) {
        Integer[] inShareLevels;
        if (skipVerification) {
            inShareLevels = ShareLevel.allShareLevelsInt;
        } else if (isFriend) {
            inShareLevels = new Integer[]{
                    ShareLevel.PUBLIC.getIndex(),
                    ShareLevel.PUBLIC_WITH_LINK.getIndex(),
                    ShareLevel.FRIENDS.getIndex()
            };
        } else {
            inShareLevels = new Integer[]{
                    ShareLevel.PUBLIC.getIndex(),
                    ShareLevel.PUBLIC_WITH_LINK.getIndex()
            };
        }
        if (depth == null) {
            depth = 1;
        }

        SubGraphPojo subGraph = userGraph.aroundForkUriWithDepthInShareLevels(
                centerUri,
                depth,
                inShareLevels
        );
        if (!subGraph.hasCenter(centerUri)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(
                SubGraphJson.toJson(
                        subGraph
                )
        ).build();
    }


}
