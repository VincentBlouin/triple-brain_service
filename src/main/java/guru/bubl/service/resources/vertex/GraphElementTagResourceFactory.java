/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.vertex;

import guru.bubl.module.model.graph.GraphElementOperator;
import guru.bubl.module.model.graph.GraphElementType;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.service.resources.GraphElementTagResource;

import java.net.URI;

public interface GraphElementTagResourceFactory {
    GraphElementTagResource forGraphElement(
            GraphElementOperator graphElement,
            GraphElementType GraphElementType
    );

    GraphElementTagResource forSchemaProperty(
            GraphElementOperator schemaProperty,
            URI schemaUri,
            UserGraph userGraph
    );
}
