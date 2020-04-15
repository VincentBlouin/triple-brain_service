/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.relation;

import guru.bubl.module.model.graph.subgraph.UserGraph;

public interface RelationResourceFactory {
    RelationResource withUserGraph(UserGraph userGraph);
}
