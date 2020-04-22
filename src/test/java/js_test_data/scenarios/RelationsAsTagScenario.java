/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package js_test_data.scenarios;

import com.google.gson.Gson;
import guru.bubl.module.common_utils.NoEx;
import guru.bubl.module.model.User;
import guru.bubl.module.model.graph.GraphFactory;
import guru.bubl.module.model.graph.ShareLevel;
import guru.bubl.module.model.graph.group_relation.GroupRelationFactory;
import guru.bubl.module.model.graph.group_relation.GroupRelationOperator;
import guru.bubl.module.model.graph.subgraph.SubGraphJson;
import guru.bubl.module.model.graph.relation.RelationOperator;
import guru.bubl.module.model.graph.tag.TagPojo;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.graph.vertex.VertexFactory;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import guru.bubl.module.model.search.GraphSearchFactory;
import guru.bubl.module.model.test.scenarios.TestScenarios;
import js_test_data.JsTestScenario;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import javax.inject.Inject;
import java.util.UUID;

public class RelationsAsTagScenario implements JsTestScenario {

    /*

     center -some relation->{
        -original some relation->b1
        -some relation->b2
        -some relation->b3
     }
     center-some different relation->b4
    */

    @Inject
    private GraphSearchFactory graphSearchFactory;

    @Inject
    protected GraphFactory graphFactory;

    @Inject
    private VertexFactory vertexFactory;

    @Inject
    private GroupRelationFactory groupRelationFactory;

    private VertexOperator
            center,
            b1,
            b2,
            b3,
            b4;

    private GroupRelationOperator groupRelation;

    User user = User.withEmailAndUsername("a", "b");

    UserGraph userGraph;

    @Override
    public JSONObject build() {
        userGraph = graphFactory.loadForUser(user);
        userGraph.createVertex();
        createVertices();
        createRelations();

        return NoEx.wrap(() -> new JSONObject().put(
                "searchSome",
                new JSONArray(
                        new Gson().toJson(
                                graphSearchFactory.usingSearchTerm(
                                        "some"
                                ).searchRelationsForAutoCompletionByLabel(
                                        user
                                )
                        )
                )
                ).put(
                "graph",
                SubGraphJson.toJson(
                        userGraph.aroundForkUriInShareLevels(
                                center.uri(),
                                ShareLevel.allShareLevelsInt
                        )
                )).put("aroundGroupRelation", SubGraphJson.toJson(
                        userGraph.aroundForkUriInShareLevels(
                                groupRelation.uri(),
                                ShareLevel.allShareLevelsInt
                        )
                ))
        ).get();
    }

    private void createVertices() {
        center = vertexFactory.createForOwner(
                user.username()
        );
        center.label("center");
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

    private void createRelations() {
        RelationOperator firstSomeRelation = center.addRelationToFork(b1.uri(), ShareLevel.PRIVATE, ShareLevel.PRIVATE);
        groupRelation = groupRelationFactory.withUri(
                firstSomeRelation.convertToGroupRelation(
                        UUID.randomUUID().toString(),
                        ShareLevel.PRIVATE,
                        "some relation",
                        ""
                ).uri()
        );
        firstSomeRelation.label("original some relation");
        TagPojo firstSomeRelationAsIdentifier = TestScenarios.tagFromFriendlyResource(
                firstSomeRelation
        );
        groupRelation.addTag(firstSomeRelationAsIdentifier, ShareLevel.PRIVATE);
        groupRelation.addRelationToFork(b2.uri(), ShareLevel.PRIVATE, ShareLevel.PRIVATE).label("some relation");
        groupRelation.addRelationToFork(b3.uri(), ShareLevel.PRIVATE, ShareLevel.PRIVATE).label("some relation");
        center.addRelationToFork(b4.uri(), ShareLevel.PRIVATE, ShareLevel.PRIVATE).label("some different relation");
    }
}
