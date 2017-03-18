/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service;

import guru.bubl.module.model.User;

import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;

public interface SessionHandler {

    String PERSISTENT_SESSION = "PERSISTENT_SESSION";

    default User userFromSession(HttpSession session){
        return (User) session.getAttribute(SecurityInterceptor.AUTHENTICATED_USER_KEY);
    }
    void removePersistentSession(String persistentSessionId);
    NewCookie persistSessionForUser(HttpSession session, User user);
    Boolean isUserInSession(HttpSession session, String persistentSessionId);
}
