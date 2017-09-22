/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package js_test_data;

import guru.bubl.module.model.User;
import guru.bubl.module.model.graph.GraphFactory;
import guru.bubl.module.model.graph.edge.EdgeOperator;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.graph.vertex.Vertex;
import guru.bubl.module.model.graph.vertex.VertexFactory;
import guru.bubl.module.model.graph.vertex.VertexOperator;

import javax.inject.Inject;

public class AbstractScenario {

    @Inject
    VertexFactory vertexFactory;

    @Inject
    protected GraphFactory graphFactory;

    protected VertexOperator
            center,
            b1,
            b2,
            b3;

    protected EdgeOperator
            r1,
            r2,
            r3;

    protected User user = User.withEmailAndUsername("a", "b");
    protected UserGraph userGraph;

    public void createUserGraph() {
        userGraph = graphFactory.loadForUser(user);
    }

    public void createVertices() {
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

    public void createEdges() {
        EdgeOperator r1 = center.addRelationToVertex(b1);
        r1.label("r1");
        EdgeOperator r2 = center.addRelationToVertex(b2);
        r2.label("r2");
        EdgeOperator r3 = center.addRelationToVertex(b3);
        r3.label("r3");
    }
}
