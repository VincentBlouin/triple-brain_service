/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package js_test_data.scenarios;

import guru.bubl.module.common_utils.NoEx;
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

public class AutomaticExpandScenario implements JsTestScenario {
    /*
     b1-r1->b2
     b2-r21->b21
     b1-r2->b3
     b3-r31->b31
     b3-r32->b32
     b31-r311->b311
     b32-r321->b321
     b32-r322->b322
     */

    @Inject
    protected GraphFactory graphFactory;

    @Inject
    protected VertexFactory vertexFactory;

    private VertexOperator
            b1,
            b2,
            b21,
            b3,
            b31,
            b311,
            b32,
            b321,
            b322;

    User user = User.withEmailAndUsername("a", "b");

    @Override
    public JSONObject build() {
        UserGraph userGraph = graphFactory.loadForUser(user);
        createVertices();
        createRelations();
        SubGraphPojo subGraphPojo = userGraph.aroundForkUriInShareLevels(
                b1.uri(),
                ShareLevel.allShareLevelsInt
        );
        SubGraphPojo b2SubGraph = userGraph.aroundForkUriInShareLevels(
                b2.uri(),
                ShareLevel.allShareLevelsInt
        );
        SubGraphPojo b3SubGraph = userGraph.aroundForkUriInShareLevels(
                b3.uri(),
                ShareLevel.allShareLevelsInt
        );
        SubGraphPojo b31SubGraph = userGraph.aroundForkUriInShareLevels(
                b31.uri(),
                ShareLevel.allShareLevelsInt
        );
        return NoEx.wrap(() -> new JSONObject()
                .put(
                        "centerGraph",
                        SubGraphJson.toJson(
                                subGraphPojo
                        )
                ).put(
                        "b2SubGraph",
                        SubGraphJson.toJson(
                                b2SubGraph
                        )
                )
                .put(
                        "b3SubGraph",
                        SubGraphJson.toJson(
                                b3SubGraph
                        )
                ).put(
                        "b31SubGraph",
                        SubGraphJson.toJson(
                                b31SubGraph
                        )
                )
        ).get();
    }

    private void createRelations() {
        b1.addRelationToFork(b2).label("r1");
        b2.addRelationToFork(b21).label("r21");
        b1.addRelationToFork(b3).label("r2");
        b3.addRelationToFork(b31).label("r31");
        b3.addRelationToFork(b32).label("r32");
        b31.addRelationToFork(b311).label("r311");
        b32.addRelationToFork(b321).label("r321");
        b32.addRelationToFork(b322).label("r322");
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
        b21 = vertexFactory.createForOwner(
                user.username()
        );
        b21.label("b21");
        b3 = vertexFactory.createForOwner(
                user.username()
        );
        b3.label("b3");
        b31 = vertexFactory.createForOwner(
                user.username()
        );
        b31.label("b31");
        b311 = vertexFactory.createForOwner(
                user.username()
        );
        b311.label("b311");
        b32 = vertexFactory.createForOwner(
                user.username()
        );
        b32.label("b32");
        b321 = vertexFactory.createForOwner(
                user.username()
        );
        b321.label("b321");
        b322 = vertexFactory.createForOwner(
                user.username()
        );
        b322.label("b322");
    }
}
