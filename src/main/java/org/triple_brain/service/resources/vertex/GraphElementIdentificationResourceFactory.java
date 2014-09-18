/*
 * Copyright Vincent Blouin under the Mozilla Public License 1.1
 */

package org.triple_brain.service.resources.vertex;

import org.triple_brain.module.model.graph.GraphElementOperator;
import org.triple_brain.module.model.graph.GraphElementType;
import org.triple_brain.module.model.graph.UserGraph;
import org.triple_brain.service.resources.GraphElementIdentificationResource;

import java.net.URI;

public interface GraphElementIdentificationResourceFactory {
    GraphElementIdentificationResource forGraphElement(
            GraphElementOperator graphElement,
            GraphElementType GraphElementType
    );

    GraphElementIdentificationResource forSchemaProperty(
            GraphElementOperator schemaProperty,
            URI schemaUri,
            UserGraph userGraph
    );
}
