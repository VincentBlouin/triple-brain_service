/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.sort;

import com.sun.jersey.api.client.ClientResponse;
import guru.bubl.module.common_utils.NoExRun;
import guru.bubl.module.model.graph.GraphElement;
import guru.bubl.module.model.graph.subgraph.SubGraph;
import guru.bubl.module.model.graph.subgraph.SubGraphPojo;
import guru.bubl.module.model.graph.vertex.Vertex;
import guru.bubl.module.model.graph.vertex.VertexInSubGraph;
import guru.bubl.module.model.graph.vertex.VertexInSubGraphPojo;
import guru.bubl.service.utils.GraphManipulationRestTestUtils;
import org.codehaus.jettison.json.JSONObject;
import org.joda.time.DateTime;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.util.Date;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class GraphElementSortResourceTest extends GraphManipulationRestTestUtils {

    private Date sortDate = new DateTime().minusMillis(10).toDate();
    private Date moveDate = new Date();

    @Test
    public void setting_sort_returns_ok_status() {
        assertThat(
                setSort(vertexA()).getStatus(),
                is(Response.Status.OK.getStatusCode())
        );
    }

    @Test
    public void setting_sort_really_sets_sort() {
        SubGraphPojo subGraph = graphUtils().graphWithCenterVertexUri(vertexBUri());
        VertexInSubGraphPojo vertexBInSubGraph = subGraph.vertexWithIdentifier(
                vertexBUri()
        );
        assertThat(
                vertexBInSubGraph.getGraphElement().getSortDate(),
                is(nullValue())
        );
        setSort(vertexB());
        subGraph = graphUtils().graphWithCenterVertexUri(vertexBUri());
        vertexBInSubGraph = subGraph.vertexWithIdentifier(
                vertexBUri()
        );
        assertThat(
                vertexBInSubGraph.getGraphElement().getSortDate(),
                is(not(nullValue()))
        );
        assertThat(
                vertexBInSubGraph.getGraphElement().getSortDate().getTime(),
                is(
                        sortDate.getTime()
                )
        );
        assertThat(
                vertexBInSubGraph.getGraphElement().getMoveDate().getTime(),
                is(
                        moveDate.getTime()
                )
        );
    }

    private ClientResponse setSort(GraphElement graphElement) {
        return NoExRun.wrap(() -> resource
                .path(
                        graphElement.uri().toString()
                ).path(
                        "sort"
                )
                .cookie(authCookie)
                .put(
                        ClientResponse.class,
                        new JSONObject().put(
                                "sortDate",
                                sortDate.getTime()
                        ).put(
                                "moveDate",
                                moveDate.getTime()
                        )
                )
        ).get();
    }

}
