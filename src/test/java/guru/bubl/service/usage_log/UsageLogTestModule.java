/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.usage_log;

import com.google.inject.AbstractModule;

import javax.sql.DataSource;

public class UsageLogTestModule extends AbstractModule{

    @Override
    protected void configure() {
        install(new UsageLogModule());
        bind(DataSource.class)
                .toInstance(new H2DataSource());
    }
}
