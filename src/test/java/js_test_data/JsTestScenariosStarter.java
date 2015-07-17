/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package js_test_data;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import guru.bubl.module.neo4j_graph_manipulator.graph.search.Neo4jGraphSearchModule;
import org.neo4j.graphdb.GraphDatabaseService;
import guru.bubl.module.model.ModelModule;
import guru.bubl.module.utils.ModelTestScenarios;
import guru.bubl.module.neo4j_graph_manipulator.graph.Neo4jModule;

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
