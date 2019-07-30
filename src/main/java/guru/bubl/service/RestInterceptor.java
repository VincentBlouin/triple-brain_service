/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import guru.bubl.module.model.GraphTransaction;
import guru.bubl.module.model.graph.GraphTransactional;

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
            e.printStackTrace();
            throw e;
        }finally {
            graphTransaction.after(state);
        }
        return returnedObject;
    }
}
