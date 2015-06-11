/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.client.apache.config.DefaultApacheHttpClientConfig;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runner.notification.RunListener;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import guru.bubl.module.neo4j_graph_manipulator.graph.Neo4jModule;
import guru.bubl.service.utils.RestTestUtils;

import javax.mail.Message;
import javax.mail.Transport;
import java.net.URI;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.powermock.api.support.membermodification.MemberMatcher.method;
import static org.powermock.api.support.membermodification.MemberModifier.suppress;

@RunWith(PowerMockRunner.class)
@PrepareForTest(javax.mail.Transport.class)
public class ServiceTestRunner extends RunListener {

    @BeforeClass
    public static void beforeAllTests() throws Exception{
        Neo4jModule.clearDb();
        RestTestUtils.BASE_URI = new URI("http://localhost:8786");
        RestTestUtils.launcher = new Launcher(
                RestTestUtils.BASE_URI.getPort()
        );
        RestTestUtils.launcher.launch();

        DefaultApacheHttpClientConfig clientConfig = new DefaultApacheHttpClientConfig();
        clientConfig.getProperties().put(
                "com.sun.jersey.impl.client.httpclient.handleCookies",
                true
        );
        RestTestUtils.client = Client.create(clientConfig);
        RestTestUtils.resource = RestTestUtils.client.resource(RestTestUtils.BASE_URI);
    }

    @Before
    public void before(){
        suppress(method(Transport.class, "send", Message.class));
    }

    @AfterClass
    public static void afterAllTests() throws Exception{
//        closeSearchEngine();
        closeGraphDatabase();
        RestTestUtils.launcher.stop();
    }

    private static void closeGraphDatabase(){
        ClientResponse response = RestTestUtils.resource
                .path("service")
                .path("test")
                .path("graph")
                .path("server")
                .delete(ClientResponse.class);
        assertThat(response.getStatus(), is(200));
    }

//    private static void closeSearchEngine() {
//        ClientResponse response = RestTestUtils.resource
//                .path("service")
//                .path("test")
//                .path("search")
//                .path("close")
//                .get(ClientResponse.class);
//        assertThat(response.getStatus(), is(200));
//    }
}
