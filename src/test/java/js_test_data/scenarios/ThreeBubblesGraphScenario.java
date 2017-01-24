/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package js_test_data.scenarios;

import com.google.gson.Gson;
import guru.bubl.module.model.IdentifiedTo;
import guru.bubl.module.model.graph.subgraph.*;
import guru.bubl.module.model.json.JsonUtils;
import guru.bubl.module.model.search.GraphElementSearchResult;
import guru.bubl.module.model.search.GraphSearch;
import guru.bubl.module.model.test.scenarios.TestScenarios;
import guru.bubl.test.module.utils.ModelTestScenarios;
import js_test_data.JsTestScenario;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import guru.bubl.module.model.User;
import guru.bubl.module.model.graph.GraphFactory;
import guru.bubl.module.model.graph.edge.EdgeOperator;
import guru.bubl.module.model.graph.vertex.VertexFactory;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import guru.bubl.module.model.json.graph.SubGraphJson;

import javax.inject.Inject;
import java.util.List;

public class ThreeBubblesGraphScenario implements JsTestScenario {

    /*
    * b1-r1->b2
    * b1-r2->b3
    * b2 has two hidden relations
    * b2 child are private
    * b3 has two hidden relations
    * b3 has the comment "b3 comment"
    */

    /*
    * b3<-r2-b1
    * b3-r3->-b4
    * b4 is public
    * b3-r4->b5
    * b5 is public
    * b4 has hidden relations
    */

    /*
    Also a fork of subgraph b1 b2 and b3
    b1 fork is identified to "Event" and has 2 suggestions
    */

    /*
    Also single child scenario
    parent-relation->child
    graph where child is connected to parent.
     */

//    username has an accent


    @Inject
    GraphFactory graphFactory;

    @Inject
    VertexFactory vertexFactory;

    @Inject
    GraphSearch graphSearch;

    @Inject
    SubGraphForkerFactory subGraphForkerFactory;

    @Inject
    IdentifiedTo identifiedTo;

    @Inject
    ModelTestScenarios modelTestScenarios;

    User user = User.withEmailAndUsername(
            "a",
            "églantier"
    );

    User forkerUser = User.withEmailAndUsername("forker@example.com", "forker");

    private VertexOperator
            b1,
            b2,
            b3,
            b4,
            b5,
            parent,
            child;

    private List<GraphElementSearchResult> forkedB1SearchResults;

    @Override
    public JSONObject build() {
        UserGraph userGraph = graphFactory.createForUser(user);
        createVertices();
        createEdges();
        SubGraphPojo subGraphForB1 = userGraph.graphWithDepthAndCenterVertexId(
                1,
                b1.uri()
        );
        SubGraphPojo subGraphForB2 = userGraph.graphWithDepthAndCenterVertexId(
                1,
                b2.uri()
        );
        SubGraphPojo subGraphForB3 = userGraph.graphWithDepthAndCenterVertexId(
                1,
                b3.uri()
        );
        List<GraphElementSearchResult> searchResultsForB1 = graphSearch.searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                "b1",
                user
        );
        List<GraphElementSearchResult> searchResultsForR2 = graphSearch.searchRelationsPropertiesSchemasForAutoCompletionByLabel(
                "r2",
                user
        );
        SubGraphPojo subGraphForParent = userGraph.graphWithDepthAndCenterVertexId(
                1,
                parent.uri()
        );
        parent.addRelationToVertex(b1);
        SubGraphPojo subGraphOfB1RelatedToParent = userGraph.graphWithDepthAndCenterVertexId(
                1,
                b1.uri()
        );
        try {
            return new JSONObject().put(
                    "getGraph",
                    SubGraphJson.toJson(
                            subGraphForB1
                    )
            ).put(
                    "forkedGraph",
                    SubGraphJson.toJson(
                            buildForkSubGraph(
                                    subGraphForB1
                            )
                    )
            ).put(
                    "searchResultsForB1",
                    new JSONArray(
                            new Gson().toJson(searchResultsForB1)
                    )
            ).put(
                    "searchResultsForR2",
                    new JSONArray(
                            new Gson().toJson(searchResultsForR2)
                    )
            ).put(
                    "subGraphForB2",
                    SubGraphJson.toJson(
                            subGraphForB2
                    )
            ).put(
                    "subGraphForB3",
                    SubGraphJson.toJson(
                            subGraphForB3
                    )
            ).put(
                    "subGraphForParent",
                    SubGraphJson.toJson(
                            subGraphForParent
                    )
            ).put(
                    "subGraphOfB1RelatedToParent",
                    SubGraphJson.toJson(
                            subGraphOfB1RelatedToParent
                    )
            ).put(
                    "forkedB1SearchResults",
                    JsonUtils.getGson().toJson(
                            forkedB1SearchResults
                    )
            );
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
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
        b2.addVertexAndRelation();
        b2.addVertexAndRelation();
        b3 = vertexFactory.createForOwnerUsername(
                user.username()
        );
        b3.label("b3");
        b3.comment("b3 comment");
        b4 = vertexFactory.createForOwnerUsername(
                user.username()
        );
        b4.label("b4");
        b4.makePublic();
        b5 = vertexFactory.createForOwnerUsername(
                user.username()
        );
        b5.makePublic();
        b5.label("b5");
        parent = vertexFactory.createForOwnerUsername(
                user.username()
        );
        parent.label("parent");
        child = vertexFactory.createForOwnerUsername(
                user.username()
        );
        child.label("child");
    }

    private void createEdges() {
        EdgeOperator r1 = b1.addRelationToVertex(b2);
        r1.label("r1");
        EdgeOperator r2 = b1.addRelationToVertex(b3);
        r2.label("r2");
        EdgeOperator r3 = b3.addRelationToVertex(b4);
        r3.label("r3");
        EdgeOperator r4 = b3.addRelationToVertex(b5);
        r4.label("r4");
        b4.addVertexAndRelation();
        b4.addVertexAndRelation();
        EdgeOperator relation = parent.addRelationToVertex(child);
        relation.label("relation");
    }

    private SubGraphPojo buildForkSubGraph(SubGraph subGraphForB1) {
        b1.makePublic();
        b2.makePublic();
        b3.makePublic();
        UserGraph forkerUserGraph = graphFactory.createForUser(forkerUser);
        subGraphForkerFactory.forUser(
                forkerUser
        ).fork(
                subGraphForB1
        );
        GraphElementSearchResult forkedB1AsIdentifier = identifiedTo.getForIdentificationAndUser(
                TestScenarios.identificationFromFriendlyResource(b1),
                forkerUser
        ).iterator().next();
        forkedB1SearchResults = graphSearch.searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                "b1",
                forkerUser
        );
        VertexOperator forkedB1 = vertexFactory.withUri(
                forkedB1AsIdentifier.getGraphElement().uri()
        );
        forkedB1.addGenericIdentification(
                modelTestScenarios.event()
        );
        forkedB1.addSuggestions(
                modelTestScenarios.suggestionsToMap(
                        modelTestScenarios.peopleInvolvedSuggestionFromEventIdentification(user),
                        modelTestScenarios.startDateSuggestionFromEventIdentification(user)
                )
        );
        return forkerUserGraph.graphWithDepthAndCenterVertexId(
                1,
                forkedB1.uri()
        );
    }
}
