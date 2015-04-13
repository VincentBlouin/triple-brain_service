/*
 * Copyright Vincent Blouin under the Mozilla Public License 1.1
 */

package js_test_data;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.neo4j.graphdb.GraphDatabaseService;
import org.triple_brain.module.model.ModelModule;
import org.triple_brain.module.model.graph.ModelTestScenarios;
import org.triple_brain.module.neo4j_graph_manipulator.graph.Neo4jModule;
import org.triple_brain.module.neo4j_search.Neo4jGraphSearchModule;

public class JsTestScenariosStarter {

    protected static Injector injector;

    public static void main(String[] args) throws Exception {
        injector = Guice.createInjector(
                Neo4jModule.forTestingUsingEmbedded(),
                new Neo4jGraphSearchModule(),
                new ModelModule(),
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        requireBinding(ModelTestScenarios.class);
                    }
                }
        );
        injector.injectMembers(JsTestScenariosBuilder.class);
        JsTestScenariosBuilder jsTestScenariosBuilder = injector.getInstance(
                JsTestScenariosBuilder.class
        );
        jsTestScenariosBuilder.build(injector);
        injector.getInstance(GraphDatabaseService.class).shutdown();
        Neo4jModule.clearDb();
    }

}
