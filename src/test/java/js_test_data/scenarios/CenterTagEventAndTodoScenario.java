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
import guru.bubl.module.model.graph.tag.Tag;
import guru.bubl.module.model.graph.subgraph.SubGraphPojo;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.graph.vertex.VertexFactory;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import guru.bubl.test.module.utils.ModelTestScenarios;
import js_test_data.JsTestScenario;
import org.codehaus.jettison.json.JSONObject;

import javax.inject.Inject;

public class CenterTagEventAndTodoScenario implements JsTestScenario {

    /*
     * event->e1
     * event->e2
     * event is a tag
     *
     * e2 has 2 hidden relations
     *
     * e1-r1->e2
     * e1-r2->e3
     * e1-r3->e4
     * e3-re31->e31
     * e3-re32->e32
     * e1-ra1->a1
     * o1-f1->o2
     * r1,r2,f1 are tagged to "to do"
     * "to do" is a tag
     * e2 has one hidden vertex
     *
     * single tagged to event
     *
     */

    @Inject
    GraphFactory graphFactory;

    @Inject
    VertexFactory vertexFactory;

    @Inject
    ModelTestScenarios modelTestScenarios;

    Tag toDo;
    Tag event;

    User user = User.withEmailAndUsername("f", "g");

    private VertexOperator
            e1,
            e2,
            e3,
            e4,
            e31,
            e32,
            o1,
            o2,
            a1,
            singleVertexTaggedToEvent;

    @Override
    public Object build() {
        UserGraph userGraph = graphFactory.loadForUser(user);
        createVertices();
        createEdges();
        SubGraphPojo aroundTodo = userGraph.aroundForkUriInShareLevels(
                toDo.uri(),
                ShareLevel.allShareLevelsInt
        );
        SubGraphPojo aroundEvent = userGraph.aroundForkUriWithDepthInShareLevels(
                event.uri(),
                2,
                ShareLevel.allShareLevelsInt

        );
        SubGraphPojo aroundE3 = userGraph.aroundForkUriInShareLevels(
                e3.uri(),
                ShareLevel.allShareLevelsInt
        );
        return NoEx.wrap(() -> new JSONObject().put(
                "aroundEvent",
                SubGraphJson.toJson(
                        aroundEvent
                )
        ).put(
                "aroundTodo",
                SubGraphJson.toJson(
                        aroundTodo
                )
        ).put(
                "aroundE3",
                SubGraphJson.toJson(
                        aroundE3
                )
        ).put(
                "singleTaggedToEvent",
                SubGraphJson.toJson(
                        userGraph.aroundForkUriInShareLevels(
                                singleVertexTaggedToEvent.uri(),
                                ShareLevel.allShareLevelsInt
                        )
                )
        )).get();
    }

    private void createVertices() {
        e1 = vertexFactory.createForOwner(
                user.username()
        );
        event = e1.addTag(
                modelTestScenarios.event()
        ).values().iterator().next();
        e1.label("e1");
        e2 = vertexFactory.createForOwner(
                user.username()
        );
        e2.label("e2");
        e2.addTag(
                event
        );
        e2.addVertexAndRelation();
        e3 = vertexFactory.createForOwner(
                user.username()
        );
        e3.label("e3");
        e4 = vertexFactory.createForOwner(
                user.username()
        );
        e4.label("e4");
        e31 = vertexFactory.createForOwner(
                user.username()
        );
        e31.label("e31");
        e32 = vertexFactory.createForOwner(
                user.username()
        );
        e32.label("e32");
        o1 = vertexFactory.createForOwner(
                user.username()
        );
        o1.label("o1");
        o2 = vertexFactory.createForOwner(
                user.username()
        );
        o2.label("o2");
        a1 = vertexFactory.createForOwner(
                user.username()
        );
        a1.label("a1");

        singleVertexTaggedToEvent = vertexFactory.createForOwner(
                user.username()
        );
        singleVertexTaggedToEvent.label("single tagged to event");
        singleVertexTaggedToEvent.addTag(
                event
        );
    }

    private void createEdges() {
        RelationOperator r1 = e1.addRelationToFork(e2.uri(), e1.getShareLevel(), e2.getShareLevel());
        r1.label("r1");
        toDo = r1.addTag(
                modelTestScenarios.toDo()
        ).values().iterator().next();
        RelationOperator r2 = e1.addRelationToFork(e3.uri(), e1.getShareLevel(), e3.getShareLevel());
        r2.label("r2");
        r2.addTag(toDo);
        RelationOperator f1 = o1.addRelationToFork(o2.uri(), o1.getShareLevel(), o2.getShareLevel());
        f1.label("f1");
        f1.addTag(toDo);
        e1.addRelationToFork(a1.uri(), e1.getShareLevel(), a1.getShareLevel()).label("ra1");
        e1.addRelationToFork(e4.uri(), e1.getShareLevel(), e4.getShareLevel()).label("r3");
        e3.addRelationToFork(e31.uri(), e3.getShareLevel(), e31.getShareLevel()).label("r3e1");
        e3.addRelationToFork(e32.uri(), e3.getShareLevel(), e32.getShareLevel()).label("r3e2");
    }
}
