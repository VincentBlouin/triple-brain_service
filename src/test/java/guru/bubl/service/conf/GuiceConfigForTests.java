package guru.bubl.service.conf;

import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.Guice;
import com.sun.jersey.guice.JerseyServletModule;
import guru.bubl.module.model.ModelTestModule;
import guru.bubl.module.model.test.GraphComponentTest;
import guru.bubl.module.neo4j_graph_manipulator.graph.test.GraphComponentTestNeo4j;
import guru.bubl.module.neo4j_graph_manipulator.graph.test.SetupNeo4jDatabaseForTests;
import guru.bubl.service.resources.test.*;
import org.neo4j.driver.Driver;

public class GuiceConfigForTests extends GuiceServletContextListener {

    @Override
    protected Injector getInjector() {
        return Guice.createInjector(
                new MRJerseyServletModule() {
                    @Override
                    protected void configureServlets() {
                        this.doConfigureServlets();
                        install(new ModelTestModule());
                        bind(ResourceForTests.class);
                        bind(VertexResourceTestUtils.class);
                        bind(EdgeResourceTestUtils.class);
                        bind(GraphResourceTestUtils.class);
                        bind(UserResourceTestUtils.class);
                        bind(PersistentSessionRestTestUtils.class);
                        bind(GraphComponentTest.class).to(GraphComponentTestNeo4j.class);
                    }
                }
        );
    }
}
