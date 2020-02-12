/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.conf;

import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;
import com.google.inject.servlet.GuiceServletContextListener;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import guru.bubl.module.model.ModelModule;
import guru.bubl.module.model.ModelTestModule;
import guru.bubl.module.model.json.JsonUtils;
import guru.bubl.module.neo4j_graph_manipulator.graph.Neo4jModule;
import guru.bubl.module.neo4j_user_repository.Neo4jUserRepositoryModule;
import guru.bubl.service.RedisSessionHandler;
import guru.bubl.service.SessionHandler;
import guru.bubl.service.resources.*;
import guru.bubl.service.resources.center.CenterGraphElementsResourceFactory;
import guru.bubl.service.resources.center.PublicCenterGraphElementsResource;
import guru.bubl.service.resources.edge.EdgeResourceFactory;
import guru.bubl.service.resources.fork.ForkResourceFactory;
import guru.bubl.service.resources.friend.FriendListResource;
import guru.bubl.service.resources.friend.FriendsResourceFactory;
import guru.bubl.service.resources.pattern.PatternConsumerResourceFactory;
import guru.bubl.service.resources.schema.SchemaNonOwnedResourceFactory;
import guru.bubl.service.resources.schema.SchemaPropertyResourceFactory;
import guru.bubl.service.resources.schema.SchemaResourceFactory;
import guru.bubl.service.resources.schema.SchemasResource;
import guru.bubl.service.resources.tag.TagResourceFactory;
import guru.bubl.service.resources.tag.UserTagsResourceFactory;
import guru.bubl.service.resources.test.*;
import guru.bubl.service.resources.vertex.*;
import guru.bubl.service.usage_log.UsageLogFilter;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.HashMap;
import java.util.Map;

public class GuiceConfig extends GuiceServletContextListener {

    @Override
    protected Injector getInjector() {
        return Guice.createInjector(new JerseyServletModule() {
            @Override
            protected void configureServlets() {
                bind(Context.class).to(InitialContext.class);
                bind(Gson.class).toInstance(JsonUtils.getGson());
                filter("*").through(UsageLogFilter.class);
                install(new Neo4jUserRepositoryModule());

                FactoryModuleBuilder builder = new FactoryModuleBuilder();

                bind(DailyJobResource.class);
                bind(UserResource.class);
                bind(SchemasResource.class);
                bind(ResetPasswordResource.class);
                bind(SessionHandler.class).to(RedisSessionHandler.class);
                bind(RedisSessionHandler.class);
                bind(PublicCenterGraphElementsResource.class);
                bind(FriendListResource.class);

                install(builder.build(
                        CenterGraphElementsResourceFactory.class
                ));
                install(builder.build(
                        GraphResourceFactory.class
                ));
                install(builder.build(
                        PatternConsumerResourceFactory.class
                ));
                install(builder.build(
                        VertexResourceFactory.class
                ));
                install(builder.build(
                        VertexSuggestionResourceFactory.class
                ));
                install(builder.build(
                        VertexCollectionResourceFactory.class
                ));
                install(builder.build(
                        GraphElementTagResourceFactory.class
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
                        SchemaResourceFactory.class
                ));
                install(builder.build(
                        TagResourceFactory.class
                ));
                install(builder.build(
                        UserTagsResourceFactory.class
                ));
                install(builder.build(
                        SchemaPropertyResourceFactory.class
                ));
                install(builder.build(
                        SchemaNonOwnedResourceFactory.class
                ));
                install(builder.build(
                        ForkResourceFactory.class
                ));
                install(builder.build(
                        FriendsResourceFactory.class
                ));
                final Map<String, String> params = new HashMap<>();
                params.put("com.sun.jersey.api.json.POJOMappingFeature", "true");
                serve("/*").with(GuiceContainer.class, params);

                try {
                    final InitialContext jndiContext = new InitialContext();
                    bindConstant().annotatedWith(Names.named("AppUrl")).to(
                            (String) jndiContext.lookup("appUrl")
                    );
                    String isTestingStr = (String) jndiContext.lookup("is_testing");
                    Boolean isTesting = "yes".equals(isTestingStr);
                    install(
                            isTesting ?
                                    PersistentSessionModule.toTest() :
                                    PersistentSessionModule.forProduction(
                                            (String) jndiContext.lookup("redisUri")
                                    )

                    );
                    bind(Boolean.class)
                            .annotatedWith(Names.named("isTesting")).toInstance(isTesting);
                    install(
                            isTesting ?
                                    Neo4jModule.forTestingUsingEmbedded() :
                                    Neo4jModule.notForTestingUsingEmbedded(
                                            (String) jndiContext.lookup("dbUser"),
                                            (String) jndiContext.lookup("dbPassword")
                                    )
                    );
                    if (isTesting) {
                        install(
                                ModelModule.forTesting()
                        );
                        install(
                                new ModelTestModule()
                        );
                    } else {
                        install(
                                new ModelModule(
                                        (String) jndiContext.lookup("sendgrid_key")
                                )
                        );
                    }

                    if (isTesting) {
                        //security flaw if binded in production
                        bind(ResourceForTests.class);
                        bind(VertexResourceTestUtils.class);
                        bind(EdgeResourceTestUtils.class);
                        bind(GraphResourceTestUtils.class);
                        bind(UserResourceTestUtils.class);
                        bind(PersistentSessionRestTestUtils.class);
                    }
//                    bind(DataSource.class)
//                            .toProvider(fromJndi(DataSource.class, "jdbc/usageLog"));
//                    install(new UsageLogModule());
//                    if (isTesting) {
//                        SQLConnection.createTablesUsingDataSource(
//                                new H2DataSource()
//                        );
//                    }
                } catch (NamingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
