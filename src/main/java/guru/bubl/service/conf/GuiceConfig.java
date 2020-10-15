/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.conf;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

public class GuiceConfig extends GuiceServletContextListener {

    @Override
    protected Injector getInjector() {
        return Guice.createInjector(
                new MRJerseyServletModule() {
                    @Override
                    protected void configureServlets() {
                        this.doConfigureServlets();
                    }
                }
        );
    }
}
