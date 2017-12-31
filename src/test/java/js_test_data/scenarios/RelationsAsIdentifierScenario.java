/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package js_test_data.scenarios;

import com.google.gson.Gson;
import guru.bubl.module.common_utils.NoEx;
import guru.bubl.module.model.User;
import guru.bubl.module.model.graph.GraphFactory;
import guru.bubl.module.model.graph.identification.IdentifierPojo;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.graph.edge.EdgeOperator;
import guru.bubl.module.model.graph.vertex.VertexFactory;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import guru.bubl.module.model.graph.SubGraphJson;
import guru.bubl.module.model.search.GraphSearch;
import guru.bubl.module.model.test.scenarios.TestScenarios;
import js_test_data.JsTestScenario;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import javax.inject.Inject;

public class RelationsAsIdentifierScenario implements JsTestScenario {

    /*
    *
    * center-original some relation->b1
    * center-some relation->b2
    * center-some relation->b3
    * center-some different relation->b4
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
    public JSONObject build() {
        userGraph = graphFactory.loadForUser(user);
        userGraph.createVertex();
        createVertices();
        createRelations();

        return NoEx.wrap(() -> new JSONObject().put(
                        "searchSome",
                        new JSONArray(
                                new Gson().toJson(
                                        graphSearch.searchRelationsPropertiesSchemasForAutoCompletionByLabel(
                                                "some",
                                                user
                                        )
                                )
                        )
                ).put(
                        "graph",
                        SubGraphJson.toJson(
                                userGraph.graphWithDepthAndCenterBubbleUri(
                                        1,
                                        center.uri()
                                )
                        ))
        ).get();
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
        b4 = vertexFactory.createForOwner(
                user.username()
        );
        b4.label("b4");
    }

    private void createRelations() {
        EdgeOperator firstSomeRelation = center.addRelationToVertex(b1);
        firstSomeRelation.label("original some relation");
        IdentifierPojo firstSomeRelationAsIdentifier = TestScenarios.identificationFromFriendlyResource(
                firstSomeRelation
        );
        EdgeOperator secondSomeRelation = center.addRelationToVertex(b2);
        secondSomeRelation.label("some relation");
        secondSomeRelation.addMeta(
                firstSomeRelationAsIdentifier
        );
        EdgeOperator thirdSomeRelation = center.addRelationToVertex(b3);
        thirdSomeRelation.label("some relation");
        thirdSomeRelation.addMeta(
                firstSomeRelationAsIdentifier
        );
        EdgeOperator differentRelation = center.addRelationToVertex(b4);
        differentRelation.label("some different relation");
    }
}
