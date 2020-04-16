package js_test_data.scenarios;

import guru.bubl.module.model.User;
import guru.bubl.module.model.graph.FriendlyResourcePojo;
import guru.bubl.module.model.graph.GraphFactory;
import guru.bubl.module.model.graph.ShareLevel;
import guru.bubl.module.model.graph.SubGraphJson;
import guru.bubl.module.model.graph.relation.RelationOperator;
import guru.bubl.module.model.graph.tag.TagPojo;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.graph.vertex.VertexFactory;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import js_test_data.JsTestScenario;
import org.codehaus.jettison.json.JSONObject;

import javax.inject.Inject;

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


    private TagPojo group1;

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
                    "aroundGroup1",
                    SubGraphJson.toJson(
                            userGraph.aroundForkUriInShareLevels(
                                    group1.uri(),
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
        center.addRelationToFork(aaaa).label("r1");

        RelationOperator g1 = center.addRelationToFork(bbbb);
        g1.label("g1");

        group1 = g1.addTag(
                new TagPojo(
                        g1.uri(),
                        new FriendlyResourcePojo(
                                "group1"
                        )
                )
        ).values().iterator().next();

        RelationOperator g2 = center.addRelationToFork(cccc);
        g2.label("g2");
        g2.addTag(group1);

        RelationOperator g21 = center.addRelationToFork(dddd);
        g21.label("g21");
        TagPojo group2 = g21.addTag(
                new TagPojo(
                        g21.uri(),
                        new FriendlyResourcePojo(
                                "group2"
                        )
                )
        ).values().iterator().next();
        g21.addTag(group1);
        g21.addTag(group2);

        RelationOperator g22 = center.addRelationToFork(eeee);
        g22.label("g22");
        g22.addTag(group1);
        g22.addTag(group2);


        RelationOperator g23 = center.addRelationToFork(ffff);
        g23.label("g23");
        g23.addTag(group1);
        g23.addTag(group2);

        RelationOperator g3 = center.addRelationToFork(gggg);
        g3.label("g3");
        g3.addTag(group1);

        center.addRelationToFork(hhhh).label("r2");

        RelationOperator r3 = otherCenter.addRelationToFork(uuuu);
        r3.label("r3");
        r3.addTag(group1);
    }
}
