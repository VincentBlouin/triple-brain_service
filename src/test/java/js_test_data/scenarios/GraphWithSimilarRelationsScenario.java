/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package js_test_data.scenarios;

import guru.bubl.module.model.graph.*;
import guru.bubl.module.model.graph.GraphFactory;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.graph.identification.IdentificationPojo;
import guru.bubl.module.model.graph.subgraph.SubGraphPojo;
import guru.bubl.test.module.utils.ModelTestScenarios;
import js_test_data.JsTestScenario;
import org.codehaus.jettison.json.JSONObject;
import guru.bubl.module.model.User;
import guru.bubl.module.model.graph.edge.EdgeOperator;
import guru.bubl.module.model.graph.vertex.VertexFactory;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import guru.bubl.module.model.json.graph.SubGraphJson;
import org.joda.time.DateTime;

import javax.inject.Inject;
import java.util.Date;

public class GraphWithSimilarRelationsScenario implements JsTestScenario {

    /*
    * me -Possession of book 1->book 1
    * me <-Possessed by book 2-book 2
    * me -Possession of book 3->book 3
    * all possession relations are identified to possession
    * me -other relation->other bubble
    * me -other relation 2->other bubble 2
    * other bubble with early creation date
    * me -original relation->b1
    * me -same as original relation->b2
    */

    @Inject
    protected GraphFactory graphFactory;

    @Inject
    protected VertexFactory vertexFactory;

    @Inject
    ModelTestScenarios modelTestScenarios;

    DateTime book1Date = new DateTime().minusSeconds(30);
    DateTime book2Date = book1Date.plusSeconds(10);
    DateTime book3Date = book2Date.plusSeconds(10);

    User user = User.withEmailAndUsername("a", "b");

    private VertexOperator
            me,
            book1,
            book2,
            book3,
            otherBubble,
            otherBubble2,
            b1,
            b2;

    EdgeOperator
            rBook1,
            rBook2,
            rBook3;


    private SubGraphPojo subGraphForMe;

    @Override
    public JSONObject build() {
        UserGraph userGraph = graphFactory.createForUser(user);
        createVertices();
        createEdges();
        subGraphForMe = userGraph.graphWithDepthAndCenterVertexId(
                1,
                me.uri()
        );
        setupCreationDates();
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
        b1.label("b1");
        b2 = vertexFactory.createForOwnerUsername(
                user.username()
        );
        b2.label("b2");
        otherBubble2 = vertexFactory.createForOwnerUsername(
                user.username()
        );
        otherBubble2.label("other bubble 2");
    }

    private void createEdges() {
        rBook1 = me.addRelationToVertex(book1);
        rBook1.label("Possession of book 1");
        rBook1.addSameAs(
                modelTestScenarios.possessionIdentification()
        );
        rBook2 = book2.addRelationToVertex(me);
        rBook2.label("Possessed by book 2");
        rBook2.addSameAs(
                modelTestScenarios.possessionIdentification()
        );
        rBook3 = me.addRelationToVertex(book3);
        rBook3.label("Possession of book 3");
        rBook3.addSameAs(
                modelTestScenarios.possessionIdentification()
        );
        EdgeOperator otherRelation = me.addRelationToVertex(otherBubble);
        otherRelation.label("other relation");
        EdgeOperator otherRelation2 = me.addRelationToVertex(otherBubble2);
        otherRelation2.label("other relation 2");
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

    private void setupCreationDates() {
        subGraphForMe.vertexWithIdentifier(
                otherBubble.uri()
        ).setCreationDate(new DateTime().minusDays(1).toDate().getTime());
        subGraphForMe.vertexWithIdentifier(
                book1.uri()
        ).setCreationDate(book1Date.toDate().getTime());
        subGraphForMe.vertexWithIdentifier(
                book1.uri()
        ).setCreationDate(book1Date.toDate().getTime());
        subGraphForMe.edgeWithIdentifier(
                rBook1.uri()
        ).setCreationDate(book1Date.toDate().getTime());
        subGraphForMe.vertexWithIdentifier(
                book2.uri()
        ).setCreationDate(book2Date.toDate().getTime());
        subGraphForMe.edgeWithIdentifier(
                rBook2.uri()
        ).setCreationDate(book2Date.toDate().getTime());
        subGraphForMe.vertexWithIdentifier(
                book3.uri()
        ).setCreationDate(book3Date.toDate().getTime());
        subGraphForMe.edgeWithIdentifier(
                rBook3.uri()
        ).setCreationDate(book3Date.toDate().getTime());
    }

}
