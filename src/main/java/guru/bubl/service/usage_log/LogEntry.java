/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.usage_log;

import java.util.Date;

public class LogEntry {
    private Date date;
    private String username;
    private String action;

    public static LogEntry withDateUsernameAndAction(Date date, String username, String action){
        return new LogEntry(
                date,
                username,
                action
        );
    }

    protected LogEntry(Date date, String username, String action){
        this.date = date;
        this.username = username;
        this.action = action;
    }

    public Date getDate() {
        return date;
    }

    public String getUsername() {
        return username;
    }

    public String getAction() {
        return action;
    }
}
