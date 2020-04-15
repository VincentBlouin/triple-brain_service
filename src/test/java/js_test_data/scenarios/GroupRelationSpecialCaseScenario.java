/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package js_test_data.scenarios;

import guru.bubl.module.model.User;
import guru.bubl.module.model.graph.GraphFactory;
import guru.bubl.module.model.graph.ShareLevel;
import guru.bubl.module.model.graph.SubGraphJson;
import guru.bubl.module.model.graph.relation.RelationOperator;
import guru.bubl.module.model.graph.tag.TagPojo;
import guru.bubl.module.model.graph.subgraph.SubGraphPojo;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.graph.vertex.VertexFactory;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import guru.bubl.module.model.test.scenarios.TestScenarios;
import guru.bubl.test.module.utils.ModelTestScenarios;
import js_test_data.JsTestScenario;

import javax.inject.Inject;

public class GroupRelationSpecialCaseScenario implements JsTestScenario {
    /*
    center -r1'->b1
    r1' is identified to r2
    r1' has another identifier
    center-r2->b2
    r2 is identified to r1
    */


    @Inject
    protected GraphFactory graphFactory;

    @Inject
    protected VertexFactory vertexFactory;

    @Inject
    protected ModelTestScenarios modelTestScenarios;

    private VertexOperator
            center,
            b1,
            b2;


    User user = User.withEmailAndUsername("a", "b");

    @Override
    public Object build() {
        UserGraph userGraph = graphFactory.loadForUser(user);
        createVertices();
        createEdges();
        SubGraphPojo subGraph = userGraph.aroundForkUriInShareLevels(
                center.uri(),
                ShareLevel.allShareLevelsInt
        );
        return SubGraphJson.toJson(
                subGraph
        );
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
    }

    private void createEdges() {
        RelationOperator r1 = center.addRelationToVertex(b1);
        r1.label("r1'");
        RelationOperator r2 = center.addRelationToVertex(b2);
        r2.label("r2");
        TagPojo r1Identifier = TestScenarios.tagFromFriendlyResource(
                r1
        );
        r1Identifier.setLabel("r1");
        r2.addTag(r1Identifier);
        TagPojo r2Identifier = TestScenarios.tagFromFriendlyResource(
                r2
        );
        r1.addTag(r2Identifier);
        r1.addTag(modelTestScenarios.computerScientistType());
    }
}
