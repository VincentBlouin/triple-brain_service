/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package js_test_data.scenarios;

import com.google.gson.Gson;
import guru.bubl.module.common_utils.NoExRun;
import guru.bubl.module.model.User;
import guru.bubl.module.model.graph.GraphFactory;
import guru.bubl.module.model.graph.SubGraphJson;
import guru.bubl.module.model.graph.edge.EdgeOperator;
import guru.bubl.module.model.graph.subgraph.SubGraphPojo;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.graph.vertex.VertexFactory;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import guru.bubl.test.module.utils.ModelTestScenarios;
import js_test_data.JsTestScenario;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import javax.inject.Inject;

public class SameLevelRelationsWithMoreThanOneCommonMetaScenario implements JsTestScenario {

    /*
    * center - r1 -> b1
    * center - r2 -> b2
    * center - r3 -> b3
    * center - other relation -> other bubble
    *
    * r1 - meta -> creator
    * r1 - meta -> possession
    * r2 - meta -> creator
    * r2 - meta -> possession
    * r3 - meta -> creator
    * r3 has no meta to possession
    */

    @Inject
    protected GraphFactory graphFactory;

    @Inject
    VertexFactory vertexFactory;

    @Inject
    protected ModelTestScenarios modelTestScenarios;

    VertexOperator
            center,
            b1,
            b2,
            b3,
            otherBubble;

    User user = User.withEmailAndUsername("a", "b");

    UserGraph userGraph;

    @Override
    public Object build() {
        userGraph = graphFactory.createForUser(user);
        createVertices();
        createRelations();
        SubGraphPojo subGraphPojo = userGraph.graphWithDepthAndCenterBubbleUri(
                1,
                center.uri()
        );
        return SubGraphJson.toJson(subGraphPojo);
    }

    private void createVertices() {
        center = vertexFactory.createForOwnerUsername(
                user.username()
        );
        center.label("center");
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
        otherBubble = vertexFactory.createForOwnerUsername(
                user.username()
        );
        otherBubble.label("other bubble");
    }

    private void createRelations() {
        EdgeOperator r1 = center.addRelationToVertex(b1);
        r1.label("r1");
        r1.addMeta(modelTestScenarios.creatorPredicate());
        r1.addMeta(modelTestScenarios.possessionIdentification());
        EdgeOperator r2 = center.addRelationToVertex(b2);
        r2.label("r2");
        r2.addMeta(modelTestScenarios.creatorPredicate());
        r2.addMeta(modelTestScenarios.possessionIdentification());
        EdgeOperator r3 = center.addRelationToVertex(b3);
        r3.label("r3");
        r3.addMeta(modelTestScenarios.creatorPredicate());
        center.addRelationToVertex(otherBubble).label("other relation");
    }
}
