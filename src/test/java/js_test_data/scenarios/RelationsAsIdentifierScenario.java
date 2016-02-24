/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package js_test_data.scenarios;

import com.google.gson.Gson;
import guru.bubl.module.common_utils.NoExRun;
import guru.bubl.module.model.User;
import guru.bubl.module.model.graph.GraphFactory;
import guru.bubl.module.model.graph.Identification;
import guru.bubl.module.model.graph.IdentificationPojo;
import guru.bubl.module.model.graph.UserGraph;
import guru.bubl.module.model.graph.edge.EdgeOperator;
import guru.bubl.module.model.graph.vertex.VertexFactory;
import guru.bubl.module.model.graph.vertex.VertexInSubGraphOperator;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import guru.bubl.module.model.search.GraphSearch;
import guru.bubl.module.model.test.scenarios.TestScenarios;
import js_test_data.JsTestScenario;
import org.codehaus.jettison.json.JSONArray;

import javax.inject.Inject;

public class RelationsAsIdentifierScenario implements JsTestScenario {

    /*
    *
    * center-some_relation->b1
    * center-some relation->b2
    * center-some relation->b3
    * center-different relation ->b4
    *
    * all relations labeled "some relation" are identified to the first "some relation"
    */

    @Inject
    GraphSearch graphSearch;

    @Inject
    protected GraphFactory graphFactory;

    @Inject
    VertexFactory vertexFactory;

    VertexOperator
            center,
            b1,
            b2,
            b3,
            b4;

    User user = User.withEmailAndUsername("a", "b");

    UserGraph userGraph;

    @Override
    public JSONArray build() {
        userGraph = graphFactory.createForUser(user);
        createVertices();
        createRelations();
        return NoExRun.wrap(()-> new JSONArray(
                new Gson().toJson(
                        graphSearch.searchRelationsPropertiesSchemasForAutoCompletionByLabel(
                                "some",
                                user
                        )
                )
        )).get();
    }

    private void createVertices() {
        center = vertexFactory.createForOwnerUsername(
                user.username()
        );
        center.label("center");
        b1 = center.addVertexAndRelation().destinationVertex();
        b1.label("b1");
        b2 = center.addVertexAndRelation().destinationVertex();
        b2.label("b2");
        b3 = center.addVertexAndRelation().destinationVertex();
        b3.label("b3");
        b4 = center.addVertexAndRelation().destinationVertex();
        b4.label("b4");
    }

    private void createRelations() {
        EdgeOperator firstSomeRelation = center.addVertexAndRelation();
        firstSomeRelation.label("some relation");
        IdentificationPojo firstSomeRelationAsIdentifier = TestScenarios.identificationFromFriendlyResource(
                firstSomeRelation
        );
        EdgeOperator secondSomeRelation = center.addVertexAndRelation();
        secondSomeRelation.label("some relation");
        secondSomeRelation.addGenericIdentification(
                firstSomeRelationAsIdentifier
        );
        EdgeOperator thirdSomeRelation = center.addVertexAndRelation();
        thirdSomeRelation.label("some relation");
        thirdSomeRelation.addGenericIdentification(
                firstSomeRelationAsIdentifier
        );
        EdgeOperator differentRelation = center.addVertexAndRelation();
        differentRelation.label("different relation");
    }
}
