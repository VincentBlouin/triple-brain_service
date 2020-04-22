/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package js_test_data.scenarios;

import guru.bubl.module.model.User;
import guru.bubl.module.model.graph.FriendlyResourcePojo;
import guru.bubl.module.model.graph.GraphFactory;
import guru.bubl.module.model.graph.ShareLevel;
import guru.bubl.module.model.graph.group_relation.GroupRelationFactory;
import guru.bubl.module.model.graph.group_relation.GroupRelationOperator;
import guru.bubl.module.model.graph.relation.RelationOperator;
import guru.bubl.module.model.graph.subgraph.SubGraphJson;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.graph.tag.TagPojo;
import guru.bubl.module.model.graph.vertex.VertexFactory;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import js_test_data.JsTestScenario;
import org.codehaus.jettison.json.JSONObject;
import sun.misc.UUDecoder;

import javax.inject.Inject;
import java.net.URI;
import java.util.UUID;

public class ThreeLevelDeepGroupRelationScenario implements JsTestScenario {
    /*
     fast charging station -region-> {
        -sub-region-a->{
            -r1->b1
            -r2->b2
        }
        -sub-region-b->{
            -r3->b3
            -r4->b4
        }
     }
    */

    private GroupRelationOperator region, subRegionA, subRegionB;

    private TagPojo regionTag = new TagPojo(
            URI.create(
                    "https://mindrespect.com/e6452d32-8015-4d8e-89ad-58f14699680d"
            ),
            new FriendlyResourcePojo(
                    "region"
            )
    );
    private TagPojo subRegionTag = new TagPojo(
            URI.create(
                    "https://mindrespect.com/077e5cd5-0adf-471a-8145-228107cf66e5"
            ),
            new FriendlyResourcePojo(
                    "sub-region"
            )
    );
    private TagPojo subRegionATag = new TagPojo(
            URI.create(
                    "https://mindrespect.com/f893f25b-7cf1-4fee-860a-18b0764949d3"
            ),
            new FriendlyResourcePojo(
                    "sub-region-a"
            )
    );

    private TagPojo subRegionBTag = new TagPojo(
            URI.create(
                    "https://mindrespect.com/fc5379ff-fdc8-4b9d-ab40-d9adcac83315"
            ),
            new FriendlyResourcePojo(
                    "sub-region-b"
            )
    );

    private VertexOperator
            fastChargingStation,
            b1,
            b2,
            b3,
            b4;

    private RelationOperator
            r1,
            r2,
            r3,
            r4;

    @Inject
    private GraphFactory graphFactory;

    @Inject
    private VertexFactory vertexFactory;

    @Inject
    private GroupRelationFactory groupRelationFactory;

    User user = User.withEmailAndUsername(
            "a",
            "églantier"
    );

    @Override
    public Object build() {
        UserGraph userGraph = graphFactory.loadForUser(user);
        createVertices();
        createEdges();
        try {
            return new JSONObject().put(
                    "fastChargingStationGraph",
                    SubGraphJson.toJson(
                            userGraph.aroundForkUriInShareLevels(
                                    fastChargingStation.uri(),
                                    ShareLevel.allShareLevelsInt
                            )
                    )
            ).put(
                    "aroundRegion",
                    SubGraphJson.toJson(
                            userGraph.aroundForkUriInShareLevels(
                                    region.uri(),
                                    ShareLevel.allShareLevelsInt
                            )
                    )
            ).put(
                    "aroundSubRegionA",
                    SubGraphJson.toJson(
                            userGraph.aroundForkUriInShareLevels(
                                    subRegionA.uri(),
                                    ShareLevel.allShareLevelsInt
                            )
                    )
            ).put(
                    "aroundSubRegionB",
                    SubGraphJson.toJson(
                            userGraph.aroundForkUriInShareLevels(
                                    subRegionB.uri(),
                                    ShareLevel.allShareLevelsInt
                            )
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void createVertices() {
        fastChargingStation = vertexFactory.createForOwner(
                user.username()
        );
        fastChargingStation.label("fast charging station");
        b1 = vertexFactory.createForOwner(
                user.username()
        );
        b1.label("b1");
        b2 = vertexFactory.createForOwner(
                user.username()
        );
        b2.label("b2");
        b3 = vertexFactory.createForOwner(
                user.username()
        );
        b3.label("b3");
        b4 = vertexFactory.createForOwner(
                user.username()
        );
        b4.label("b4");
    }

    private void createEdges() {
        r1 = fastChargingStation.addRelationToFork(b1.uri(), ShareLevel.PRIVATE, ShareLevel.PRIVATE);
        r1.label("r1");
        r1.addTag(regionTag);
        region = groupRelationFactory.withUri(
                r1.convertToGroupRelation(
                        UUID.randomUUID().toString(),
                        ShareLevel.PRIVATE,
                        "region",
                        ""
                ).uri()
        );
        r1.addTag(subRegionTag);
        r1.addTag(subRegionATag);
        subRegionA = groupRelationFactory.withUri(
                r1.convertToGroupRelation(
                        UUID.randomUUID().toString(),
                        ShareLevel.PRIVATE,
                        "sub-region-a",
                        ""
                ).uri()
        );
        r2 = subRegionA.addRelationToFork(b2.uri(), ShareLevel.PRIVATE, ShareLevel.PRIVATE);
        r2.label("r2");
        r3 = region.addRelationToFork(b3.uri(), ShareLevel.PRIVATE, ShareLevel.PRIVATE);
        r3.label("r3");
        r3.addTag(regionTag);
        r3.addTag(subRegionTag);
        r3.addTag(subRegionBTag);
        subRegionB = groupRelationFactory.withUri(
                r3.convertToGroupRelation(
                        UUID.randomUUID().toString(),
                        ShareLevel.PRIVATE,
                        "sub-region-b",
                        ""
                ).uri()
        );
        r4 = subRegionB.addRelationToFork(b4.uri(), ShareLevel.PRIVATE, ShareLevel.PRIVATE);
        r4.label("r4");
    }
}
