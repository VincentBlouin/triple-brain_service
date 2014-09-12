/*
 * Copyright Vincent Blouin under the Mozilla Public License 1.1
 */

package org.triple_brain.service.resources.vertex;

import org.triple_brain.module.model.graph.GraphElementOperator;
import org.triple_brain.module.model.graph.GraphElementType;
import org.triple_brain.service.resources.GraphElementIdentificationResource;

public interface GraphElementIdentificationResourceFactory {
    public GraphElementIdentificationResource forGraphElement(
            GraphElementOperator graphElement,
            GraphElementType GraphElementType
    );
}
