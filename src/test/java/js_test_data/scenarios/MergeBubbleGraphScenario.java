/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package js_test_data.scenarios;

import guru.bubl.module.model.User;
import guru.bubl.module.model.graph.GraphFactory;
import guru.bubl.module.model.graph.ShareLevel;
import guru.bubl.module.model.graph.subgraph.SubGraphJson;
import guru.bubl.module.model.graph.relation.RelationOperator;
import guru.bubl.module.model.graph.subgraph.SubGraphPojo;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.graph.vertex.VertexFactory;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import js_test_data.JsTestScenario;
import org.codehaus.jettison.json.JSONObject;

import javax.inject.Inject;

public class MergeBubbleGraphScenario implements JsTestScenario {

    /*
     one bubble labeled merge
     merge contains bubbles
     b1-r1->b2
     b2-r2->b3
     b1<-r4-b4
     */

    @Inject
    protected GraphFactory graphFactory;

    @Inject
    protected VertexFactory vertexFactory;

    private VertexOperator
            b1,
            b2,
            b3,
            b4;

    private RelationOperator
            r1,
            r2,
            r4;

    User user = User.withEmailAndUsername("a", "b");

    @Override
    public JSONObject build() {
        UserGraph userGraph = graphFactory.loadForUser(user);
        createVertices();
        createEdges();
        VertexOperator mergeBubble = vertexFactory.createForOwner(
                user.username()
        );
        mergeBubble.label("merge");
        SubGraphPojo subGraphPojo = userGraph.aroundForkUriInShareLevels(
                mergeBubble.uri(),
                ShareLevel.allShareLevelsInt
        );
        return SubGraphJson.toJson(
                subGraphPojo
        );
    }

    private void createVertices() {
        b1 = vertexFactory.createForOwner(
                user.username()
        );
        b1.label("b1");
        b2 = vertexFactory.createForOwner(
                user.username()
        );
        b2.label("b2");
        b3 = vertexFactory.createForOwner(
                user.username()
        );
        b3.label("b3");
        b4 = vertexFactory.createForOwner(
                user.username()
        );
        b4.label("b4");
    }

    private void createEdges() {
        r1 = b1.addRelationToFork(b2);
        r1.label("r1");
        r2 = b2.addRelationToFork(b3);
        r2.label("r2");
        r4 = b4.addRelationToFork(b1);
        r4.label("r4");
    }
}
