package org.triple_brain.service.resources;

import org.triple_brain.module.model.graph.Vertex;
import org.triple_brain.service.resources.vertex.VertexSuggestionResource;

/*
* Copyright Mozilla Public License 1.1
*/
public interface VertexSuggestionResourceFactory {
    public VertexSuggestionResource ofVertex(Vertex vertex);
}
