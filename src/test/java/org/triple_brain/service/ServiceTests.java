/*
 * Copyright Vincent Blouin under the Mozilla Public License 1.1
 */

package org.triple_brain.service;

import com.googlecode.junittoolbox.SuiteClasses;
import com.googlecode.junittoolbox.WildcardPatternSuite;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.client.apache.config.DefaultApacheHttpClientConfig;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runner.notification.RunListener;
import org.triple_brain.module.neo4j_graph_manipulator.graph.Neo4jModule;
import org.triple_brain.service.utils.RestTestUtils;

import java.net.URI;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(WildcardPatternSuite.class)
@SuiteClasses("**/*Test.class")
public class ServiceTests extends RunListener {
    @BeforeClass
    public static void doYourOneTimeSetup() throws Exception{
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

    @AfterClass
    public static void doYourOneTimeTeardown() throws Exception{
        closeSearchEngine();
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

    private static void closeSearchEngine() {
        ClientResponse response = RestTestUtils.resource
                .path("service")
                .path("test")
                .path("search")
                .path("close")
                .get(ClientResponse.class);
        assertThat(response.getStatus(), is(200));
    }
}
