package org.triple_brain.service.resources.vertex;

import org.triple_brain.module.model.graph.Vertex;

/*
* Copyright Mozilla Public License 1.1
*/
public interface VertexSurroundGraphResourceFactory {
    public VertexSurroundGraphResource ofCenterVertexWithDepth(
            Vertex centerVertex,
            Integer depth
    );
}
