package org.triple_brain.service.resources.vertex;

import org.triple_brain.module.model.graph.vertex.VertexOperator;

/*
* Copyright Mozilla Public License 1.1
*/
public interface VertexPublicAccessResourceFactory {
    public VertexPublicAccessResource ofVertex(
            VertexOperator vertex
    );
}
