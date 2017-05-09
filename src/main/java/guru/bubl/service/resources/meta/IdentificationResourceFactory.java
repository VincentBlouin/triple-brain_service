/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.meta;

import guru.bubl.module.model.User;
import guru.bubl.module.model.graph.subgraph.UserGraph;

public interface IdentificationResourceFactory {
    IdentifierResource forAuthenticatedUserAndGraph(
            User user,
            UserGraph userGraph
    );
}
