/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package js_test_data.scenarios;

import com.google.gson.Gson;
import guru.bubl.module.common_utils.NoEx;
import guru.bubl.module.model.User;
import guru.bubl.module.model.graph.FriendlyResourcePojo;
import guru.bubl.module.model.graph.GraphFactory;
import guru.bubl.module.model.graph.ShareLevel;
import guru.bubl.module.model.graph.SubGraphJson;
import guru.bubl.module.model.graph.relation.RelationOperator;
import guru.bubl.module.model.graph.tag.TagPojo;
import guru.bubl.module.model.graph.subgraph.SubGraphPojo;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.graph.vertex.VertexFactory;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import guru.bubl.module.model.search.GraphElementSearchResult;
import guru.bubl.module.model.search.GraphSearchFactory;
import guru.bubl.module.model.test.scenarios.TestScenarios;
import guru.bubl.test.module.utils.ModelTestScenarios;
import js_test_data.JsTestScenario;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.joda.time.DateTime;

import javax.inject.Inject;
import java.util.List;

public class GroupRelationsScenario implements JsTestScenario {

    /*
     * me -Possession of book 1->book 1
     * me <-Possessed by book 2-book 2
     * me -Possession of book 3->book 3
     * me -Possession of book 3 copy->book 3 copy
     * book3 has two hidden relations
     * Relation "Possession of book 3 copy" is identified to relation "Possession of book 3"
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

    @Inject
    GraphSearchFactory graphSearchFactory;

    private DateTime
            book1Date = new DateTime().minusSeconds(30),
            book2Date = book1Date.plusSeconds(10),
            book3Date = book2Date.plusSeconds(10),
            book3CopyDate = book3Date.plusSeconds(10);

    User user = User.withEmailAndUsername("a", "b");

    private VertexOperator
            me,
            book1,
            book2,
            book3,
            book3Copy,
            otherBubble,
            otherBubble2,
            b1,
            b2;

    private RelationOperator
            rBook1,
            rBook2,
            rBook3,
            rBook3Copy;

    private SubGraphPojo subGraphForMe;

    @Override
    public JSONObject build() {
        UserGraph userGraph = graphFactory.loadForUser(user);
        createVertices();
        createEdges();
        subGraphForMe = userGraph.aroundForkUriInShareLevels(
                me.uri(),
                ShareLevel.allShareLevelsInt
        );
        List<GraphElementSearchResult> bookSearchResults = graphSearchFactory.usingSearchTerm(
                "book"
        ).searchOnlyForOwnVerticesForAutoCompletionByLabel(
                user
        );
        setupCreationDates();
        return NoEx.wrap(() ->
                new JSONObject().put(
                        "graph",
                        SubGraphJson.toJson(
                                subGraphForMe
                        )
                ).put(
                        "bookSearchResults",
                        new JSONArray(
                                new Gson().toJson(
                                        bookSearchResults
                                )
                        )
                )
        ).get();
    }

    private void createVertices() {
        me = vertexFactory.createForOwner(
                user.username()
        );
        me.label("me");
        book1 = vertexFactory.createForOwner(
                user.username()
        );
        book1.label("book 1");
        TagPojo bookMeta = book1.addTag(
                modelTestScenarios.book()
        ).values().iterator().next();
        book2 = vertexFactory.createForOwner(
                user.username()
        );
        book2.label("book 2");
        book2.addTag(bookMeta);
        book3 = vertexFactory.createForOwner(
                user.username()
        );
        book3.label("book 3");
        book3.addVertexAndRelation();
        book3.addVertexAndRelation();
        book3.addTag(bookMeta);
        book3Copy = vertexFactory.createForOwner(
                user.username()
        );
        book3Copy.label("book 3 copy");
        book3Copy.addTag(bookMeta);
        otherBubble = vertexFactory.createForOwner(
                user.username()
        );
        otherBubble.label("other bubble");
        b1 = vertexFactory.createForOwner(
                user.username()
        );
        b1.label("b1");
        b2 = vertexFactory.createForOwner(
                user.username()
        );
        b2.label("b2");
        otherBubble2 = vertexFactory.createForOwner(
                user.username()
        );
        otherBubble2.label("other bubble 2");
    }

    private void createEdges() {
        rBook1 = me.addRelationToFork(book1);
        rBook1.label("Possession of book 1");
        TagPojo possession = book1.addTag(
                modelTestScenarios.possessionIdentification()
        ).values().iterator().next();
        rBook1.addTag(
                possession
        );
        rBook2 = book2.addRelationToFork(me);
        rBook2.label("Possessed by book 2");
        rBook2.addTag(
                possession
        );
        rBook3 = me.addRelationToFork(book3);
        rBook3.label("Possession of book 3");
        rBook3.addTag(
                possession
        );
        rBook3.addTag(
                possession
        );
        rBook3Copy = me.addRelationToFork(book3Copy);
        rBook3Copy.addTag(
                possession
        );
        rBook3Copy.addTag(
                TestScenarios.tagFromFriendlyResource(
                        rBook3
                )
        );
        rBook3Copy.label("Possession of book 3 copy");
        RelationOperator otherRelation = me.addRelationToFork(otherBubble);
        otherRelation.label("other relation");
        RelationOperator otherRelation2 = me.addRelationToFork(otherBubble2);
        otherRelation2.label("other relation 2");
        RelationOperator originalRelation = me.addRelationToFork(b1);
        originalRelation.label("original relation");
        RelationOperator sameAsOriginalRelation = me.addRelationToFork(b2);
        sameAsOriginalRelation.label("same as original relation");
        TagPojo b1RelationIdentification = new TagPojo(
                originalRelation.uri(),
                new FriendlyResourcePojo("original relation")
        );
        sameAsOriginalRelation.addTag(b1RelationIdentification);
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
        subGraphForMe.edgeWithIdentifier(
                rBook3Copy.uri()
        ).setCreationDate(book3CopyDate.toDate().getTime());
    }

}
