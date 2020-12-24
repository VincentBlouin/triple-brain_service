package guru.bubl.service.conf;

import com.google.gson.Gson;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import guru.bubl.module.model.ModelModule;
import guru.bubl.module.model.json.JsonUtils;
import guru.bubl.module.neo4j_graph_manipulator.graph.Neo4jModule;
import guru.bubl.module.neo4j_user_repository.Neo4jUserRepositoryModule;
import guru.bubl.service.RedisSessionHandler;
import guru.bubl.service.SessionHandler;
import guru.bubl.service.resources.*;
import guru.bubl.service.resources.center.CenterGraphElementsResourceFactory;
import guru.bubl.service.resources.center.PublicCenterGraphElementsResource;
import guru.bubl.service.resources.fork.ForkCollectionResourceFactory;
import guru.bubl.service.resources.friend.FriendListResource;
import guru.bubl.service.resources.friend.FriendsResourceFactory;
import guru.bubl.service.resources.group_relation.GroupRelationResourceFactory;
import guru.bubl.service.resources.notification.NotificationResource;
import guru.bubl.service.resources.notification.NotificationResourceFactory;
import guru.bubl.service.resources.pattern.PatternConsumerResourceFactory;
import guru.bubl.service.resources.relation.RelationResourceFactory;
import guru.bubl.service.resources.tag.TagResourceFactory;
import guru.bubl.service.resources.test.*;
import guru.bubl.service.resources.tree_copier.TreeCopierResourceFactory;
import guru.bubl.service.resources.vertex.GraphElementTagResourceFactory;
import guru.bubl.service.resources.vertex.VertexImageResourceFactory;
import guru.bubl.service.resources.vertex.VertexResourceFactory;
import guru.bubl.service.usage_log.UsageLogFilter;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.HashMap;
import java.util.Map;

import static guru.bubl.module.neo4j_graph_manipulator.graph.Neo4jModule.BOLT_PORT_FOR_PROD;

public class MRJerseyServletModule extends JerseyServletModule {

    public void doConfigureServlets() {
        bind(Context.class).to(InitialContext.class);
        bind(Gson.class).toInstance(JsonUtils.getGson());
        filter("*").through(UsageLogFilter.class);
        install(new Neo4jUserRepositoryModule());

        FactoryModuleBuilder builder = new FactoryModuleBuilder();

        bind(ConnectivityResource.class);
        bind(DailyJobResource.class);
        bind(UserResource.class);
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
                ForkCollectionResourceFactory.class
        ));
        install(builder.build(
                GraphElementTagResourceFactory.class
        ));
        install(builder.build(
                RelationResourceFactory.class
        ));
        install(builder.build(
                SearchResourceFactory.class
        ));
        install(builder.build(
                VertexImageResourceFactory.class
        ));
        install(builder.build(
                TagResourceFactory.class
        ));
        install(builder.build(
                FriendsResourceFactory.class
        ));
        install(builder.build(
                GroupRelationResourceFactory.class
        ));
        install(builder.build(
                TreeCopierResourceFactory.class
        ));
        install(builder.build(
                NotificationResourceFactory.class
        ));
        final Map<String, String> params = new HashMap<>();
        serve("/*").with(GuiceContainer.class, params);

        try {
            final InitialContext jndiContext = new InitialContext();
            bindConstant().annotatedWith(Names.named("AppUrl")).to(
                    (String) jndiContext.lookup("appUrl")
            );
            bindConstant().annotatedWith(Names.named("googleRecaptchaKey")).to(
                    (String) jndiContext.lookup("googleRecaptchaKey")
            );
            bindConstant().annotatedWith(Names.named("skipRecaptcha")).to(
                    "yes".equals(jndiContext.lookup("skipRecaptcha"))
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
                            Neo4jModule.withUserPasswordAndPort("neo4j", "proute", 8022) :
                            Neo4jModule.withUserPasswordAndPort(
                                    (String) jndiContext.lookup("dbUser"),
                                    (String) jndiContext.lookup("dbPassword"),
                                    BOLT_PORT_FOR_PROD
                            )
            );
            if (isTesting) {
                install(
                        ModelModule.forTesting()
                );
            } else {
                install(
                        new ModelModule(
                                (String) jndiContext.lookup("sendgrid_key")
                        )
                );
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
}
