package js_test_data.scenarios;

import guru.bubl.module.model.User;
import guru.bubl.module.model.graph.FriendlyResourcePojo;
import guru.bubl.module.model.graph.GraphFactory;
import guru.bubl.module.model.graph.ShareLevel;
import guru.bubl.module.model.graph.group_relation.GroupRelation;
import guru.bubl.module.model.graph.group_relation.GroupRelationFactory;
import guru.bubl.module.model.graph.group_relation.GroupRelationOperator;
import guru.bubl.module.model.graph.subgraph.SubGraphJson;
import guru.bubl.module.model.graph.relation.RelationOperator;
import guru.bubl.module.model.graph.tag.TagPojo;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.graph.vertex.VertexFactory;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import js_test_data.JsTestScenario;
import org.codehaus.jettison.json.JSONObject;

import javax.inject.Inject;
import java.util.UUID;

public class TwoLevelGroupRelationScenario implements JsTestScenario {
/*
    center -r1-> aaaa
    center -group1-> {
        -g1->bbbb
        -g2->cccc
        -group2->{
            -g21->dddd
            -g22->eeee
            -g23->ffff
        }
        -g3->gggg
    }
    center -r2-> hhhh
    other center -r3-> uuuu
    r3 is tagged to group1
*/


    @Inject
    protected GraphFactory graphFactory;

    @Inject
    protected VertexFactory vertexFactory;

    @Inject
    protected GroupRelationFactory groupRelationFactory;

    User user = User.withEmailAndUsername("a", "b");

    private VertexOperator
            center,
            aaaa,
            bbbb,
            cccc,
            dddd,
            eeee,
            ffff,
            gggg,
            hhhh,
            otherCenter,
            uuuu;


    private TagPojo group1Tag;
    private GroupRelationOperator group1, group2;


    @Override
    public Object build() {
        UserGraph userGraph = graphFactory.loadForUser(user);
        createVertices();
        createEdges();
        try {
            return new JSONObject().put(
                    "getGraph",
                    SubGraphJson.toJson(
                            userGraph.aroundForkUriInShareLevels(
                                    center.uri(),
                                    ShareLevel.allShareLevelsInt
                            )
                    )
            ).put(
                    "aroundGroup1Tag",
                    SubGraphJson.toJson(
                            userGraph.aroundForkUriInShareLevels(
                                    group1Tag.uri(),
                                    ShareLevel.allShareLevelsInt
                            )
                    )
            ).put(
                    "aroundGroup1",
                    SubGraphJson.toJson(
                            userGraph.aroundForkUriInShareLevels(
                                    group1.uri(),
                                    ShareLevel.allShareLevelsInt
                            )
                    )
            ).put(
                    "aroundGroup2",
                    SubGraphJson.toJson(
                            userGraph.aroundForkUriInShareLevels(
                                    group2.uri(),
                                    ShareLevel.allShareLevelsInt
                            )
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void createVertices() {
        center = vertexFactory.createForOwner(user.username());
        center.label("center");
        aaaa = vertexFactory.createForOwner(user.username());
        aaaa.label("aaaa");
        bbbb = vertexFactory.createForOwner(user.username());
        bbbb.label("bbbb");
        cccc = vertexFactory.createForOwner(user.username());
        cccc.label("cccc");
        dddd = vertexFactory.createForOwner(user.username());
        dddd.label("dddd");
        eeee = vertexFactory.createForOwner(user.username());
        eeee.label("eeee");
        ffff = vertexFactory.createForOwner(user.username());
        ffff.label("ffff");
        gggg = vertexFactory.createForOwner(user.username());
        gggg.label("gggg");
        hhhh = vertexFactory.createForOwner(user.username());
        hhhh.label("hhhh");
        otherCenter = vertexFactory.createForOwner(user.username());
        otherCenter.label("other center");
        uuuu = vertexFactory.createForOwner(user.username());
        uuuu.label("uuuu");
    }

    private void createEdges() {
        center.addRelationToFork(aaaa.uri(), center.getShareLevel(), aaaa.getShareLevel()).label("r1");

        RelationOperator g1 = center.addRelationToFork(bbbb.uri(), center.getShareLevel(), bbbb.getShareLevel());
        g1.label("g1");
        group1Tag = g1.addTag(
                new TagPojo(
                        g1.uri(),
                        new FriendlyResourcePojo(
                                "group1"
                        )
                )
        ).values().iterator().next();
        group1 = groupRelationFactory.withUri(
                g1.convertToGroupRelation(
                        UUID.randomUUID().toString(),
                        ShareLevel.PRIVATE,
                        "group1",
                        ""
                ).uri()
        );
        RelationOperator g2 = group1.addRelationToFork(cccc.uri(), group1.getShareLevel(), cccc.getShareLevel());
        g2.label("g2");
        RelationOperator g21 = group1.addRelationToFork(dddd.uri(), group1.getShareLevel(), dddd.getShareLevel());
        g21.label("g21");
        g21.addTag(
                new TagPojo(
                        g21.uri(),
                        new FriendlyResourcePojo(
                                "group2"
                        )
                )
        ).values().iterator().next();
        group2 = groupRelationFactory.withUri(
                g21.convertToGroupRelation(
                        UUID.randomUUID().toString(),
                        ShareLevel.PRIVATE,
                        "group2",
                        ""
                ).uri()
        );
        RelationOperator g22 = group2.addRelationToFork(eeee.uri(), group2.getShareLevel(), eeee.getShareLevel());
        g22.label("g22");


        RelationOperator g23 = group2.addRelationToFork(ffff.uri(), group2.getShareLevel(), ffff.getShareLevel());
        g23.label("g23");

        RelationOperator g3 = group1.addRelationToFork(gggg.uri(), group1.getShareLevel(), gggg.getShareLevel());
        g3.label("g3");

        center.addRelationToFork(hhhh.uri(), center.getShareLevel(), hhhh.getShareLevel()).label("r2");

        RelationOperator r3 = otherCenter.addRelationToFork(uuuu.uri(), otherCenter.getShareLevel(), uuuu.getShareLevel());
        r3.label("r3");
        r3.addTag(group1Tag);
    }
}
