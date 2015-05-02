/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package org.triple_brain.service.resources.vertex;

import org.triple_brain.module.model.graph.UserGraph;
import org.triple_brain.service.resources.vertex.VertexResource;

public interface VertexResourceFactory {
    public VertexResource withUserGraph(UserGraph userGraph);
}
