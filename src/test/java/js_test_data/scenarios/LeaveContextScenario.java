package js_test_data.scenarios;

import guru.bubl.module.model.User;
import guru.bubl.module.model.graph.GraphFactory;
import guru.bubl.module.model.graph.ShareLevel;
import guru.bubl.module.model.graph.SubGraphJson;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.graph.vertex.VertexFactory;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import guru.bubl.test.module.utils.ModelTestScenarios;
import js_test_data.JsTestScenario;
import org.codehaus.jettison.json.JSONObject;

import javax.inject.Inject;

public class LeaveContextScenario implements JsTestScenario {

    /*
     *
     * tech choice -> choice a
     * tech choice -> choice b
     * tech choice -> choice c
     * choice a -> choice a1
     * choice a -> choice a2
     * */

    @Inject
    protected GraphFactory graphFactory;

    @Inject
    protected VertexFactory vertexFactory;

    @Inject
    ModelTestScenarios modelTestScenarios;

    User user = User.withEmailAndUsername("a", "b");

    private VertexOperator
            techChoice,
            choiceA,
            choiceB,
            choiceC,
            choiceA1,
            choiceA2;

    @Override
    public Object build() {
        UserGraph userGraph = graphFactory.loadForUser(user);
        create();
        try {
            return new JSONObject().put(
                    "techChoiceCenter",
                    SubGraphJson.toJson(
                            userGraph.aroundForkUriInShareLevels(
                                    techChoice.uri(),
                                    ShareLevel.allShareLevelsInt
                            )
                    )
            ).put(
                    "choiceACenter",
                    SubGraphJson.toJson(
                            userGraph.aroundForkUriInShareLevels(
                                    choiceA.uri(),
                                    ShareLevel.allShareLevelsInt
                            )
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void create() {
        techChoice = vertexFactory.createForOwner(user.username());
        techChoice.label("tech choice");

        choiceA = vertexFactory.createForOwner(user.username());
        choiceA.label("choice a");
        techChoice.addRelationToVertex(choiceA);

        choiceB = vertexFactory.createForOwner(user.username());
        choiceB.label("choice b");
        techChoice.addRelationToVertex(choiceB);

        choiceC = vertexFactory.createForOwner(user.username());
        choiceC.label("choice c");
        techChoice.addRelationToVertex(choiceC);

        choiceA1 = vertexFactory.createForOwner(user.username());
        choiceA1.label("choice a1");
        choiceA.addRelationToVertex(choiceA1);

        choiceA2 = vertexFactory.createForOwner(user.username());
        choiceA2.label("choice a2");
        choiceA.addRelationToVertex(choiceA2);
    }


}
