package js_test_data.scenarios;

import guru.bubl.module.model.User;
import guru.bubl.module.model.graph.GraphFactory;
import guru.bubl.module.model.graph.ShareLevel;
import guru.bubl.module.model.graph.subgraph.SubGraph;
import guru.bubl.module.model.graph.subgraph.SubGraphJson;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.graph.vertex.VertexFactory;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import js_test_data.JsTestScenario;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.inject.Inject;

public class DuplicateScenario implements JsTestScenario {

    /*
     center{
        -r1->a1{
            -r11->a11
            -r12->a12
            -r13->a2
        }
        -r2->a2{
            -r21->a21
            -r22->a22
            -r23->a23
            a1<-r13
        }
     }
     */

    private VertexOperator
            center,
            a1,
            a11,
            a12,
            a2,
            a21,
            a22,
            a23;

    @Inject
    protected GraphFactory graphFactory;

    @Inject
    protected VertexFactory vertexFactory;

    User owner = User.withEmailAndUsername("a", "b");

    @Override
    public JSONObject build() {
        createVertices();
        createEdges();
        UserGraph userGraph = graphFactory.loadForUser(owner);
        try {
            return new JSONObject().put(
                    "centerGraph",
                    SubGraphJson.toJson(
                            userGraph.aroundForkUriWithDepthInShareLevels(
                                    center.uri(),
                                    1,
                                    ShareLevel.allShareLevelsInt
                            )
                    )
            ).put(
                    "a1Graph",
                    SubGraphJson.toJson(
                            userGraph.aroundForkUriWithDepthInShareLevels(
                                    a1.uri(),
                                    1,
                                    ShareLevel.allShareLevelsInt
                            )
                    )
            ).put(
                    "a2Graph",
                    SubGraphJson.toJson(
                            userGraph.aroundForkUriWithDepthInShareLevels(
                                    a2.uri(),
                                    1,
                                    ShareLevel.allShareLevelsInt
                            )
                    )
            );
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void createVertices() {
        center = vertexFactory.createForOwner(owner.username());
        center.label("center");
        a1 = vertexFactory.createForOwner(owner.username());
        a1.label("a1");
        a11 = vertexFactory.createForOwner(owner.username());
        a11.label("a11");
        a12 = vertexFactory.createForOwner(owner.username());
        a12.label("a12");
        a2 = vertexFactory.createForOwner(owner.username());
        a2.label("a2");
        a21 = vertexFactory.createForOwner(owner.username());
        a21.label("a21");
        a22 = vertexFactory.createForOwner(owner.username());
        a22.label("a22");
        a23 = vertexFactory.createForOwner(owner.username());
        a23.label("a23");
    }

    private void createEdges() {
        center.addRelationToFork(a1.uri(), ShareLevel.PRIVATE, ShareLevel.PRIVATE).label("r1");
        a1.addRelationToFork(a11.uri(), ShareLevel.PRIVATE, ShareLevel.PRIVATE).label("r11");
        a1.addRelationToFork(a12.uri(), ShareLevel.PRIVATE, ShareLevel.PRIVATE).label("r12");
        center.addRelationToFork(a2.uri(), ShareLevel.PRIVATE, ShareLevel.PRIVATE).label("r2");
        a2.addRelationToFork(a21.uri(), ShareLevel.PRIVATE, ShareLevel.PRIVATE).label("r21");
        a2.addRelationToFork(a22.uri(), ShareLevel.PRIVATE, ShareLevel.PRIVATE).label("r22");
        a2.addRelationToFork(a23.uri(), ShareLevel.PRIVATE, ShareLevel.PRIVATE).label("r23");
        a1.addRelationToFork(a2.uri(), ShareLevel.PRIVATE, ShareLevel.PRIVATE).label("r13");
    }
}
