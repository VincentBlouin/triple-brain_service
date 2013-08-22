package org.triple_brain.service.resources.vertex;

import org.triple_brain.module.model.graph.GraphElement;
import org.triple_brain.service.resources.GraphElementIdentificationResource;

/*
* Copyright Mozilla Public License 1.1
*/
public interface GraphElementIdentificationResourceFactory {
    public GraphElementIdentificationResource forGraphElement(GraphElement graphElement);
}
