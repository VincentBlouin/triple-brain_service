/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.sort;

import guru.bubl.module.common_utils.NoExRun;
import guru.bubl.module.model.graph.GraphElementOperator;
import guru.bubl.module.model.graph.GraphTransactional;
import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GraphElementSortResource {

    private GraphElementOperator graphElementOperator;

    public GraphElementSortResource(GraphElementOperator graphElementOperator) {
        this.graphElementOperator = graphElementOperator;
    }


    @PUT
    @GraphTransactional
    public Response setSort(JSONObject sortValues) {
        return NoExRun.wrap(() -> {
            graphElementOperator.setSortDate(
                    new Date(
                            sortValues.getLong(
                                    "sortDate"
                            )
                    ),
                    new Date(
                            sortValues.getLong(
                                    "moveDate"
                            )
                    )
            );
            return Response.noContent().build();
        }).get();
    }

}
