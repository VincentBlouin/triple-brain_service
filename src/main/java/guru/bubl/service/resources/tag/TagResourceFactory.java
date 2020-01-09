/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.tag;

import guru.bubl.module.model.User;
import guru.bubl.module.model.graph.subgraph.UserGraph;

public interface TagResourceFactory {
    TagResource forAuthenticatedUserAndGraph(
            User user,
            UserGraph userGraph
    );
}
