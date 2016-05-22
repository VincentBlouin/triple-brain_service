/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package js_test_data.scenarios;

import guru.bubl.module.common_utils.NoExRun;
import guru.bubl.module.model.User;
import guru.bubl.module.model.graph.GraphFactory;
import guru.bubl.module.model.graph.subgraph.SubGraphPojo;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.graph.edge.EdgeOperator;
import guru.bubl.module.model.graph.vertex.VertexFactory;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import guru.bubl.module.model.json.graph.SubGraphJson;
import js_test_data.JsTestScenario;
import org.codehaus.jettison.json.JSONObject;
import org.joda.time.DateTime;

import javax.inject.Inject;

public class CreationDateScenario implements JsTestScenario {

    /*
    * b1-r1->b2
    * b1-r2->b3
    * b1-r3->b4
    * b1-r4->b5
    * b1-r5->b6
    * b1-r6->b7
    * b7-r71->b71
    * b7-r72->b72
    * b7-r73->b73
    * b7-r74->b74
    */

    @Inject
    GraphFactory graphFactory;

    @Inject
    VertexFactory vertexFactory;

    User user = User.withEmailAndUsername("a", "b");

    private VertexOperator
            b1,
            b2,
            b3,
            b4,
            b5,
            b6,
            b7,
            b71,
            b72,
            b73,
            b74;
    private SubGraphPojo
            subGraphForB1,
            subGraphForB7;


    @Override
    public Object build() {
        UserGraph userGraph = graphFactory.createForUser(user);
        createVertices();
        createEdges();
        subGraphForB1 = userGraph.graphWithDepthAndCenterVertexId(
                1,
                b1.uri()
        );
        setupcreationDatesForSubGraphForB1();
        subGraphForB7 = userGraph.graphWithDepthAndCenterVertexId(
                1,
                b7.uri()
        );
        setupcreationDatesForSubGraphForB7();
        return NoExRun.wrap(() -> new JSONObject().put(
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
        b3.comment("b3 comment");
        b4 = vertexFactory.createForOwnerUsername(
                user.username()
        );
        b4.label("b4");
        b5 = vertexFactory.createForOwnerUsername(
                user.username()
        );
        b5.label("b5");
        b6 = vertexFactory.createForOwnerUsername(
                user.username()
        );
        b6.label("b6");
        b7 = vertexFactory.createForOwnerUsername(
                user.username()
        );
        b7.label("b7");
        b71 = vertexFactory.createForOwnerUsername(
                user.username()
        );
        b71.label("b71");
        b72 = vertexFactory.createForOwnerUsername(
                user.username()
        );
        b72.label("b72");
        b73 = vertexFactory.createForOwnerUsername(
                user.username()
        );
        b73.label("b73");
        b74 = vertexFactory.createForOwnerUsername(
                user.username()
        );
        b74.label("b74");
    }

    private void createEdges() {
        EdgeOperator r1 = b1.addRelationToVertex(b2);
        r1.label("r1");
        EdgeOperator r2 = b1.addRelationToVertex(b3);
        r2.label("r2");
        EdgeOperator r3 = b1.addRelationToVertex(b4);
        r3.label("r3");
        EdgeOperator r4 = b1.addRelationToVertex(b5);
        r4.label("r4");
        EdgeOperator r5 = b1.addRelationToVertex(b6);
        r5.label("r5");
        EdgeOperator r6 = b1.addRelationToVertex(b7);
        r6.label("r6");
        EdgeOperator r71 = b7.addRelationToVertex(b71);
        r71.label("r71");
        EdgeOperator r72 = b7.addRelationToVertex(b72);
        r72.label("r72");
        EdgeOperator r73 = b7.addRelationToVertex(b73);
        r73.label("r73");
        EdgeOperator r74 = b7.addRelationToVertex(b74);
        r74.label("r74");
    }

    private void setupcreationDatesForSubGraphForB1() {
        DateTime b1CreationDate = new DateTime();
        subGraphForB1.vertexWithIdentifier(
                b1.uri()
        ).setCreationDate(b1CreationDate);
        subGraphForB1.vertexWithIdentifier(
                b2.uri()
        ).setCreationDate(b1CreationDate.plusDays(1));
        subGraphForB1.vertexWithIdentifier(
                b3.uri()
        ).setCreationDate(b1CreationDate.plusDays(2));
        subGraphForB1.vertexWithIdentifier(
                b4.uri()
        ).setCreationDate(b1CreationDate.plusDays(3));
        subGraphForB1.vertexWithIdentifier(
                b5.uri()
        ).setCreationDate(b1CreationDate.plusDays(4));
        subGraphForB1.vertexWithIdentifier(
                b6.uri()
        ).setCreationDate(b1CreationDate.plusDays(5));
        subGraphForB1.vertexWithIdentifier(
                b7.uri()
        ).setCreationDate(b1CreationDate.plusDays(6));
    }

    private void setupcreationDatesForSubGraphForB7() {
        DateTime b71CreationDate = new DateTime();
        subGraphForB7.vertexWithIdentifier(
                b71.uri()
        ).setCreationDate(b71CreationDate);
        subGraphForB7.vertexWithIdentifier(
                b72.uri()
        ).setCreationDate(b71CreationDate.plusDays(1));
        subGraphForB7.vertexWithIdentifier(
                b73.uri()
        ).setCreationDate(b71CreationDate.plusDays(3));
        subGraphForB7.vertexWithIdentifier(
                b74.uri()
        ).setCreationDate(b71CreationDate.plusDays(4));
    }
}


