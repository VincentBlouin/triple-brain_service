/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.fork;

import guru.bubl.module.model.graph.subgraph.UserGraph;

public interface ForkCollectionResourceFactory {
    ForkCollectionResource withUserGraph(UserGraph userGraph);
}
