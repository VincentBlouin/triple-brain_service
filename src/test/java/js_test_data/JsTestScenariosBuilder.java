/*
 * Copyright Vincent Blouin under the Mozilla Public License 1.1
 */

package js_test_data;
import com.google.common.collect.Sets;
import com.google.inject.Injector;
import js_test_data.scenarios.*;
import org.apache.commons.io.FileUtils;
import org.codehaus.jettison.json.JSONObject;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.triple_brain.module.model.User;
import org.triple_brain.module.model.graph.GraphFactory;
import org.triple_brain.module.model.graph.UserGraph;
import org.triple_brain.module.model.test.GraphComponentTest;
import org.triple_brain.module.neo4j_graph_manipulator.graph.graph.Neo4jUserGraphFactory;

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
            new MergeBubbleGraphScenario(),
            new ThreeBubblesGraphScenario(),
            new GraphWithAnInverseRelationScenario(),
            new GraphWithSimilarRelationsScenario(),
            new OneBubbleHavingSuggestionsGraphScenario(),
            new KaraokeSchemaGraphScenario()
    );

    UserGraph userGraph;

    User user = User.withEmail(
            "roger.lamothe@example.org"
    ).setUsername("roger_lamothe");


    public void build(Injector injector) throws Exception {
        Transaction transaction = graphDb.beginTx();
        graphComponentTest.removeWholeGraph();
        userGraph = neo4jUserGraphFactory.withUser(user);
        JSONObject jsonObject = new JSONObject();
        for(JsTestScenario scenario :scenarios){
            injector.injectMembers(scenario);
            jsonObject.put(
                    jsonKeyNameFromTestScenario(scenario),
                    scenario.build()
            );
        }
        FileUtils.writeStringToFile(
                new File(
                        "src/test/java/js_test_data/js-test-data.json"
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
