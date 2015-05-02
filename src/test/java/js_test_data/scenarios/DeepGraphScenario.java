/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package js_test_data.scenarios;

import js_test_data.JsTestScenario;
import org.codehaus.jettison.json.JSONObject;
import org.triple_brain.module.model.User;
import org.triple_brain.module.model.graph.GraphFactory;
import org.triple_brain.module.model.graph.SubGraphPojo;
import org.triple_brain.module.model.graph.UserGraph;
import org.triple_brain.module.model.graph.edge.EdgeOperator;
import org.triple_brain.module.model.graph.vertex.VertexFactory;
import org.triple_brain.module.model.graph.vertex.VertexOperator;
import org.triple_brain.module.model.json.graph.SubGraphJson;

import javax.inject.Inject;

public class DeepGraphScenario implements JsTestScenario {
    /*
     b1-r1->b2
     b2-r2->b3
     b2<-r3-b4
     b1<-r4-b5
     */

    @Inject
    protected GraphFactory graphFactory;

    @Inject
    protected VertexFactory vertexFactory;

    private VertexOperator
            b1,
            b2,
            b3,
            b4,
            b5;

    User user = User.withEmailAndUsername("a", "b");

    @Override
    public JSONObject build() {
        UserGraph userGraph = graphFactory.createForUser(user);
        createVertices();
        b1.addRelationToVertex(b2).label("r1");
        b2.addRelationToVertex(b3).label("r2");
        b4.addRelationToVertex(b2).label("r3");
        b5.addRelationToVertex(b1).label("r4");
        SubGraphPojo subGraphPojo = userGraph.graphWithDepthAndCenterVertexId(
                2,
                b1.uri()
        );
        return SubGraphJson.toJson(
                subGraphPojo
        );
    }

    private void createVertices() {
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
        b4 = vertexFactory.createForOwnerUsername(
                user.username()
        );
        b4.label("b4");
        b5 = vertexFactory.createForOwnerUsername(
                user.username()
        );
        b5.label("b5");
    }
}
