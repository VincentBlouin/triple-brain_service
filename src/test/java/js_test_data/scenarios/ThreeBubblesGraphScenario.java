/*
 * Copyright Vincent Blouin under the Mozilla Public License 1.1
 */

package js_test_data.scenarios;

import com.google.gson.Gson;
import js_test_data.JsTestScenario;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.triple_brain.module.model.User;
import org.triple_brain.module.model.graph.GraphFactory;
import org.triple_brain.module.model.graph.SubGraphPojo;
import org.triple_brain.module.model.graph.UserGraph;
import org.triple_brain.module.model.graph.edge.EdgeOperator;
import org.triple_brain.module.model.graph.vertex.VertexFactory;
import org.triple_brain.module.model.graph.vertex.VertexOperator;
import org.triple_brain.module.model.json.graph.SubGraphJson;
import org.triple_brain.module.search.GraphElementSearchResult;
import org.triple_brain.module.search.GraphSearch;
import org.triple_brain.module.search.VertexSearchResult;

import javax.inject.Inject;
import java.util.List;

public class ThreeBubblesGraphScenario implements JsTestScenario {

    /*
    * b1-r1->b2
    * b1-r2->b3
    * b2 has two hidden relations
    * b3 has two hidden relations
    */

    /*
    * b3<-r2-b1
    * b3-r3->-b4
    * b3-r4->b5
    * b4 has hidden relations
    */


    @Inject
    GraphFactory graphFactory;

    @Inject
    VertexFactory vertexFactory;

    @Inject
    GraphSearch graphSearch;

    User user = User.withEmailAndUsername("a", "b");

    private VertexOperator
            b1,
            b2,
            b3,
            b4,
            b5;

    @Override
    public JSONObject build() {

        try {
            UserGraph userGraph = graphFactory.createForUser(user);
            createVertices();
            createEdges();
            SubGraphPojo subGraphForB1 = userGraph.graphWithDepthAndCenterVertexId(
                    1,
                    b1.uri()
            );
            SubGraphPojo subGraphForB3 = userGraph.graphWithDepthAndCenterVertexId(
                    1,
                    b3.uri()
            );
            List<VertexSearchResult> searchResultsForB1 = graphSearch.searchSchemasOwnVerticesAndPublicOnesForAutoCompletionByLabel(
                    "b1",
                    user
            );
            List<GraphElementSearchResult> searchResultsForR2 = graphSearch.searchRelationsPropertiesOrSchemasForAutoCompletionByLabel(
                    "r2",
                    user
            );
            return new JSONObject().put(
                    "getGraph",
                    SubGraphJson.toJson(
                            subGraphForB1
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
                    "getSurroundBubble3Graph",
                    SubGraphJson.toJson(
                            subGraphForB3
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
        b3 = vertexFactory.createForOwnerUsername(
                user.username()
        );
        b3.label("b3");
        b4 = vertexFactory.createForOwnerUsername(
                user.username()
        );
        b4.label("b4");
        b5 = vertexFactory.createForOwnerUsername(
                user.username()
        );
        b5.label("b5");
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
    }
}
