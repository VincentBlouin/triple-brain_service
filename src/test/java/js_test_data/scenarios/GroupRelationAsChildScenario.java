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
import guru.bubl.module.model.graph.edge.EdgeOperator;
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

public class GroupRelationAsChildScenario implements JsTestScenario {

/*
center <-rCenter- group relation{
    -rc1-> c1,
    -rc2-> c2,
    -rc3-> c3
}
*/

    @Inject
    protected GraphFactory graphFactory;

    @Inject
    protected VertexFactory vertexFactory;

    @Inject
    private GroupRelationFactory groupRelationFactory;

    User user = User.withEmailAndUsername("a", "b");

    private VertexOperator
            center,
            c1,
            c2,
            c3;

    private GroupRelationOperator groupRelation;

    @Override
    public JSONObject build() {
        UserGraph userGraph = graphFactory.loadForUser(user);
        createVertices();
        createEdges();
        return NoEx.wrap(() ->
                new JSONObject().put(
                        "graph",
                        SubGraphJson.toJson(
                                userGraph.aroundForkUriInShareLevels(
                                        center.uri(),
                                        ShareLevel.allShareLevelsInt
                                )
                        )
                ).put(
                        "aroundGroupRelation",
                        SubGraphJson.toJson(
                                userGraph.aroundForkUriInShareLevels(
                                        groupRelation.uri(),
                                        ShareLevel.allShareLevelsInt
                                )
                        )
                )
        ).get();
    }

    private void createVertices() {
        center = vertexFactory.createForOwner(
                user.username()
        );
        center.label("center");
        c1 = vertexFactory.createForOwner(
                user.username()
        );
        c1.label("c1");
        c2 = vertexFactory.createForOwner(
                user.username()
        );
        c2.label("c2");
        c3 = vertexFactory.createForOwner(
                user.username()
        );
        c3.label("c3");
    }

    private void createEdges() {
        RelationOperator c1Edge = c1.addRelationToFork(center.uri(), ShareLevel.PRIVATE, ShareLevel.PRIVATE);
        c1Edge.label("rc1");
        groupRelation = groupRelationFactory.withUri(
                c1Edge.convertToGroupRelation(
                        UUID.randomUUID().toString(),
                        ShareLevel.PRIVATE,
                        "group relation",
                        ""
                ).uri()
        );
        groupRelation.addRelationToFork(c2.uri(), ShareLevel.PRIVATE, ShareLevel.PRIVATE).label("rc2");
        groupRelation.addRelationToFork(c3.uri(), ShareLevel.PRIVATE, ShareLevel.PRIVATE).label("rc3");
    }
}
