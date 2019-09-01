/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources;

import com.google.inject.Guice;
import com.google.inject.Injector;
import guru.bubl.module.common_utils.NoEx;
import guru.bubl.service.usage_log.SQLConnection;
import guru.bubl.service.usage_log.UsageLogTestModule;
import guru.bubl.service.utils.GraphManipulationRestTestUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import javax.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@Ignore
public class UsageLogFilterTest extends GraphManipulationRestTestUtils {

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
        SQLConnection.clearDatabases();
        SQLConnection.createTables();
    }

    @Test
    public void logs_request(){
        assertFalse(
                hasLogEntryWithAction("/service/users/is_authenticated")
        );
        isUserAuthenticated(authCookie);
        assertTrue(
                hasLogEntryWithAction("/service/users/is_authenticated")
        );
    }

    @Test
    public void doest_not_duplicate_request_logs(){
        isUserAuthenticated(authCookie);
        assertThat(
                numberOfLogEntryWithAction("/service/users/is_authenticated"),
                is(1)
        );
    }

    @Test
    public void saves_request_method(){
        isUserAuthenticated(authCookie);
        assertThat(
                getMethodForAction("/service/users/is_authenticated"),
                is("GET")
        );
    }

    public String getMethodForAction(String action){
        return NoEx.wrap(()-> {
            PreparedStatement stm = connection.getConnection().prepareStatement(
                    "SELECT method from usage_log where user_action=?"
            );
            stm.setString(1, action);
            ResultSet rs = stm.executeQuery();
            rs.next();
            return rs.getString(1);
        }).get();
    }

    public boolean hasLogEntryWithAction(String action){
        return NoEx.wrap(()->{
            PreparedStatement stm = connection.getConnection().prepareStatement(
                    "SELECT * from usage_log where user_action=?"
            );
            stm.setString(1, action);
            ResultSet rs = stm.executeQuery();
            return rs.next();
        }).get();
    }


    public Integer numberOfLogEntryWithAction(String action){
        return NoEx.wrap(()->{
            PreparedStatement stm = connection.getConnection().prepareStatement(
                    "SELECT count(*) from usage_log where user_action=?"
            );
            stm.setString(1, action);
            ResultSet rs = stm.executeQuery();
            rs.next();
            return rs.getInt(1);
        }).get();
    }
}
