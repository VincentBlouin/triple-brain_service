/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package org.triple_brain.service.resources;

import org.triple_brain.module.model.graph.UserGraph;

public interface EdgeResourceFactory{
    public EdgeResource withUserGraph(UserGraph userGraph);
}
