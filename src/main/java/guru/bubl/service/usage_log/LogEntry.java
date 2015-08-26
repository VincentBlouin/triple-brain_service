/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.usage_log;

import java.util.Date;

public class LogEntry {
    private Date date;
    private String username;
    private String action;
    private String method;

    public static LogEntry withDateUsernameMethodAndAction(Date date, String username, String method, String action){
        return new LogEntry(
                date,
                username,
                method,
                action
        );
    }

    protected LogEntry(Date date, String username, String method, String action){
        this.date = date;
        this.username = username;
        this.method = method;
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

    public String getMethod() {
        return method;
    }
}
