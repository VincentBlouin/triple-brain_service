/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package js_test_data.scenarios;

import guru.bubl.module.model.User;
import guru.bubl.module.model.graph.GraphFactory;
import guru.bubl.module.model.graph.ShareLevel;
import guru.bubl.module.model.graph.SubGraphJson;
import guru.bubl.module.model.graph.subgraph.SubGraphPojo;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.graph.vertex.VertexFactory;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import js_test_data.JsTestScenario;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.inject.Inject;

public class ConvertVertexToGroupRelationScenario implements JsTestScenario {

    /*
     * b1-->b2
     * b2-->b3
     * b2-->b4
     * b2-->b5
     */

    @Inject
    GraphFactory graphFactory;

    @Inject
    VertexFactory vertexFactory;

    private VertexOperator
            b1,
            b2,
            b3,
            b4,
            b5;

    private User user = User.withEmailAndUsername("a", "b");


    @Override
    public Object build() {
        UserGraph userGraph = graphFactory.loadForUser(user);
        createVertices();
        createEdges();
        SubGraphPojo b1Graph = userGraph.aroundForkUriInShareLevels(
                b1.uri(),
                ShareLevel.allShareLevelsInt
        );
        SubGraphPojo b2Graph = userGraph.aroundForkUriInShareLevels(
                b2.uri(),
                ShareLevel.allShareLevelsInt
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
            );
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
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

    private void createEdges() {
        b1.addRelationToFork(b2);
        b2.addRelationToFork(b3);
        b2.addRelationToFork(b4);
        b2.addRelationToFork(b5);
    }
}
