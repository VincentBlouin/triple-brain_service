/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package org.triple_brain.service;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.triple_brain.module.model.GraphTransaction;
import org.triple_brain.module.model.graph.GraphTransactional;

import javax.inject.Inject;

public class RestInterceptor implements MethodInterceptor {

    @Inject
    GraphTransaction graphTransaction;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        boolean isTransactional = invocation.getMethod().isAnnotationPresent(
                GraphTransactional.class
        );
        if(!isTransactional){
            return invocation.proceed();
        }
        Object state = graphTransaction.before();
        Object returnedObject;
        try{
            returnedObject = invocation.proceed();
        }catch(Exception e){
            throw e;
        }finally {
            graphTransaction.after(state);
        }
        return returnedObject;
    }
}
