/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package js_test_data.scenarios;

import com.google.gson.Gson;
import guru.bubl.module.model.User;
import guru.bubl.module.model.admin.WholeGraphAdmin;
import guru.bubl.module.model.graph.GraphFactory;
import guru.bubl.module.model.graph.ShareLevel;
import guru.bubl.module.model.graph.SubGraphJson;
import guru.bubl.module.model.graph.edge.EdgeOperator;
import guru.bubl.module.model.graph.subgraph.*;
import guru.bubl.module.model.graph.vertex.VertexFactory;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import guru.bubl.module.model.json.JsonUtils;
import guru.bubl.module.model.search.GraphElementSearchResult;
import guru.bubl.module.model.search.GraphSearchFactory;
import guru.bubl.test.module.utils.ModelTestScenarios;
import js_test_data.JsTestScenario;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.inject.Inject;
import java.net.URI;
import java.util.List;
import java.util.Map;


public class MergeTwoChildHavingChildrenScenario implements JsTestScenario {

    /*
     * center1-r1->b2
     * b2-r21->b2.1
     * b2-r22->b2.2
     */

    /*
     center2-or1->o2
     o2-or21->o2.1
     o2-or22->o2.2
     */

    @Inject
    GraphFactory graphFactory;

    @Inject
    VertexFactory vertexFactory;

    User user = User.withEmailAndUsername(
            "a",
            "églantier"
    );

    private VertexOperator
            center1,
            b2,
            b21,
            b22,
            center2,
            o2,
            o21,
            o22;

    @Override
    public JSONObject build() {
        UserGraph userGraph = graphFactory.loadForUser(user);
        createVertices();
        createEdges();
        SubGraphPojo center1SubGraph = userGraph.aroundVertexUriInShareLevels(
                center1.uri(),
                ShareLevel.allShareLevelsInt
        );
        SubGraphPojo center2SubGraph = userGraph.aroundVertexUriInShareLevels(
                center2.uri(),
                ShareLevel.allShareLevelsInt
        );
        SubGraphPojo b2SubGraph = userGraph.aroundVertexUriInShareLevels(
                b2.uri(),
                ShareLevel.allShareLevelsInt
        );
        o2.mergeTo(b2);
        SubGraphPojo b2SubGraphAfterMerge = userGraph.aroundVertexUriInShareLevels(
                b2.uri(),
                ShareLevel.allShareLevelsInt
        );
        try {
            return new JSONObject().put(
                    "center1SubGraph",
                    SubGraphJson.toJson(
                            center1SubGraph
                    )
            ).put(
                    "center2SubGraph",
                    SubGraphJson.toJson(
                            center2SubGraph
                    )
            ).put(
                    "b2SubGraph",
                    SubGraphJson.toJson(
                            b2SubGraph
                    )
            ).put(
                    "b2SubGraphAfterMerge",
                    SubGraphJson.toJson(
                            b2SubGraphAfterMerge
                    )
            );
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void createVertices() {
        center1 = vertexFactory.createForOwner(
                user.username()
        );
        center1.label("center1");
        b2 = vertexFactory.createForOwner(
                user.username()
        );
        b2.label("b2");
        b21 = vertexFactory.createForOwner(
                user.username()
        );
        b21.label("b21");
        b22 = vertexFactory.createForOwner(
                user.username()
        );
        b22.label("b22");
        center2 = vertexFactory.createForOwner(
                user.username()
        );
        center2.label("center2");
        o2 = vertexFactory.createForOwner(
                user.username()
        );
        o2.label("o2");
        o21 = vertexFactory.createForOwner(
                user.username()
        );
        o21.label("o21");
        o22 = vertexFactory.createForOwner(
                user.username()
        );
        o22.label("o22");

    }

    private void createEdges() {
        center1.addRelationToVertex(b2).label("r1");
        b2.addRelationToVertex(b21).label("r21");
        b2.addRelationToVertex(b22).label("r22");

        center2.addRelationToVertex(o2).label("or1");
        o2.addRelationToVertex(o21).label("or21");
        o2.addRelationToVertex(o22).label("or22");
    }
}
