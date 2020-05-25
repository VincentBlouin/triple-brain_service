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

public class MergeCentersScenario implements JsTestScenario {
    /*
    oto 1{
        -ro1->oto 2
        -ro2->oto 3
    }
    urlu 1{
        -ru1->urlu 2
        -ru2->urlu 3
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
            oto1,
            oto2,
            oto3,
            urlu1,
            urlu2,
            urlu3;

    @Override
    public JSONObject build() {
        UserGraph userGraph = graphFactory.loadForUser(user);
        createVertices();
        createEdges();
        SubGraphPojo oto1Subgraph = userGraph.aroundForkUriInShareLevels(
                oto1.uri(),
                ShareLevel.allShareLevelsInt
        );
        SubGraphPojo urlu1Subgraph = userGraph.aroundForkUriInShareLevels(
                urlu1.uri(),
                ShareLevel.allShareLevelsInt
        );
        urlu1.mergeTo(oto1);
        SubGraphPojo oto1AfterMerge = userGraph.aroundForkUriInShareLevels(
                oto1.uri(),
                ShareLevel.allShareLevelsInt
        );
        try {
            return new JSONObject().put(
                    "oto1Subgraph",
                    SubGraphJson.toJson(
                            oto1Subgraph
                    )
            ).put(
                    "urlu1Subgraph",
                    SubGraphJson.toJson(
                            urlu1Subgraph
                    )
            ).put(
                    "oto1AfterMerge",
                    SubGraphJson.toJson(
                            oto1AfterMerge
                    )
            );
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void createVertices() {
        oto1 = vertexFactory.createForOwner(
                user.username()
        );
        oto1.label("oto 1");
        oto2 = vertexFactory.createForOwner(
                user.username()
        );
        oto2.label("oto 2");
        oto3 = vertexFactory.createForOwner(
                user.username()
        );
        oto3.label("oto 3");
        urlu1 = vertexFactory.createForOwner(
                user.username()
        );
        urlu1.label("urlu 1");
        urlu2 = vertexFactory.createForOwner(
                user.username()
        );
        urlu2.label("urlu 2");
        urlu3 = vertexFactory.createForOwner(
                user.username()
        );
        urlu3.label("urlu 3");
    }

    private void createEdges() {
        oto1.addRelationToFork(oto2.uri(), ShareLevel.PRIVATE, ShareLevel.PRIVATE).label("ro1");
        oto1.addRelationToFork(oto3.uri(), ShareLevel.PRIVATE, ShareLevel.PRIVATE).label("ro2");

        urlu1.addRelationToFork(urlu2.uri(), ShareLevel.PRIVATE, ShareLevel.PRIVATE).label("urlu 2");
        urlu1.addRelationToFork(urlu3.uri(), ShareLevel.PRIVATE, ShareLevel.PRIVATE).label("urlu 3");
    }
}
