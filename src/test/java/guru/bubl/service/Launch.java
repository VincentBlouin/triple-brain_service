/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service;

import guru.bubl.service.usage_log.H2DataSource;
import guru.bubl.service.usage_log.SQLConnection;

public class Launch {
    public static void before() {
        SQLConnection.createTablesUsingDataSource(
                new H2DataSource()
        );
    }

    public static void after() {
    }
}
