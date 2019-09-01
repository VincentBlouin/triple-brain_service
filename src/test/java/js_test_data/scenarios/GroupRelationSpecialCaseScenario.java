/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package js_test_data.scenarios;

import guru.bubl.module.model.User;
import guru.bubl.module.model.graph.GraphFactory;
import guru.bubl.module.model.graph.ShareLevel;
import guru.bubl.module.model.graph.SubGraphJson;
import guru.bubl.module.model.graph.edge.EdgeOperator;
import guru.bubl.module.model.graph.identification.IdentifierPojo;
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
        SubGraphPojo subGraph = userGraph.aroundVertexUriInShareLevels(
                center.uri(),
                ShareLevel.allShareLevels
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
        EdgeOperator r1 = center.addRelationToVertex(b1);
        r1.label("r1'");
        EdgeOperator r2 = center.addRelationToVertex(b2);
        r2.label("r2");
        IdentifierPojo r1Identifier = TestScenarios.identificationFromFriendlyResource(
                r1
        );
        r1Identifier.setLabel("r1");
        r2.addMeta(r1Identifier);
        IdentifierPojo r2Identifier = TestScenarios.identificationFromFriendlyResource(
                r2
        );
        r1.addMeta(r2Identifier);
        r1.addMeta(modelTestScenarios.computerScientistType());
    }
}
