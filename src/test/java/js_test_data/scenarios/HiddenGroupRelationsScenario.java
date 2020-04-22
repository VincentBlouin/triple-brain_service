/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package js_test_data.scenarios;

import com.google.common.collect.Sets;
import guru.bubl.module.common_utils.NoEx;
import guru.bubl.module.model.Image;
import guru.bubl.module.model.User;
import guru.bubl.module.model.graph.GraphFactory;
import guru.bubl.module.model.graph.ShareLevel;
import guru.bubl.module.model.graph.group_relation.GroupRelationFactory;
import guru.bubl.module.model.graph.group_relation.GroupRelationOperator;
import guru.bubl.module.model.graph.subgraph.SubGraphJson;
import guru.bubl.module.model.graph.relation.RelationOperator;
import guru.bubl.module.model.graph.subgraph.SubGraphPojo;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.graph.vertex.VertexFactory;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import guru.bubl.test.module.utils.ModelTestScenarios;
import js_test_data.JsTestScenario;
import org.codehaus.jettison.json.JSONObject;

import javax.inject.Inject;
import java.net.URI;
import java.util.UUID;

public class HiddenGroupRelationsScenario implements JsTestScenario {

    /*
     * b1-r1->b2
     * b2 has hidden relations
     * b2 has an image
     * b2 -T-shirt->{
         -shirt1->shirt1
         -shirt2->shirt2{
            shirt2-color->red
         }
       }
      shirt2 has an image
 */

    /*
     * Distant graph
     * bubble labeled "distant bubble" will eventually connect to b2 or b1
     */

    @Inject
    protected GraphFactory graphFactory;

    @Inject
    protected VertexFactory vertexFactory;

    @Inject
    private ModelTestScenarios modelTestScenarios;

    @Inject
    private GroupRelationFactory groupRelationFactory;

    User user = User.withEmailAndUsername("a", "b");

    private VertexOperator
            b1,
            b2,
            shirt1,
            shirt2,
            red,
            distantBubble;

    private GroupRelationOperator tShirtGroupRelation;

    @Override
    public JSONObject build() {
        UserGraph userGraph = graphFactory.loadForUser(user);
        createVertices();
        createRelations();
        SubGraphPojo b2Graph = userGraph.aroundForkUriInShareLevels(
                b2.uri(),
                ShareLevel.allShareLevelsInt
        );
        SubGraphPojo b1Graph = userGraph.aroundForkUriInShareLevels(
                b1.uri(),
                ShareLevel.allShareLevelsInt
        );
        SubGraphPojo shirt2Graph = userGraph.aroundForkUriInShareLevels(
                shirt2.uri(),
                ShareLevel.allShareLevelsInt
        );
        SubGraphPojo distantBubbleGraph = userGraph.aroundForkUriInShareLevels(
                distantBubble.uri(),
                ShareLevel.allShareLevelsInt
        );
        RelationOperator distantToB2 = distantBubble.addRelationToFork(b2.uri(), ShareLevel.PRIVATE, ShareLevel.PRIVATE);
        SubGraphPojo b2GraphWhenConnectedToDistantBubble = userGraph.aroundForkUriInShareLevels(
                b2.uri(),
                ShareLevel.allShareLevelsInt
        );
        distantToB2.remove();
        b1.addRelationToFork(distantBubble.uri(), ShareLevel.PRIVATE, ShareLevel.PRIVATE);
        SubGraphPojo distantBubbleGraphWhenConnectedToBubble1 = userGraph.aroundForkUriInShareLevels(
                distantBubble.uri(),
                ShareLevel.allShareLevelsInt
        );

        return NoEx.wrap(() ->
                new JSONObject().put(
                        "b1Graph",
                        SubGraphJson.toJson(b1Graph)
                ).put(
                        "b2Graph",
                        SubGraphJson.toJson(b2Graph)
                ).put(
                        "distantBubbleGraph",
                        SubGraphJson.toJson(distantBubbleGraph)
                ).put(
                        "b2GraphWhenConnectedToDistantBubble",
                        SubGraphJson.toJson(b2GraphWhenConnectedToDistantBubble)
                ).put(
                        "distantBubbleGraphWhenConnectedToBubble1",
                        SubGraphJson.toJson(distantBubbleGraphWhenConnectedToBubble1)
                ).put(
                        "distantBubbleUri",
                        distantBubble.uri()
                ).put(
                        "tShirtGroupRelationGraph",
                        SubGraphJson.toJson(
                                userGraph.aroundForkUriInShareLevels(
                                        tShirtGroupRelation.uri(),
                                        ShareLevel.allShareLevelsInt
                                )
                        )

                ).put(
                        "shirt2Graph",
                        SubGraphJson.toJson(shirt2Graph)
                )
                        .put(
                                "shirt2BubbleUri",
                                shirt2.uri()
                        )
        ).get();
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
        b2.addImages(Sets.newHashSet(
                Image.withUrlForSmallAndUriForBigger(
                        "base64ForB2",
                        URI.create("http://example.org/bigImageForB2")
                )
                )
        );
        shirt1 = vertexFactory.createForOwner(
                user.username()
        );
        shirt1.label("shirt1");
        shirt2 = vertexFactory.createForOwner(
                user.username()
        );
        shirt2.label("shirt2");
        shirt2.addImages(Sets.newHashSet(
                Image.withUrlForSmallAndUriForBigger(
                        "base64ForShirt2",
                        URI.create("http://example.org/bigImageForShirt2")
                )
                )
        );
        red = vertexFactory.createForOwner(
                user.username()
        );
        red.label("red");
        distantBubble = vertexFactory.createForOwner(
                user.username()
        );
        distantBubble.label("distant bubble");
    }

    private void createRelations() {
        b1.addRelationToFork(b2.uri(), ShareLevel.PRIVATE, ShareLevel.PRIVATE).label("r1");
        RelationOperator shirt1Relation = b2.addRelationToFork(shirt1.uri(), ShareLevel.PRIVATE, ShareLevel.PRIVATE);
        shirt1Relation.label("shirt1");
        shirt1Relation.addTag(modelTestScenarios.tShirt());
        tShirtGroupRelation = groupRelationFactory.withUri(
                shirt1Relation.convertToGroupRelation(
                        UUID.randomUUID().toString(),
                        ShareLevel.PRIVATE,
                        "T-shirt",
                        ""
                ).uri()
        );
        RelationOperator shirt2Relation = tShirtGroupRelation.addRelationToFork(shirt2.uri(), ShareLevel.PRIVATE, ShareLevel.PRIVATE);
        shirt2Relation.label("shirt2");
        RelationOperator color = shirt2.addRelationToFork(red.uri(), ShareLevel.PRIVATE, ShareLevel.PRIVATE);
        color.label("color");
    }
}
