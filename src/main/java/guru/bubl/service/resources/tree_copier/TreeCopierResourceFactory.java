/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.tree_copier;

import guru.bubl.module.model.User;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.graph.tree_copier.TreeCopier;
import guru.bubl.service.resources.fork.ForkCollectionResource;

public interface TreeCopierResourceFactory {
    TreeCopierResource forCopier(User user);
}
