package guru.bubl.service.resources;

import guru.bubl.module.model.User;
import guru.bubl.module.model.UserUris;
import guru.bubl.module.model.center_graph_element.CenterGraphElementOperator;
import guru.bubl.module.model.center_graph_element.CenterGraphElementOperatorFactory;
import guru.bubl.module.model.graph.graph_element.GraphElementOperator;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import guru.bubl.module.model.json.LocalizedStringJson;
import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Date;
import java.util.Set;

public interface GraphElementResource {

    URI getUriFromShortId(String shortId);

    UserGraph getUserGraph();

    CenterGraphElementOperatorFactory getCenterOperatorFactory();

    GraphElementOperator getOperatorFromShortId(String shortId);

    @Path("/{shortId}/center")
    @POST
    default Response makeCenter(@PathParam("shortId") String shortId, JSONObject data) {
        CenterGraphElementOperator centerGraphElementOperator = getCenterOperatorFactory().usingFriendlyResource(
                getOperatorFromShortId(shortId)
        );
        centerGraphElementOperator.incrementNumberOfVisits();
        centerGraphElementOperator.setLastCenterDate(
                new Date(
                        data.optLong(
                                "lastCenterDate",
                                new Date().getTime()
                        )
                )
        );
        return Response.noContent().build();
    }

    @Path("/{shortId}/center")
    @DELETE
    default Response removeCenter(@PathParam("shortId") String shortId) {
        getCenterOperatorFactory().usingFriendlyResource(
                getOperatorFromShortId(shortId)
        ).remove();
        return Response.noContent().build();
    }


    @DELETE
    @Path("/{shortId}")
    default Response remove(
            @PathParam("shortId") String shortId
    ) {
        getOperatorFromShortId(shortId).remove();
        return Response.ok().build();
    }

    @POST
    @Path("{shortId}/label")
    default Response updateVertexLabel(
            @PathParam("shortId") String shortId,
            JSONObject localizedLabel
    ) {
        getOperatorFromShortId(shortId).label(
                localizedLabel.optString(
                        LocalizedStringJson.content.name()
                )
        );
        return Response.noContent().build();
    }

    @POST
    @Path("{shortId}/comment")
    @Consumes(MediaType.TEXT_PLAIN)
    default Response updateVertexComments(
            @PathParam("shortId") String shortId,
            String comment
    ) {
        getOperatorFromShortId(shortId).comment(comment);
        return Response.noContent().build();
    }

    default User getUser() {
        return getUserGraph().user();
    }

}
