/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package js_test_data.scenarios;

import guru.bubl.module.model.graph.*;
import js_test_data.JsTestScenario;
import org.codehaus.jettison.json.JSONObject;
import guru.bubl.module.model.User;
import guru.bubl.module.model.graph.edge.EdgeOperator;
import guru.bubl.module.model.graph.vertex.VertexFactory;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import guru.bubl.module.model.json.graph.SubGraphJson;

import javax.inject.Inject;

public class GraphWithSimilarRelationsScenario implements JsTestScenario {

    /*
    * me-Possession of book 1->book 1
    * me<-Possessed by book 2-book 2
    * me-Possession of book 3->book 3
    * me-other relation->other bubble
    * me-original relation->b1
    * me-same as original relation->b2
    */

    @Inject
    protected GraphFactory graphFactory;

    @Inject
    protected VertexFactory vertexFactory;

    @Inject
    ModelTestScenarios modelTestScenarios;

    User user = User.withEmailAndUsername("a", "b");

    private VertexOperator
            me,
            book1,
            book2,
            book3,
            otherBubble,
            b1,
            b2;

    @Override
    public JSONObject build() {
        UserGraph userGraph = graphFactory.createForUser(user);
        createVertices();
        createEdges();
        SubGraphPojo subGraphForMe = userGraph.graphWithDepthAndCenterVertexId(
                1,
                me.uri()
        );
        return SubGraphJson.toJson(
                subGraphForMe
        );
    }
    private void createVertices() {
        me = vertexFactory.createForOwnerUsername(
                user.username()
        );
        me.label("me");
        book1 = vertexFactory.createForOwnerUsername(
                user.username()
        );
        book1.label("book 1");
        book2 = vertexFactory.createForOwnerUsername(
                user.username()
        );
        book2.label("book 2");
        book3 = vertexFactory.createForOwnerUsername(
                user.username()
        );
        book3.label("book 3");
        otherBubble = vertexFactory.createForOwnerUsername(
                user.username()
        );
        otherBubble.label("other bubble");
        b1 = vertexFactory.createForOwnerUsername(
                user.username()
        );
        otherBubble.label("b1");
        b2 = vertexFactory.createForOwnerUsername(
                user.username()
        );
        otherBubble.label("b2");
    }

    private void createEdges() {
        EdgeOperator rBook1 = me.addRelationToVertex(book1);
        rBook1.label("Possession of book 1");
        rBook1.addSameAs(
                modelTestScenarios.possessionIdentification()
        );
        EdgeOperator rBook2 = book2.addRelationToVertex(me);
        rBook2.label("Possessed by book 2");
        rBook2.addSameAs(
                modelTestScenarios.possessionIdentification()
        );
        EdgeOperator rBook3 = me.addRelationToVertex(book3);
        rBook3.label("Possession of book 3");
        rBook3.addSameAs(
                modelTestScenarios.possessionIdentification()
        );
        EdgeOperator otherRelation = me.addRelationToVertex(otherBubble);
        otherRelation.label("other relation");
        EdgeOperator originalRelation = me.addRelationToVertex(b1);
        originalRelation.label("original relation");
        EdgeOperator sameAsOriginalRelation = me.addRelationToVertex(b2);
        sameAsOriginalRelation.label("same as original relation");
        IdentificationPojo b1RelationIdentification = new IdentificationPojo(
                originalRelation.uri(),
                new FriendlyResourcePojo("original relation")
        );
        sameAsOriginalRelation.addSameAs(b1RelationIdentification);
    }
}
