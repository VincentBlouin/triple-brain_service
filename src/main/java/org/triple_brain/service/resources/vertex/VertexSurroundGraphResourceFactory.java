package org.triple_brain.service.resources.vertex;

import org.triple_brain.module.model.graph.UserGraph;
import org.triple_brain.module.model.graph.vertex.Vertex;

/*
* Copyright Mozilla Public License 1.1
*/
public interface VertexSurroundGraphResourceFactory {
    public VertexSurroundGraphResource ofUserGraphCenterVertexAndDepth(
            UserGraph userGraph,
            Vertex centerVertex,
            Integer depth
    );
}
