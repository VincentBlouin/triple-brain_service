/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package js_test_data.scenarios;

import guru.bubl.module.model.User;
import guru.bubl.module.model.graph.GraphFactory;
import guru.bubl.module.model.graph.ShareLevel;
import guru.bubl.module.model.graph.subgraph.SubGraphJson;
import guru.bubl.module.model.graph.subgraph.SubGraphPojo;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.graph.vertex.VertexFactory;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import js_test_data.JsTestScenario;
import org.codehaus.jettison.json.JSONObject;

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
        UserGraph userGraph = graphFactory.loadForUser(user);
        createVertices();
        b1.addRelationToFork(b2).label("r1");
        b2.addRelationToFork(b3).label("r2");
        b4.addRelationToFork(b2).label("r3");
        b5.addRelationToFork(b1).label("r4");
        SubGraphPojo subGraphPojo = userGraph.aroundForkUriWithDepthInShareLevels(
                b1.uri(),
                2,
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
        b5 = vertexFactory.createForOwner(
                user.username()
        );
        b5.label("b5");
    }
}
