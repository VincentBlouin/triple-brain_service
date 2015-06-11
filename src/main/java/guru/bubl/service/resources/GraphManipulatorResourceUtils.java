/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources;

import guru.bubl.service.SecurityInterceptor;
import guru.bubl.module.model.User;

import javax.servlet.http.HttpSession;
public class GraphManipulatorResourceUtils {
    public static User userFromSession(HttpSession session){
        return (User) session.getAttribute(SecurityInterceptor.AUTHENTICATED_USER_KEY);
    }

    public static Boolean isUserInSession(HttpSession session){
        return session.getAttribute(SecurityInterceptor.AUTHENTICATED_USER_KEY) != null;
    }
}
