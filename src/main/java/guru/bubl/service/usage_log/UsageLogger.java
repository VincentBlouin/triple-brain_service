/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.usage_log;

import guru.bubl.module.common_utils.NoEx;

import javax.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

public class UsageLogger {

    @Inject
    SQLConnection connection;

    public void log(LogEntry entry){
        NoEx.wrap(() -> {
            String query = "insert into usage_log(action_date, username, method, user_action) values(?, ?, ?, ?);";
            PreparedStatement stm = connection.getConnection().prepareStatement(
                    query
            );
            stm.setTimestamp(
                    1,
                    new Timestamp(entry.getDate().getTime())
            );
            stm.setString(2, entry.getUsername());
            stm.setString(3, entry.getMethod());
            stm.setString(4, entry.getAction());
            return stm.execute();
        }).get();
    }
}
