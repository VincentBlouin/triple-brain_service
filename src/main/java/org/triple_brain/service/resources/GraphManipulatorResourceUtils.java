/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package org.triple_brain.service.resources;

import org.triple_brain.service.SecurityInterceptor;
import org.triple_brain.module.model.User;

import javax.servlet.http.HttpSession;
public class GraphManipulatorResourceUtils {
    public static User userFromSession(HttpSession session){
        return (User) session.getAttribute(SecurityInterceptor.AUTHENTICATED_USER_KEY);
    }

    public static Boolean isUserInSession(HttpSession session){
        return session.getAttribute(SecurityInterceptor.AUTHENTICATED_USER_KEY) != null;
    }
}
