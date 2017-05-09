/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package js_test_data.scenarios;

import guru.bubl.module.model.User;
import guru.bubl.module.model.graph.GraphFactory;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.graph.edge.EdgeOperator;
import guru.bubl.module.model.graph.vertex.VertexFactory;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import guru.bubl.module.model.graph.SubGraphJson;
import guru.bubl.test.module.utils.ModelTestScenarios;
import js_test_data.JsTestScenario;

import javax.inject.Inject;

public class RelationWithMultipleIdentifiers implements JsTestScenario {

    /*
    * Team - computer scientist -> Aria Sauley
    * the relation "computer scientist" has two identifiers with different labels
    *
    * Team to build a complex application - member -> John Foo
    *
    * the relation member has two identifiers with the same label
    */


    @Inject
    protected GraphFactory graphFactory;

    @Inject
    protected VertexFactory vertexFactory;

    @Inject
    ModelTestScenarios modelTestScenarios;

    User user = User.withEmailAndUsername("a", "b");

    private VertexOperator team,
            john,
            aria;

    @Override
    public Object build() {
        UserGraph userGraph = graphFactory.createForUser(user);
        buildBubbles();
        buildRelations();
        return SubGraphJson.toJson(
                userGraph.graphWithDepthAndCenterBubbleUri(
                        1,
                        team.uri()
                )
        );
    }

    private void buildBubbles(){
        team = vertexFactory.createForOwnerUsername(
                user.username()
        );
        team.label("Team");

        john = vertexFactory.createForOwnerUsername(
                user.username()
        );
        john.label("John Foo");

        aria = vertexFactory.createForOwnerUsername(
                user.username()
        );
        aria.label("Aria Sauley");
    }
    private void buildRelations(){
        EdgeOperator scientistRelation = team.addRelationToVertex(aria);
        scientistRelation.label("computer scientist");
        scientistRelation.addMeta(
                modelTestScenarios.computerScientistType()
        );
        scientistRelation.addMeta(
                modelTestScenarios.person()
        );

        EdgeOperator memberRelation = team.addRelationToVertex(john);
        memberRelation.label("member");
        memberRelation.addMeta(
                modelTestScenarios.person()
        );
        memberRelation.addMeta(
                modelTestScenarios.personFromFreebase()
        );
    }

}
