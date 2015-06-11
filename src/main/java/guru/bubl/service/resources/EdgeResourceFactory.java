/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources;

import guru.bubl.module.model.graph.UserGraph;

public interface EdgeResourceFactory{
    public EdgeResource withUserGraph(UserGraph userGraph);
}
