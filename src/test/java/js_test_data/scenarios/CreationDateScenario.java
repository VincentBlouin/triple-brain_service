/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package js_test_data.scenarios;

import guru.bubl.module.common_utils.NoEx;
import guru.bubl.module.model.User;
import guru.bubl.module.model.graph.GraphFactory;
import guru.bubl.module.model.graph.ShareLevel;
import guru.bubl.module.model.graph.subgraph.SubGraphJson;
import guru.bubl.module.model.graph.relation.RelationOperator;
import guru.bubl.module.model.graph.subgraph.SubGraphPojo;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.graph.vertex.VertexFactory;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import guru.bubl.test.module.utils.ModelTestScenarios;
import js_test_data.JsTestScenario;
import org.codehaus.jettison.json.JSONObject;
import org.joda.time.DateTime;

import javax.inject.Inject;

public class CreationDateScenario implements JsTestScenario {

    /*
     * b1-r1->b2
     * b1-r2->b3
     * b1-r3->b4
     * b1-similar->s1
     * b1-similar->s2
     * b1-similar->s3
     * similar have same identifier
     * b1-r4->b5
     * b1-r5->b6
     * b1-r6->b7
     * b7-r71->b71
     * b7-r72->b72
     * b7-r73->b73
     * b7-r74->b74
     * b1-r8->b8
     * b1-r9->b9
     * b1-r10->b10
     */

    @Inject
    GraphFactory graphFactory;

    @Inject
    VertexFactory vertexFactory;

    @Inject
    ModelTestScenarios modelTestScenarios;

    User user = User.withEmailAndUsername("f", "g");

    private VertexOperator
            b1,
            b2,
            b3,
            b4,
            s1,
            s2,
            s3,
            b5,
            b6,
            b7,
            b71,
            b72,
            b73,
            b74,
            b8,
            b9,
            b10;
    private SubGraphPojo
            subGraphForB1,
            subGraphForB7;


    @Override
    public Object build() {
        UserGraph userGraph = graphFactory.loadForUser(user);
        createVertices();
        createEdges();
        subGraphForB1 = userGraph.aroundForkUriInShareLevels(
                b1.uri(),
                ShareLevel.allShareLevelsInt
        );
        setupcreationDatesForSubGraphForB1();
        subGraphForB7 = userGraph.aroundForkUriInShareLevels(
                b7.uri(),
                ShareLevel.allShareLevelsInt
        );
        setupcreationDatesForSubGraphForB7();
        return NoEx.wrap(() -> new JSONObject().put(
                "surroundBubble1Graph",
                SubGraphJson.toJson(
                        subGraphForB1
                )
        ).put(
                "surroundBubble7Graph",
                SubGraphJson.toJson(
                        subGraphForB7
                )
        )).get();
    }

    private void createVertices() {
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
        b3.comment("b3 comment");
        b4 = vertexFactory.createForOwner(
                user.username()
        );
        b4.label("b4");
        b5 = vertexFactory.createForOwner(
                user.username()
        );
        b5.label("b5");
        b6 = vertexFactory.createForOwner(
                user.username()
        );
        b6.label("b6");
        b7 = vertexFactory.createForOwner(
                user.username()
        );
        b7.label("b7");
        b71 = vertexFactory.createForOwner(
                user.username()
        );
        b71.label("b71");
        b72 = vertexFactory.createForOwner(
                user.username()
        );
        b72.label("b72");
        b73 = vertexFactory.createForOwner(
                user.username()
        );
        b73.label("b73");
        b74 = vertexFactory.createForOwner(
                user.username()
        );
        b74.label("b74");
        b8 = vertexFactory.createForOwner(
                user.username()
        );
        b8.label("8");
        b9 = vertexFactory.createForOwner(
                user.username()
        );
        b9.label("9");
        b10 = vertexFactory.createForOwner(
                user.username()
        );
        b10.label("10");
        s1 = vertexFactory.createForOwner(
                user.username()
        );
        s1.label("s1");
        s2 = vertexFactory.createForOwner(
                user.username()
        );
        s2.label("s2");
        s3 = vertexFactory.createForOwner(
                user.username()
        );
        s3.label("s3");
    }

