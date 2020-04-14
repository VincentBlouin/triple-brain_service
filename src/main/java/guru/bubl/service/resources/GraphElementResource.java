package guru.bubl.service.resources;

import guru.bubl.module.model.center_graph_element.CenterGraphElementOperator;
import guru.bubl.module.model.center_graph_element.CenterGraphElementOperatorFactory;
import guru.bubl.module.model.graph.GraphElementOperator;
import org.codehaus.jettison.json.JSONObject;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Date;

public abstract class GraphElementResource {

    @Inject
    private CenterGraphElementOperatorFactory centerGraphElementOperatorFactory;

    protected abstract URI getUriFromShortId(String shortId);

    protected abstract GraphElementOperator getOperatorFromShortId(String shortId);

    @Path("/{type}/{shortId}/center")
    @POST
    public Response makeCenter(@PathParam("shortId") String shortId, JSONObject data) {
        CenterGraphElementOperator centerGraphElementOperator = centerGraphElementOperatorFactory.usingFriendlyResource(
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
    public Response removeCenter(@PathParam("shortId") String shortId) {
        centerGraphElementOperatorFactory.usingFriendlyResource(
                getOperatorFromShortId(shortId)
        ).remove();
        return Response.noContent().build();
    }
}
