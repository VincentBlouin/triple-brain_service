/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.edge;

import guru.bubl.module.model.graph.subgraph.UserGraph;

public interface EdgeResourceFactory{
    EdgeResource withUserGraph(UserGraph userGraph);
}
