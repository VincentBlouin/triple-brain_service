/*
 * Copyright Vincent Blouin under the Mozilla Public License 1.1
 */

package org.triple_brain.service.resources.vertex;

import org.triple_brain.module.model.graph.UserGraph;

public interface VertexCollectionPublicAccessResourceFactory {
    public VertexCollectionPublicAccessResource withUserGraph(UserGraph userGraph);
}
