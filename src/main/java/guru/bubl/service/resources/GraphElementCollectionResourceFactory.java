/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources;

import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.service.resources.vertex.VertexCollectionResource;

public interface GraphElementCollectionResourceFactory {
    GraphElementCollectionResource withUserGraph(UserGraph userGraph);
}
