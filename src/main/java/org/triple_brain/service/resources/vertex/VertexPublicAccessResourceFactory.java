/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package org.triple_brain.service.resources.vertex;

import org.triple_brain.module.model.graph.vertex.VertexOperator;

public interface VertexPublicAccessResourceFactory {
    public VertexPublicAccessResource ofVertex(
            VertexOperator vertex
    );
}
