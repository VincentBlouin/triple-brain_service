/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.usage_log;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

public class H2DataSource extends BasicDataSource {

    public H2DataSource() {
        super();
        setDriverClassName("org.h2.Driver");
        setUrl("jdbc:h2:mem:usage_log");
        setUsername("sa");
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException();
    }
}
