package org.triple_brain.service.resources;

import org.triple_brain.module.model.graph.UserGraph;

/*
* Copyright Mozilla Public License 1.1
*/
public interface VertexResourceFactory {
    public VertexResource withUserGraph(UserGraph userGraph);
}
