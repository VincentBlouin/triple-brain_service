package org.triple_brain.service.resources;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import org.codehaus.jettison.json.JSONObject;
import org.triple_brain.module.graphviz_visualisation.GraphToDrawnGraphConverter;
import org.triple_brain.module.model.graph.GraphTransactional;
import org.triple_brain.module.model.graph.SubGraph;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/*
* Copyright Mozilla Public License 1.1
*/
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DrawnGraphResource {

    private SubGraph graph;

    @AssistedInject
    public DrawnGraphResource(
            @Assisted SubGraph graph
    ) {
        this.graph = graph;
    }

    @GET
    @Path("/")
    @GraphTransactional
    public Response get() {
        JSONObject drawnGraph = GraphToDrawnGraphConverter.withGraph(
                graph
        ).convert();
        return Response.ok(
                drawnGraph
        ).build();
    }
}
