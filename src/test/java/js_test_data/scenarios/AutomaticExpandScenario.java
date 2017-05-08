/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package js_test_data.scenarios;

import guru.bubl.module.common_utils.NoExRun;
import guru.bubl.module.model.User;
import guru.bubl.module.model.graph.GraphFactory;
import guru.bubl.module.model.graph.subgraph.SubGraphPojo;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.graph.vertex.VertexFactory;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import guru.bubl.module.model.json.graph.SubGraphJson;
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
        UserGraph userGraph = graphFactory.createForUser(user);
        createVertices();
        createRelations();
        SubGraphPojo subGraphPojo = userGraph.graphWithDepthAndCenterBubbleUri(
                1,
                b1.uri()
        );
        SubGraphPojo b2SubGraph = userGraph.graphWithDepthAndCenterBubbleUri(
                1,
                b2.uri()
        );
        SubGraphPojo b3SubGraph = userGraph.graphWithDepthAndCenterBubbleUri(
                1,
                b3.uri()
        );
        SubGraphPojo b31SubGraph = userGraph.graphWithDepthAndCenterBubbleUri(
                1,
                b31.uri()
        );
        return NoExRun.wrap(() -> new JSONObject()
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
        b1.addRelationToVertex(b2).label("r1");
        b2.addRelationToVertex(b21).label("r21");
        b1.addRelationToVertex(b3).label("r2");
        b3.addRelationToVertex(b31).label("r31");
        b3.addRelationToVertex(b32).label("r32");
        b31.addRelationToVertex(b311).label("r311");
        b32.addRelationToVertex(b321).label("r321");
        b32.addRelationToVertex(b322).label("r322");
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
        b21 = vertexFactory.createForOwnerUsername(
                user.username()
        );
        b21.label("b21");
        b3 = vertexFactory.createForOwnerUsername(
                user.username()
        );
        b3.label("b3");
        b31 = vertexFactory.createForOwnerUsername(
                user.username()
        );
        b31.label("b31");
        b311 = vertexFactory.createForOwnerUsername(
                user.username()
        );
        b311.label("b311");
        b32 = vertexFactory.createForOwnerUsername(
                user.username()
        );
        b32.label("b32");
        b321 = vertexFactory.createForOwnerUsername(
                user.username()
        );
        b321.label("b321");
        b322 = vertexFactory.createForOwnerUsername(
                user.username()
        );
        b322.label("b322");
    }
}
