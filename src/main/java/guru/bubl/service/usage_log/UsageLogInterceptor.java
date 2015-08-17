/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.usage_log;

import guru.bubl.service.resources.GraphManipulatorResourceUtils;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import java.util.Date;

public class UsageLogInterceptor implements MethodInterceptor {

    @Inject
    UsageLogger usageLogger;

    @Inject
    private Provider<HttpServletRequest> requestProvider;


    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        String username = null;
        if (GraphManipulatorResourceUtils.isUserInSession(requestProvider.get().getSession())) {
            username = GraphManipulatorResourceUtils.userFromSession(
                    requestProvider.get().getSession()
            ).username();
        }
        usageLogger.log(
                LogEntry.withDateUsernameAndAction(
                        new Date(),
                        username,
                        methodInvocation.getMethod().getName()
                )
        );
        return methodInvocation.proceed();
    }

}
