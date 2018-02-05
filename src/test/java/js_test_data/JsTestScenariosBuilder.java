/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package js_test_data;

import com.google.common.collect.Sets;
import com.google.inject.Injector;
import js_test_data.scenarios.*;
import org.apache.commons.io.FileUtils;
import org.codehaus.jettison.json.JSONObject;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import guru.bubl.module.model.graph.GraphFactory;
import guru.bubl.module.model.test.GraphComponentTest;
import guru.bubl.module.neo4j_graph_manipulator.graph.graph.Neo4jUserGraphFactory;

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
    protected Neo4jUserGraphFactory neo4jUserGraphFactory;

    Set<JsTestScenario> scenarios = Sets.newHashSet(
            new DeepGraphScenario(),
            new DeepGraphWithCircularityScenario(),
            new MergeBubbleGraphScenario(),
            new ThreeBubblesGraphScenario(),
            new GraphWithAnInverseRelationScenario(),
            new GraphWithSimilarRelationsScenario(),
            new GroupRelationWithImageScenario(),
            new GraphWithHiddenSimilarRelationsScenario(),
            new GroupRelationSpecialCaseScenario(),
            new OneBubbleHavingSuggestionsGraphScenario(),
            new WithAcceptedSuggestionScenario(),
            new KaraokeSchemaScenario(),
            new ProjectSchemaScenario(),
            new WikidataSearchResultForProjectScenario(),
            new GraphWithCircularityScenario(),
            new PublicPrivateScenario(),
            new CreationDateScenario(),
            new RelationWithMultipleIdentifiers(),
            new RelationsAsIdentifierScenario(),
            new BubbleWith2RelationsToSameBubbleScenario(),
            new AutomaticExpandScenario(),
            new CenterMetaEventAndTodoScenario(),
            new SameLevelRelationsWithMoreThanOneCommonMetaScenario(),
            new MetasWithSameLabelSearchResultScenario(),
            new MetaCenterChildHavingGroupRelationScenario(),
            new ThreeLevelDeepGroupRelationScenario()
    );


    public void build(Injector injector) throws Exception {
        Transaction transaction = graphDb.beginTx();
        JSONObject jsonObject = new JSONObject();
        for(JsTestScenario scenario :scenarios){
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

    private String jsonKeyNameFromTestScenario(JsTestScenario testScenario){
        String className = testScenario.getClass().getSimpleName();
        String firstChar = (className.charAt(0) + "").toLowerCase();
        return firstChar + className.substring(1).replace("Scenario", "");
    }

}
