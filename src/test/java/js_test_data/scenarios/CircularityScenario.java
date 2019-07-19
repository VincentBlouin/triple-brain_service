/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package js_test_data.scenarios;

import guru.bubl.module.model.graph.ShareLevel;
import js_test_data.JsTestScenario;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import guru.bubl.module.model.User;
import guru.bubl.module.model.graph.GraphFactory;
import guru.bubl.module.model.graph.subgraph.SubGraphPojo;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.graph.vertex.VertexFactory;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import guru.bubl.module.model.graph.SubGraphJson;

import javax.inject.Inject;

public class CircularityScenario implements JsTestScenario {

    /*
     * A graph
     * b1-r1->b2
     * b2-r2->b3
     * b2-r4->b4
     *
     * Another graph
     * b3-r3->b1
     * b3-r5->b5
     */

    @Inject
    GraphFactory graphFactory;

    @Inject
    VertexFactory vertexFactory;

    User user = User.withEmailAndUsername("a", "b");

    private VertexOperator
            b1,
            b2,
            b3,
            b4,
            b5;

    @Override
    public JSONObject build() {
        UserGraph userGraph = graphFactory.loadForUser(user);
        createVertices();
        createRelations();
        SubGraphPojo b1Graph = userGraph.aroundVertexUriInShareLevels(
                b1.uri(),
                ShareLevel.allShareLevels
        );
        SubGraphPojo b2Graph = userGraph.aroundVertexUriInShareLevels(
                b2.uri(),
                ShareLevel.allShareLevels
        );
        SubGraphPojo b3Graph = userGraph.aroundVertexUriInShareLevels(
                b3.uri(),
                ShareLevel.allShareLevels
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

    private void createRelations() {
        b1.addRelationToVertex(b2).label("r1");
        b2.addRelationToVertex(b3).label("r2");
        b2.addRelationToVertex(b4).label("r4");
        b3.addRelationToVertex(b1).label("r3");
        b3.addRelationToVertex(b5).label("r5");
    }
}
