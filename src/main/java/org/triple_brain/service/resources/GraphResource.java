/*
 * Copyright Vincent Blouin under the Mozilla Public License 1.1
 */

package org.triple_brain.service.resources;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import org.triple_brain.module.model.User;
import org.triple_brain.module.model.graph.GraphFactory;
import org.triple_brain.module.model.graph.UserGraph;
import org.triple_brain.service.resources.schema.SchemaResource;
import org.triple_brain.service.resources.schema.SchemaResourceFactory;
import org.triple_brain.service.resources.vertex.VertexResource;
import org.triple_brain.service.resources.vertex.VertexResourceFactory;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GraphResource {

    @Inject
    GraphFactory graphFactory;

    @Inject
    VertexResourceFactory vertexResourceFactory;

    @Inject
    EdgeResourceFactory edgeResourceFactory;

    @Inject
    SchemaResourceFactory schemaResourceFactory;

    private User user;

    @AssistedInject
    public GraphResource(
            @Assisted User user
    ) {
        this.user = user;
    }

    @Path("/vertex")
    public VertexResource vertexResource() {
        return vertexResourceFactory.withUserGraph(
                userGraph()
        );
    }

    @Path("/edge")
    public EdgeResource edgeResource() {
        return edgeResourceFactory.withUserGraph(
                userGraph()
        );
    }

    @Path("/schema")
    public SchemaResource schemaResource() {
        return schemaResourceFactory.fromUserGraph(
                userGraph()
        );
    }

    private UserGraph userGraph() {
        return graphFactory.loadForUser(
                user
        );
    }

}
