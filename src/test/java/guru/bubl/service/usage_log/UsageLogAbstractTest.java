/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.usage_log;

import com.google.inject.Guice;
import com.google.inject.Injector;
import guru.bubl.module.common_utils.NoExRun;
import org.junit.After;
import org.junit.Before;

import javax.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.assertTrue;

public class UsageLogAbstractTest {

    @Inject
    SQLConnection connection;

    @Before
    public void before(){
        Injector injector = Guice.createInjector(new UsageLogTestModule());
        injector.injectMembers(this);
        SQLConnection.clearDatabases();
        SQLConnection.createTables();
    }

    @After
    public final void after() throws SQLException {
        SQLConnection.closeConnection();
    }

    public LogEntry getLogEntryWithAction(String action) {
        return NoExRun.wrap(() -> {
            PreparedStatement stm = connection.getConnection().prepareStatement(
                    "SELECT * from usage_log where user_action=?"
            );
            stm.setString(1, action);
            ResultSet rs = stm.executeQuery();
            assertTrue(rs.next());
            return LogEntry.withDateUsernameAndAction(
                    rs.getDate(1),
                    rs.getString(2),
                    rs.getString(3)
            );
        }).get();
    }
}
