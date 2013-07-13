package org.triple_brain.service.resources.vertex;

import org.triple_brain.module.model.graph.UserGraph;
import org.triple_brain.service.resources.vertex.VertexResource;

/*
* Copyright Mozilla Public License 1.1
*/
public interface VertexResourceFactory {
    public VertexResource withUserGraph(UserGraph userGraph);
}
