/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.usage_log;

import org.junit.Test;

import javax.inject.Inject;
import java.util.Date;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class UsageLoggerTest extends UsageLogAbstractTest{

    @Inject
    UsageLogger usageLogger;

    @Test
    public void can_add_entry() {
        usageLogger.log(
                LogEntry.withDateUsernameMethodAndAction(
                        new Date(),
                        "some_user",
                        "POST",
                        "some_action"
                )
        );
        LogEntry entry = getLogEntryWithAction("some_action");
        assertThat(
                entry.getUsername(),
                is("some_user")
        );
    }
}
