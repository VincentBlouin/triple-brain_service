package guru.bubl.service.resources.group_relation;

import guru.bubl.module.model.graph.subgraph.UserGraph;

public interface GroupRelationResourceFactory {
    GroupRelationResource withUserGraph(UserGraph userGraph);
}
