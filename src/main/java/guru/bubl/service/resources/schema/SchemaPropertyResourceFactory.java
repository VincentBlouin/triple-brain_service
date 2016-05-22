/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.schema;

import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.graph.schema.SchemaOperator;

public interface SchemaPropertyResourceFactory {
    SchemaPropertyResource forSchema(
            SchemaOperator schemaOperator,
            UserGraph userGraph
    );
}
