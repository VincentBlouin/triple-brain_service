/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package js_test_data.scenarios;

import guru.bubl.module.model.User;
import guru.bubl.module.model.graph.GraphFactory;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.graph.edge.EdgeOperator;
import guru.bubl.module.model.graph.vertex.VertexFactory;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import guru.bubl.module.model.graph.SubGraphJson;
import js_test_data.JsTestScenario;

import javax.inject.Inject;

public class PublicPrivateScenario implements JsTestScenario {

    /*
    * b1-r1->b2
    * b1-r2->b3
    b1 is public
    b2 is public
    b3 is private
    */

    @Inject
    GraphFactory graphFactory;

    @Inject
    VertexFactory vertexFactory;

    private VertexOperator
            b1,
            b2,
            b3;

    private User user = User.withEmailAndUsername("a", "b");


    @Override
    public Object build() {
        UserGraph userGraph = graphFactory.loadForUser(user);
        createVertices();
        createEdges();
        return SubGraphJson.toJson(
                userGraph.graphWithDepthAndCenterBubbleUri(
                        1,
                        b1.uri()
                )
        );
    }

    private void createVertices() {
        b1 = vertexFactory.createForOwnerUsername(
                user.username()
        );
        b1.label("b1");
        b1.makePublic();
        b2 = vertexFactory.createForOwnerUsername(
                user.username()
        );
        b2.label("b2");
        b2.makePublic();
        b3 = vertexFactory.createForOwnerUsername(
                user.username()
        );
        b3.label("b3");
    }

    private void createEdges() {
        EdgeOperator r1 = b1.addRelationToVertex(b2);
        r1.label("r1");
        EdgeOperator r2 = b1.addRelationToVertex(b3);
        r2.label("r2");
    }
}
