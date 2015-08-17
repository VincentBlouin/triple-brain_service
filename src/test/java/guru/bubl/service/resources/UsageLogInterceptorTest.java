/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources;

import com.google.inject.Guice;
import com.google.inject.Injector;
import guru.bubl.module.common_utils.NoExRun;
import guru.bubl.service.usage_log.SQLConnection;
import guru.bubl.service.usage_log.UsageLogTestModule;
import guru.bubl.service.utils.GraphManipulationRestTestUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UsageLogInterceptorTest extends GraphManipulationRestTestUtils {

    @Inject
    SQLConnection connection;

    private static Injector injector;

    @BeforeClass
    public static void beforeClass(){
        injector = Guice.createInjector(
                new UsageLogTestModule()
        );
        SQLConnection.clearDatabases();
        SQLConnection.createTables();
    }

    @Before
    public void before(){
        injector.injectMembers(this);
    }

    @Test
    public void logs_request(){
        assertFalse(
                hasLogEntryWithAction("isAuthenticated")
        );
        isUserAuthenticated(authCookie);
        assertTrue(
                hasLogEntryWithAction("isAuthenticated")
        );
    }

    public boolean hasLogEntryWithAction(String action){
        return NoExRun.wrap(()->{
            PreparedStatement stm = connection.getConnection().prepareStatement(
                    "SELECT * from usage_log where user_action=?"
            );
            stm.setString(1, action);
            ResultSet rs = stm.executeQuery();
            return rs.next();
        }).get();
    }
}
