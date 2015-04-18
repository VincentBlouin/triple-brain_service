/*
 * Copyright Vincent Blouin under the Mozilla Public License 1.1
 */

package js_test_data.scenarios;

import js_test_data.JsTestScenario;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.triple_brain.module.model.User;
import org.triple_brain.module.model.graph.GraphFactory;
import org.triple_brain.module.model.graph.SubGraphPojo;
import org.triple_brain.module.model.graph.UserGraph;
import org.triple_brain.module.model.graph.vertex.VertexFactory;
import org.triple_brain.module.model.graph.vertex.VertexOperator;
import org.triple_brain.module.model.json.graph.SubGraphJson;

import javax.inject.Inject;

public class GraphWithCircularityScenario implements JsTestScenario {

    /*
    * A graph
    * b1-r1>b2
    * b2-r2>b3
    *
    * Another graph
    * b3-r3->b1
    */

    @Inject
    GraphFactory graphFactory;

    @Inject
    VertexFactory vertexFactory;

    User user = User.withEmailAndUsername("a", "b");

    private VertexOperator
            b1,
            b2,
            b3;

    @Override
    public JSONObject build() {
        UserGraph userGraph = graphFactory.createForUser(user);
        createVertices();
        createRelations();
        SubGraphPojo b1Graph = userGraph.graphWithDepthAndCenterVertexId(
                1,
                b1.uri()
        );
        SubGraphPojo b2Graph = userGraph.graphWithDepthAndCenterVertexId(
                1,
                b2.uri()
        );
        SubGraphPojo b3Graph = userGraph.graphWithDepthAndCenterVertexId(
                1,
                b3.uri()
        );
        try {
            return new JSONObject().put(
                    "b1Graph",
                    SubGraphJson.toJson(
                            b1Graph
                    )
            ).put(
                    "b2Graph",
                    SubGraphJson.toJson(
                            b2Graph
                    )
            ).put(
                    "b3Graph",
                    SubGraphJson.toJson(
                            b3Graph
                    )
            );
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
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
    }

    private void createRelations() {
        b1.addRelationToVertex(b2).label("r1");
        b2.addRelationToVertex(b3).label("r2");
        b3.addRelationToVertex(b1).label("r3");
    }
}
