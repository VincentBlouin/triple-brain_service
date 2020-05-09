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
import guru.bubl.module.model.graph.group_relation.GroupRelationFactory;
import guru.bubl.module.model.graph.group_relation.GroupRelationOperator;
import guru.bubl.module.model.graph.relation.RelationOperator;
import guru.bubl.module.model.graph.subgraph.SubGraphJson;
import guru.bubl.module.model.graph.subgraph.SubGraphPojo;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.graph.tag.TagPojo;
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
import java.util.UUID;

public class GroupRelationsScenario implements JsTestScenario {

/*
me -Possession of book 1->{
    -Possession of book 1-> book 1,
    <-Possessed by book 2- book 2,
    -Possession of book 3-> {
        -Possession of book 3->book 3 "two hidden relations",
        -Possession of book 3 copy->book 3 copy
   },
   -original relation->{
        -original relation->b1
        -same as original relation->b2
   },
   -other relation->other bubble "other bubble with early creation date"
   -other relation 2->other bubble 2
}

*/

    @Inject
    protected GraphFactory graphFactory;

    @Inject
    protected VertexFactory vertexFactory;

    @Inject
    private ModelTestScenarios modelTestScenarios;

    @Inject
    private GraphSearchFactory graphSearchFactory;

    @Inject
    private GroupRelationFactory groupRelationFactory;

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

    private GroupRelationOperator possessionOfBook1, possessionOfBook3, originalRelationGroupRelation;

    private SubGraphPojo subGraphForMe, possessionOfBook1SubGraph, possessionOfBook3SubGraph;

    @Override
    public JSONObject build() {
        UserGraph userGraph = graphFactory.loadForUser(user);
        createVertices();
        createEdges();
        subGraphForMe = userGraph.aroundForkUriInShareLevels(
                me.uri(),
                ShareLevel.allShareLevelsInt
        );
        possessionOfBook1SubGraph = userGraph.aroundForkUriInShareLevels(
                possessionOfBook1.uri(),
                ShareLevel.allShareLevelsInt
        );
        possessionOfBook3SubGraph = userGraph.aroundForkUriInShareLevels(
                possessionOfBook3.uri(),
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
                        .put(
                                "aroundPossessionOfBook1",
                                SubGraphJson.toJson(
                                        possessionOfBook1SubGraph
                                )
                        )
                        .put(
                                "aroundPossessionOfBook3",
                                SubGraphJson.toJson(
                                        possessionOfBook3SubGraph
                                )
                        )
                        .put(
                                "aroundOriginalRelation",
                                SubGraphJson.toJson(
                                        userGraph.aroundForkUriInShareLevels(
                                                originalRelationGroupRelation.uri(),
                                                ShareLevel.allShareLevelsInt
                                        )
                                )
                        ).put(
                        "aroundBook1",
                        SubGraphJson.toJson(
                                userGraph.aroundForkUriInShareLevels(
                                        book1.uri(),
                                        ShareLevel.allShareLevelsInt
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
        rBook1 = me.addRelationToFork(book1.uri(), ShareLevel.PRIVATE, ShareLevel.PRIVATE);
        rBook1.label("Possession of book 1");
        TagPojo possession = rBook1.addTag(
                modelTestScenarios.possessionIdentification()
        ).values().iterator().next();
        possessionOfBook1 = groupRelationFactory.withUri(
                rBook1.convertToGroupRelation(
                        UUID.randomUUID().toString(),
                        ShareLevel.PRIVATE,
                        "Possession",
                        ""
                ).uri()
        );
        rBook2 = book2.addRelationToFork(possessionOfBook1.uri(), ShareLevel.PRIVATE, ShareLevel.PRIVATE);
        rBook2.label("Possessed by book 2");
        rBook2.addTag(
                possession
        );
        rBook3 = possessionOfBook1.addRelationToFork(book3.uri(), ShareLevel.PRIVATE, ShareLevel.PRIVATE);
        rBook3.label("Possession of book 3");
        rBook3.addTag(
                possession
        );
        possessionOfBook3 = groupRelationFactory.withUri(
                rBook3.convertToGroupRelation(
                        UUID.randomUUID().toString(),
                        ShareLevel.PRIVATE,
                        "Possession of book 3",
                        ""
                ).uri()
        );
        TagPojo rBook3Tag = TestScenarios.tagFromFriendlyResource(
                rBook3
        );
        rBook3Copy = possessionOfBook3.addRelationToFork(book3Copy.uri(), ShareLevel.PRIVATE, ShareLevel.PRIVATE);
        rBook3Copy.addTag(
                possession
        );
        rBook3Copy.addTag(
                rBook3Tag
        );
        rBook3Copy.label("Possession of book 3 copy");
        RelationOperator otherRelation = me.addRelationToFork(otherBubble.uri(), ShareLevel.PRIVATE, ShareLevel.PRIVATE);
        otherRelation.label("other relation");
        RelationOperator otherRelation2 = me.addRelationToFork(otherBubble2.uri(), ShareLevel.PRIVATE, ShareLevel.PRIVATE);
        otherRelation2.label("other relation 2");
        RelationOperator originalRelation = me.addRelationToFork(b1.uri(), ShareLevel.PRIVATE, ShareLevel.PRIVATE);
        originalRelation.label("original relation");
        originalRelationGroupRelation = groupRelationFactory.withUri(
                originalRelation.convertToGroupRelation(
                        UUID.randomUUID().toString(),
                        ShareLevel.PRIVATE,
                        "original relation",
                        ""
                ).uri()
        );
        RelationOperator sameAsOriginalRelation = originalRelationGroupRelation.addRelationToFork(b2.uri(), ShareLevel.PRIVATE, ShareLevel.PRIVATE);
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
        possessionOfBook1SubGraph.vertexWithIdentifier(
                book1.uri()
        ).setCreationDate(book1Date.toDate().getTime());
        possessionOfBook1SubGraph.edgeWithIdentifier(
                rBook1.uri()
        ).setCreationDate(book1Date.toDate().getTime());
        possessionOfBook1SubGraph.vertexWithIdentifier(
                book2.uri()
        ).setCreationDate(book2Date.toDate().getTime());
        possessionOfBook1SubGraph.edgeWithIdentifier(
                rBook2.uri()
        ).setCreationDate(book2Date.toDate().getTime());
        possessionOfBook3SubGraph.vertexWithIdentifier(
                book3.uri()
        ).setCreationDate(book3Date.toDate().getTime());
        possessionOfBook3SubGraph.edgeWithIdentifier(
                rBook3.uri()
        ).setCreationDate(book3Date.toDate().getTime());
        possessionOfBook3SubGraph.edgeWithIdentifier(
                rBook3Copy.uri()
        ).setCreationDate(book3CopyDate.toDate().getTime());
    }

}
