/*
 * Copyright Vincent Blouin under the Mozilla Public License 1.1
 */

package org.triple_brain.service.conf;

import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;
import com.google.inject.servlet.GuiceServletContextListener;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import org.triple_brain.module.neo4j_graph_manipulator.graph.Neo4jModule;
import org.triple_brain.module.repository_sql.SQLModule;
import org.triple_brain.module.solr_search.SolrSearchModule;
import org.triple_brain.service.RestInterceptor;
import org.triple_brain.service.resources.*;
import org.triple_brain.service.resources.schema.SchemaNonOwnedResourceFactory;
import org.triple_brain.service.resources.schema.SchemaPropertyResourceFactory;
import org.triple_brain.service.resources.schema.SchemaResourceFactory;
import org.triple_brain.service.resources.test.*;
import org.triple_brain.service.resources.vertex.*;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.ws.rs.Path;
import java.util.HashMap;
import java.util.Map;

import static com.google.inject.jndi.JndiIntegration.fromJndi;

public class GuiceConfig extends GuiceServletContextListener {

    @Override
    protected Injector getInjector() {
        return Guice.createInjector(new JerseyServletModule() {
            @Override
            protected void configureServlets() {
                bind(Context.class).to(InitialContext.class);
                bind(Gson.class).toInstance(new Gson());
                RestInterceptor restInterceptor = new RestInterceptor();
                requestInjection(restInterceptor);

                bindInterceptor(Matchers.any(), Matchers.annotatedWith(Path.class),
                        restInterceptor);

                install(new SQLModule());

                FactoryModuleBuilder builder = new FactoryModuleBuilder();

                bind(UserResource.class);
                bind(ServerConfigResource.class);

                install(builder.build(
                        GraphResourceFactory.class
                ));
                install(builder.build(
                        VertexResourceFactory.class
                ));
                install(builder.build(
                        VertexSuggestionResourceFactory.class
                ));
                install(builder.build(
                        VertexPublicAccessResourceFactory.class
                ));
                install(builder.build(
                        VertexCollectionPublicAccessResourceFactory.class
                ));
                install(builder.build(
                        GraphElementIdentificationResourceFactory.class
                ));
                install(builder.build(
                        EdgeResourceFactory.class
                ));
                install(builder.build(
                        SearchResourceFactory.class
                ));
                install(builder.build(
                        VertexImageResourceFactory.class
                ));
                install(builder.build(
                        VertexGroupResourceFactory.class
                ));
                install(builder.build(
                        SchemaResourceFactory.class
                ));
                install(builder.build(
                        SchemaPropertyResourceFactory.class
                ));
                install(builder.build(
                        SchemaNonOwnedResourceFactory.class
                ));

                final Map<String, String> params = new HashMap<>();
                params.put("com.sun.jersey.api.json.POJOMappingFeature", "true");
                serve("/*").with(GuiceContainer.class, params);

                bind(DataSource.class)
                        .annotatedWith(Names.named("nonRdfDb"))
                        .toProvider(fromJndi(DataSource.class, "jdbc/nonRdfTripleBrainDB"));
                try {
                    final InitialContext jndiContext = new InitialContext();
                    String isTestingStr = (String) jndiContext.lookup("is_testing");
                    Boolean isTesting = "yes".equals(isTestingStr);
                    bind(Boolean.class)
                            .annotatedWith(Names.named("isTesting")).toInstance(isTesting);
                    install(
                            isTesting ?
                                    Neo4jModule.forTestingUsingEmbedded() :
                                    Neo4jModule.notForTestingUsingEmbedded()
                    );
                    SolrSearchModule searchModule;
                    if (isTesting) {
                        searchModule = new SolrSearchModule(isTesting);
                    } else {
                        String solrHomePath = (String) jndiContext.lookup("solr_home_path");
                        String solrXMLPath = (String) jndiContext.lookup("solr_xml_path_relative_to_home");
                        searchModule = isTesting ?
                                new SolrSearchModule(isTesting) :
                                new SolrSearchModule(
                                        isTesting,
                                        solrHomePath,
                                        solrXMLPath
                                );
                    }
                    install(searchModule);
                    if (isTesting) {
                        bind(ResourceForTests.class);
                        bind(VertexResourceTestUtils.class);
                        bind(EdgeResourceTestUtils.class);
                        bind(GraphResourceTestUtils.class);
                        bind(UserResourceTestUtils.class);
                    }
                } catch (NamingException e) {
                    throw new RuntimeException(e);
                }

            }
        });
    }
}
