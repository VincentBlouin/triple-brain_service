/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package js_test_data.scenarios;

import guru.bubl.module.model.graph.ShareLevel;
import js_test_data.JsTestScenario;
import org.codehaus.jettison.json.JSONObject;
import guru.bubl.module.model.User;
import guru.bubl.module.model.graph.GraphFactory;
import guru.bubl.module.model.graph.subgraph.SubGraphPojo;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.graph.edge.EdgeOperator;
import guru.bubl.module.model.graph.vertex.VertexFactory;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import guru.bubl.module.model.graph.SubGraphJson;

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
        UserGraph userGraph = graphFactory.loadForUser(user);
        userGraph.createVertex();
        createVertices();
        createEdges();
        SubGraphPojo subGraphForMe = userGraph.aroundVertexUriInShareLevels(
                me.uri(),
                ShareLevel.allShareLevels
        );
        return SubGraphJson.toJson(
                subGraphForMe
        );
    }

    private void createVertices(){
        me = vertexFactory.createForOwner(
                user.username()
        );
        me.label("me");
        straightBubble = vertexFactory.createForOwner(
                user.username()
        );
        straightBubble.label("straight bubble");
        inverseBubble = vertexFactory.createForOwner(
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
