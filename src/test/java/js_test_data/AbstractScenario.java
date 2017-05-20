/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package js_test_data;

import guru.bubl.module.model.User;
import guru.bubl.module.model.graph.vertex.Vertex;
import guru.bubl.module.model.graph.vertex.VertexFactory;
import guru.bubl.module.model.graph.vertex.VertexOperator;

import javax.inject.Inject;

public class AbstractScenario {

    @Inject
    VertexFactory vertexFactory;

    protected VertexOperator center;
    protected VertexOperator b1;
    protected VertexOperator b2;
    protected VertexOperator b3;

    protected User user = User.withEmailAndUsername("a", "b");

    public void createVertices(){
        center = vertexFactory.createForOwnerUsername(
                user.username()
        );
        center.label("center");
        b1 = vertexFactory.createForOwnerUsername(
                user.username()
        );
        b1.label("b1");
        b2 = vertexFactory.createForOwnerUsername(
                user.username()
        );
        b2.label("b2");
        b3 = vertexFactory.createForOwnerUsername(
                user.username()
        );
        b3.label("b3");
    }
}
