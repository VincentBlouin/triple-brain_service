/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package js_test_data.scenarios;

import js_test_data.JsTestScenario;
import org.codehaus.jettison.json.JSONObject;
import guru.bubl.module.model.User;
import guru.bubl.module.model.graph.GraphFactory;
import guru.bubl.module.model.graph.SubGraphPojo;
import guru.bubl.module.model.graph.UserGraph;
import guru.bubl.module.model.graph.edge.EdgeOperator;
import guru.bubl.module.model.graph.vertex.VertexFactory;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import guru.bubl.module.model.json.graph.SubGraphJson;

import javax.inject.Inject;

public class GraphWithAnInverseRelationScenario implements JsTestScenario {

    /*
    * me -going straight->straight bubble
    * me <-going inverse-inverse bubble
    */

    @Inject
    protected GraphFactory graphFactory;

    @Inject
    protected VertexFactory vertexFactory;

    User user = User.withEmailAndUsername("a", "b");

    private VertexOperator
            me,
            straightBubble,
            inverseBubble;

    @Override
    public JSONObject build() {
        UserGraph userGraph = graphFactory.createForUser(user);
        createVertices();
        createEdges();
        SubGraphPojo subGraphForMe = userGraph.graphWithDepthAndCenterVertexId(
                1,
                me.uri()
        );
        return SubGraphJson.toJson(
                subGraphForMe
        );
    }

    private void createVertices(){
        me = vertexFactory.createForOwnerUsername(
                user.username()
        );
        me.label("me");
        straightBubble = vertexFactory.createForOwnerUsername(
                user.username()
        );
        straightBubble.label("straight bubble");
        inverseBubble = vertexFactory.createForOwnerUsername(
                user.username()
        );
        inverseBubble.label("inverse bubble");
    }

    private void createEdges(){
        EdgeOperator straight = me.addRelationToVertex(straightBubble);
        straight.label("going straight");
        EdgeOperator inverse = inverseBubble.addRelationToVertex(me);
        inverse.label("going inverse");
    }
}
