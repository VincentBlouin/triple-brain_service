/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.usage_log;

import guru.bubl.module.common_utils.NoExRun;

import javax.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

public class UsageLogger {

    @Inject
    SQLConnection connection;

    public void log(LogEntry entry){
        NoExRun.wrap(() -> {
            String query = "insert into usage_log(action_date, username, user_action) values(?, ?, ?);";
            PreparedStatement stm = connection.getConnection().prepareStatement(
                    query
            );
            stm.setTimestamp(
                    1,
                    new Timestamp(entry.getDate().getTime())
            );
            stm.setString(2, entry.getUsername());
            stm.setString(3, entry.getAction());
            return stm.execute();
        }).get();
    }
}
