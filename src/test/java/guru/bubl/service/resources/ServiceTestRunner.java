/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.client.apache.config.ApacheHttpClientConfig;
import com.sun.jersey.client.apache.config.DefaultApacheHttpClientConfig;
import guru.bubl.module.neo4j_graph_manipulator.graph.Neo4jModule;
import guru.bubl.service.Launcher;
import guru.bubl.service.utils.RestTestUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.notification.RunListener;

import java.net.URI;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
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
        clientConfig.getProperties().put(ApacheHttpClientConfig.PROPERTY_HANDLE_COOKIES, true);
        RestTestUtils.client = Client.create(clientConfig);
        RestTestUtils.resource = RestTestUtils.client.resource(RestTestUtils.BASE_URI);
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
