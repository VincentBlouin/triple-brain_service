/*
 * Copyright Vincent Blouin under the Mozilla Public License 1.1
 */

package org.triple_brain.service.resources.schema;

import org.triple_brain.module.model.graph.UserGraph;
import org.triple_brain.module.model.graph.schema.SchemaOperator;

public interface SchemaPropertyResourceFactory {
    SchemaPropertyResource forSchema(
            SchemaOperator schemaOperator,
            UserGraph userGraph
    );
}
