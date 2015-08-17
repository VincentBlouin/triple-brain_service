/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.usage_log;

import com.google.inject.AbstractModule;

public class UsageLogModule extends AbstractModule{

    private Boolean isTesting;

    public UsageLogModule(){
        this.isTesting = isTesting;
    }

    @Override
    protected void configure() {
        requestStaticInjection(SQLConnection.class);
    }
}
