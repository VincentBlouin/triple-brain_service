package org.triple_brain.service.resources.schema;

import org.triple_brain.module.model.graph.schema.SchemaOperator;

/**
 * Copyright Mozilla Public License 1.1
 */
public interface SchemaPropertyResourceFactory {
    SchemaPropertyResource forSchema(SchemaOperator schemaOperator);
}
