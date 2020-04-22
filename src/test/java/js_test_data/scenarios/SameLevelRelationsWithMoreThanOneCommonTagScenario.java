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
import guru.bubl.test.module.utils.ModelTestScenarios;
import js_test_data.JsTestScenario;

import javax.inject.Inject;

public class SameLevelRelationsWithMoreThanOneCommonTagScenario implements JsTestScenario {

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
        userGraph = graphFactory.loadForUser(user);
        createVertices();
        createRelations();
        SubGraphPojo subGraphPojo = userGraph.aroundForkUriInShareLevels(
                center.uri(),
                ShareLevel.allShareLevelsInt
        );
        return SubGraphJson.toJson(subGraphPojo);
    }

    private void createVertices() {
        center = vertexFactory.createForOwner(
                user.username()
        );
        center.label("center");
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
        otherBubble = vertexFactory.createForOwner(
                user.username()
        );
        otherBubble.label("other bubble");
    }

    private void createRelations() {
        RelationOperator r1 = center.addRelationToFork(b1.uri(), ShareLevel.PRIVATE, ShareLevel.PRIVATE);
        r1.label("r1");
        r1.addTag(modelTestScenarios.creatorPredicate());
        r1.addTag(modelTestScenarios.possessionIdentification());
        RelationOperator r2 = center.addRelationToFork(b2.uri(), ShareLevel.PRIVATE, ShareLevel.PRIVATE);
        r2.label("r2");
        r2.addTag(modelTestScenarios.creatorPredicate());
        r2.addTag(modelTestScenarios.possessionIdentification());
        RelationOperator r3 = center.addRelationToFork(b3.uri(), ShareLevel.PRIVATE, ShareLevel.PRIVATE);
        r3.label("r3");
        r3.addTag(modelTestScenarios.creatorPredicate());
        center.addRelationToFork(otherBubble.uri(), ShareLevel.PRIVATE, ShareLevel.PRIVATE).label("other relation");
    }
}
