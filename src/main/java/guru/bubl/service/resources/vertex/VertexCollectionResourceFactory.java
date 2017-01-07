/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.vertex;

import guru.bubl.module.model.graph.subgraph.UserGraph;

public interface VertexCollectionResourceFactory {
    VertexCollectionResource withUserGraph(UserGraph userGraph);
}
