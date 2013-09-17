package org.triple_brain.service;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.triple_brain.module.model.BeforeAfterEachRestCall;
import org.triple_brain.module.model.graph.GraphTransactional;

import javax.inject.Inject;

/*
* Copyright Mozilla Public License 1.1
*/
public class RestInterceptor implements MethodInterceptor {

    @Inject
    BeforeAfterEachRestCall beforeAfterEachRestCall;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        boolean isTransactional = invocation.getMethod().isAnnotationPresent(
                GraphTransactional.class
        );
        if(!isTransactional){
            return invocation.proceed();
        }
        Object state = beforeAfterEachRestCall.before();
        Object returnedObject;
        try{
            returnedObject = invocation.proceed();
        }catch(Exception e){
            throw e;
        }finally {
            beforeAfterEachRestCall.after(state);
        }
        return returnedObject;
    }
}
