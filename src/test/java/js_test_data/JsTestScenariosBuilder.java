/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package js_test_data;

import com.google.common.collect.Sets;
import com.google.inject.Injector;
import guru.bubl.module.model.graph.GraphFactory;
import guru.bubl.module.model.test.GraphComponentTest;
import guru.bubl.module.neo4j_graph_manipulator.graph.graph.UserGraphFactoryNeo4j;
import js_test_data.scenarios.*;
import org.apache.commons.io.FileUtils;
import org.codehaus.jettison.json.JSONObject;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

import javax.inject.Inject;
import java.io.File;
import java.util.Set;

public class JsTestScenariosBuilder {

    @Inject
    protected GraphFactory graphFactory;

    @Inject
    protected GraphDatabaseService graphDb;

    @Inject
    protected GraphComponentTest graphComponentTest;

    @Inject
    protected UserGraphFactoryNeo4j neo4jUserGraphFactory;

    Set<JsTestScenario> scenarios = Sets.newHashSet(
            new DeepGraphScenario(),
            new DeepGraphWithCircularityScenario(),
            new MergeBubbleGraphScenario(),
            new ThreeScenario(),
            new InverseRelationScenario(),
            new GroupRelationsScenario(),
            new GroupRelationWithImageScenario(),
            new HiddenGroupRelationsScenario(),
            new GroupRelationSpecialCaseScenario(),
            new OneBubbleHavingSuggestionsGraphScenario(),
            new WithAcceptedSuggestionScenario(),
            new KaraokeSchemaScenario(),
            new ProjectSchemaScenario(),
            new WikidataSearchResultForProjectScenario(),
            new CircularityScenario(),
            new PublicPrivateScenario(),
            new CreationDateScenario(),
            new RelationWithMultipleTagsScenario(),
            new RelationsAsTagScenario(),
            new BubbleWith2RelationsToSameBubbleScenario(),
            new AutomaticExpandScenario(),
            new CenterTagEventAndTodoScenario(),
            new SameLevelRelationsWithMoreThanOneCommonTagScenario(),
            new TagsWithSameLabelSearchResultScenario(),
            new TagCenterChildHavingGroupRelationScenario(),
            new ThreeLevelDeepGroupRelationScenario(),
            new ConvertVertexToGroupRelationScenario(),
            new LeaveContextScenario(),
            new TwoLevelGroupRelationScenario(),
            new ThreeLevelGroupRelationScenario()
    );


    public void build(Injector injector) throws Exception {
        Transaction transaction = graphDb.beginTx();
        JSONObject jsonObject = new JSONObject();
        for (JsTestScenario scenario : scenarios) {
            graphComponentTest.removeWholeGraph();
            injector.injectMembers(scenario);
            jsonObject.put(
                    jsonKeyNameFromTestScenario(scenario),
                    scenario.build()
            );
        }
        FileUtils.writeStringToFile(
                new File(
                        "src/test/java/js_test_data/js-test-data-server-side.json"
                ),
                jsonObject.toString()
        );
        transaction.failure();
        transaction.close();
    }

    private String jsonKeyNameFromTestScenario(JsTestScenario testScenario) {
        String className = testScenario.getClass().getSimpleName();
        String firstChar = (className.charAt(0) + "").toLowerCase();
        return firstChar + className.substring(1).replace("Scenario", "");
    }

}
