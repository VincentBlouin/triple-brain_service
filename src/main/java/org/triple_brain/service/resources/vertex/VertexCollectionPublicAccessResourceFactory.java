package org.triple_brain.service.resources.vertex;

import org.triple_brain.module.model.graph.UserGraph;

/**
 * Copyright Mozilla Public License 1.1
 */
public interface VertexCollectionPublicAccessResourceFactory {
    public VertexCollectionPublicAccessResource withUserGraph(UserGraph userGraph);
}
