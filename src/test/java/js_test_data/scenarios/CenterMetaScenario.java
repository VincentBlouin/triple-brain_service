/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package js_test_data.scenarios;

import guru.bubl.module.common_utils.NoExRun;
import guru.bubl.module.model.User;
import guru.bubl.module.model.graph.GraphFactory;
import guru.bubl.module.model.graph.edge.EdgeOperator;
import guru.bubl.module.model.graph.identification.Identifier;
import guru.bubl.module.model.graph.subgraph.SubGraphPojo;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.graph.vertex.VertexFactory;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import guru.bubl.module.model.graph.SubGraphJson;
import guru.bubl.test.module.utils.ModelTestScenarios;
import js_test_data.JsTestScenario;
import org.codehaus.jettison.json.JSONObject;

import javax.inject.Inject;

public class CenterMetaScenario implements JsTestScenario {

    /*
     * event->e1
     * event->e2
     * event is an identifier
     *
     * e2 has 2 hidden relations
     *
     * e1-r1->e2
     * e1-r2->e3
     * o2-f1->o4
     * r1,r2,f1 are tagged to "to do"
     * "to do" is a tag
     * e4 has one hidden vertex
     */

    @Inject
    GraphFactory graphFactory;

    @Inject
    VertexFactory vertexFactory;

    @Inject
    ModelTestScenarios modelTestScenarios;

    Identifier toDo;
    Identifier event;

    User user = User.withEmailAndUsername("f", "g");

    private VertexOperator
            e1,
            e2,
            e3,
            o1,
            o2;

    @Override
    public Object build() {
        UserGraph userGraph = graphFactory.loadForUser(user);
        createVertices();
        createEdges();
        SubGraphPojo aroundTodo = userGraph.graphWithDepthAndCenterBubbleUri(
                2,
                toDo.uri()
        );
        SubGraphPojo aroundEvent = userGraph.graphWithDepthAndCenterBubbleUri(
                2,
                event.uri()
        );
        return NoExRun.wrap(() -> new JSONObject().put(
                "aroundEvent",
                SubGraphJson.toJson(
                        aroundEvent
                )
        ).put(
                "aroundTodo",
                SubGraphJson.toJson(
                        aroundTodo
                )
        )).get();
    }

    private void createVertices() {
        e1 = vertexFactory.createForOwnerUsername(
                user.username()
        );
        event = e1.addMeta(
                modelTestScenarios.event()
        ).values().iterator().next();
        e1.label("e1");
        e2 = vertexFactory.createForOwnerUsername(
                user.username()
        );
        e2.label("e2");
        e2.addMeta(
                event
        );
        e2.addVertexAndRelation();
        e3 = vertexFactory.createForOwnerUsername(
                user.username()
        );
        e3.label("e3");
        o1 = vertexFactory.createForOwnerUsername(
                user.username()
        );
        o1.label("o1");
        o2 = vertexFactory.createForOwnerUsername(
                user.username()
        );
        o2.label("o2");
    }

    private void createEdges() {
        EdgeOperator r1 = e1.addRelationToVertex(e2);
        r1.label("r1");
        toDo = r1.addMeta(
                modelTestScenarios.toDo()
        ).values().iterator().next();
        EdgeOperator r2 = e1.addRelationToVertex(e3);
        r2.label("r2");
        r2.addMeta(toDo);
        EdgeOperator f1 = o1.addRelationToVertex(o2);
        f1.label("f1");
        f1.addMeta(toDo);
    }
}
