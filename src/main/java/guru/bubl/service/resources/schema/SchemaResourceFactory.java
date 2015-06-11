/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.schema;

import guru.bubl.module.model.graph.UserGraph;

public interface SchemaResourceFactory {
    SchemaResource fromUserGraph(UserGraph userGraph);
}
