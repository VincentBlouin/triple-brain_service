/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.vertex;

import guru.bubl.module.model.graph.GraphElementOperator;
import guru.bubl.module.model.graph.GraphElementType;
import guru.bubl.module.model.graph.UserGraph;
import guru.bubl.service.resources.GraphElementIdentificationResource;

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