    private void createEdges() {
        RelationOperator r1 = b1.addRelationToFork(b2);
        r1.label("r1");
        RelationOperator r2 = b1.addRelationToFork(b3);
        r2.label("r2");
        RelationOperator r3 = b1.addRelationToFork(b4);
        r3.label("r3");
        RelationOperator r4 = b1.addRelationToFork(b5);
        r4.label("r4");
        RelationOperator similar1 = b1.addRelationToFork(s1);
        similar1.label("similar");
        similar1.addTag(modelTestScenarios.toDo());
        RelationOperator similar2 = b1.addRelationToFork(s2);
        similar2.label("similar");
        similar2.addTag(modelTestScenarios.toDo());
        RelationOperator similar3 = b1.addRelationToFork(s3);
        similar3.label("similar");
        similar3.addTag(modelTestScenarios.toDo());
        RelationOperator r5 = b1.addRelationToFork(b6);
        r5.label("r5");
        RelationOperator r6 = b1.addRelationToFork(b7);
        r6.label("r6");
        RelationOperator r71 = b7.addRelationToFork(b71);
        r71.label("r71");
        RelationOperator r72 = b7.addRelationToFork(b72);
        r72.label("r72");
        RelationOperator r73 = b7.addRelationToFork(b73);
        r73.label("r73");
        RelationOperator r74 = b7.addRelationToFork(b74);
        r74.label("r74");
        RelationOperator r8 = b1.addRelationToFork(b8);
        r8.label("r8");
        RelationOperator r9 = b1.addRelationToFork(b9);
        r9.label("r9");
        RelationOperator r10 = b1.addRelationToFork(b10);
        r10.label("r10");
    }

    private void setupcreationDatesForSubGraphForB1() {
        DateTime b1CreationDate = new DateTime().withDayOfMonth(
                27
        ).withMonthOfYear(
                6
        );
        subGraphForB1.vertexWithIdentifier(
                b1.uri()
        ).setCreationDate(b1CreationDate.toDate().getTime());
        subGraphForB1.vertexWithIdentifier(
                b2.uri()
        ).setCreationDate(b1CreationDate.plusDays(1).toDate().getTime());
        subGraphForB1.vertexWithIdentifier(
                b3.uri()
        ).setCreationDate(b1CreationDate.plusDays(2).toDate().getTime());
        subGraphForB1.vertexWithIdentifier(
                b4.uri()
        ).setCreationDate(b1CreationDate.plusDays(3).toDate().getTime());
        subGraphForB1.vertexWithIdentifier(
                s1.uri()
        ).setCreationDate(b1CreationDate.plusDays(4).toDate().getTime());
        subGraphForB1.vertexWithIdentifier(
                b5.uri()
        ).setCreationDate(b1CreationDate.plusDays(5).toDate().getTime());
        subGraphForB1.vertexWithIdentifier(
                b6.uri()
        ).setCreationDate(b1CreationDate.plusDays(6).toDate().getTime());
        subGraphForB1.vertexWithIdentifier(
                b7.uri()
        ).setCreationDate(b1CreationDate.plusDays(7).toDate().getTime());
        subGraphForB1.vertexWithIdentifier(
                b8.uri()
        ).setCreationDate(b1CreationDate.plusDays(8).toDate().getTime());
        subGraphForB1.vertexWithIdentifier(
                b9.uri()
        ).setCreationDate(b1CreationDate.plusDays(9).toDate().getTime());
        subGraphForB1.vertexWithIdentifier(
                b10.uri()
        ).setCreationDate(b1CreationDate.plusDays(10).toDate().getTime());
        subGraphForB1.vertexWithIdentifier(
                s2.uri()
        ).setCreationDate(b1CreationDate.plusDays(11).toDate().getTime());
        subGraphForB1.vertexWithIdentifier(
                s3.uri()
        ).setCreationDate(b1CreationDate.plusDays(12).toDate().getTime());
    }

    private void setupcreationDatesForSubGraphForB7() {
        DateTime b71CreationDate = new DateTime();
        subGraphForB7.vertexWithIdentifier(
                b71.uri()
        ).setCreationDate(b71CreationDate.toDate().getTime());
        subGraphForB7.vertexWithIdentifier(
                b72.uri()
        ).setCreationDate(b71CreationDate.plusDays(1).toDate().getTime());
        subGraphForB7.vertexWithIdentifier(
                b73.uri()
        ).setCreationDate(b71CreationDate.plusDays(3).toDate().getTime());
        subGraphForB7.vertexWithIdentifier(
                b74.uri()
        ).setCreationDate(b71CreationDate.plusDays(4).toDate().getTime());
    }
}


