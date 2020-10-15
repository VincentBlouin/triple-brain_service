/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.xml.XmlConfiguration;

import java.io.File;

public class Launcher {
    private Server server;

    private int port = 0;

    public Launcher() throws Exception {
        this(8080, false);
    }

    public Launcher(int port, Boolean isForServiceTests) throws Exception {
        this.port = port;
        server = new Server(port);
        HandlerCollection handlers = new HandlerCollection();

        WebAppContext wac = new WebAppContext("src/test/webapp", "/service");
        handlers.addHandler(wac);

        String fileName = isForServiceTests ? "jetty-web-service-tests.xml" : "jetty-web-local-dev.xml";
        XmlConfiguration conf = new XmlConfiguration(new File("src/test/" + fileName).toURI().toURL().openStream());
        conf.configure(wac);

        server.setHandler(handlers);
    }

    int getPort() {
        return port;
    }

    public void launch() throws Exception {

        server.start();
    }

    public void stop() throws Exception {

        server.stop();
    }
}
