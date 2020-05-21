package js_test_data.scenarios;

import guru.bubl.module.model.User;
import guru.bubl.module.model.graph.GraphFactory;
import guru.bubl.module.model.graph.ShareLevel;
import guru.bubl.module.model.graph.subgraph.SubGraphJson;
import guru.bubl.module.model.graph.subgraph.SubGraphPojo;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.graph.vertex.VertexFactory;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import js_test_data.JsTestScenario;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.inject.Inject;

public class MergeAncestorWithDescendantScenario implements JsTestScenario {
    /*
    center{
        -ra1->a1{
            -ra2->a2{
                -ra21->a21{
                    -ra211->a211,
                    -ra212->a212
                }
                -ra22->a22
                -ra23->a23
            }
        }
        -ra4->a4
     }
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
            center,
            a1,
            a4,
            a2,
            a3,
            a22,
            a23,
            a21,
            a211,
            a212;

    @Override
    public JSONObject build() {
        UserGraph userGraph = graphFactory.loadForUser(user);
        createVertices();
        createEdges();
        SubGraphPojo centerSubGraph = userGraph.aroundForkUriInShareLevels(
                center.uri(),
                ShareLevel.allShareLevelsInt
        );
        SubGraphPojo a1SubGraph = userGraph.aroundForkUriInShareLevels(
                a1.uri(),
                ShareLevel.allShareLevelsInt
        );
        SubGraphPojo a2SubGraph = userGraph.aroundForkUriInShareLevels(
                a2.uri(),
                ShareLevel.allShareLevelsInt
        );
        SubGraphPojo a21SubGraph = userGraph.aroundForkUriInShareLevels(
                a21.uri(),
                ShareLevel.allShareLevelsInt
        );
        a1.mergeTo(a21);
        SubGraphPojo a21SubGraphAfterMerge = userGraph.aroundForkUriInShareLevels(
                a21.uri(),
                ShareLevel.allShareLevelsInt
        );
        try {
            return new JSONObject().put(
                    "centerSubGraph",
                    SubGraphJson.toJson(
                            centerSubGraph
                    )
            ).put(
                    "a1SubGraph",
                    SubGraphJson.toJson(
                            a1SubGraph
                    )
            ).put(
                    "a2SubGraph",
                    SubGraphJson.toJson(
                            a2SubGraph
                    )
            ).put(
                    "a21SubGraph",
                    SubGraphJson.toJson(
                            a21SubGraph
                    )
            ).put(
                    "a21SubGraphAfterMerge",
                    SubGraphJson.toJson(
                            a21SubGraphAfterMerge
                    )
            );
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void createVertices() {
        center = vertexFactory.createForOwner(
                user.username()
        );
        center.label("center");
        a1 = vertexFactory.createForOwner(
                user.username()
        );
        a1.label("a1");
        a4 = vertexFactory.createForOwner(
                user.username()
        );
        a4.label("a4");
        a2 = vertexFactory.createForOwner(
                user.username()
        );
        a2.label("a2");
        a21 = vertexFactory.createForOwner(
                user.username()
        );
        a21.label("a21");
        a22 = vertexFactory.createForOwner(
                user.username()
        );
        a22.label("a22");
        a23 = vertexFactory.createForOwner(
                user.username()
        );
        a23.label("a23");
        a211 = vertexFactory.createForOwner(
                user.username()
        );
        a211.label("a211");
        a212 = vertexFactory.createForOwner(
                user.username()
        );
        a212.label("a212");
    }

    private void createEdges() {
        center.addRelationToFork(a1.uri(), ShareLevel.PRIVATE, ShareLevel.PRIVATE).label("ra1");
        center.addRelationToFork(a4.uri(), ShareLevel.PRIVATE, ShareLevel.PRIVATE).label("ra4");
        a1.addRelationToFork(a2.uri(), ShareLevel.PRIVATE, ShareLevel.PRIVATE).label("ra2");

        a2.addRelationToFork(a21.uri(), ShareLevel.PRIVATE, ShareLevel.PRIVATE).label("ra21");
        a21.addRelationToFork(a211.uri(), ShareLevel.PRIVATE, ShareLevel.PRIVATE).label("ra211");
        a21.addRelationToFork(a212.uri(), ShareLevel.PRIVATE, ShareLevel.PRIVATE).label("ra212");

        a2.addRelationToFork(a22.uri(), ShareLevel.PRIVATE, ShareLevel.PRIVATE).label("ra22");
        a2.addRelationToFork(a23.uri(), ShareLevel.PRIVATE, ShareLevel.PRIVATE).label("ra23");
    }
}
