/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.usage_log;

import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.servlet.RequestScoped;
import guru.bubl.service.resources.GraphManipulatorResourceUtils;

import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

@Singleton
public class UsageLogFilter implements Filter {

    @Inject
    UsageLogger usageLogger;

    @Inject
    private Provider<HttpServletRequest> requestProvider;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String username = GraphManipulatorResourceUtils.isUserInSession(requestProvider.get().getSession()) ?
                GraphManipulatorResourceUtils.userFromSession(
                        requestProvider.get().getSession()
                ).email():
                null;
        usageLogger.log(
                LogEntry.withDateUsernameAndAction(
                        new Date(),
                        username,
                        requestProvider.get().getRequestURI()
                )
        );
        filterChain.doFilter(servletRequest, servletResponse);

    }

    @Override
    public void destroy() {

    }
}
