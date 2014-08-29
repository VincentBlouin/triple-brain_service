package org.triple_brain.service.resources.schema;

import org.triple_brain.module.model.graph.UserGraph;

/**
 * Copyright Mozilla Public License 1.1
 */
public interface SchemaResourceFactory {
    SchemaResource fromUserGraph(UserGraph userGraph);
}
