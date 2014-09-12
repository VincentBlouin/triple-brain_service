/*
 * Copyright Vincent Blouin under the Mozilla Public License 1.1
 */

package org.triple_brain.service.resources.schema;

import org.triple_brain.module.model.graph.UserGraph;

public interface  SchemaNonOwnedResourceFactory {
    SchemaNonOwnedResource fromUserGraph(UserGraph userGraph);
}
