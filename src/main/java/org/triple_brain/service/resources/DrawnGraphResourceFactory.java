package org.triple_brain.service.resources;

import org.triple_brain.module.model.graph.SubGraph;

/*
* Copyright Mozilla Public License 1.1
*/
public interface DrawnGraphResourceFactory {
    public DrawnGraphResource ofGraph(SubGraph graph);
}
