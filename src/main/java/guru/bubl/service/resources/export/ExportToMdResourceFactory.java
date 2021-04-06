package guru.bubl.service.resources.export;

import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.service.resources.relation.RelationResource;

public interface ExportToMdResourceFactory {
    ExportToMdResource forUsername(String username);
}
