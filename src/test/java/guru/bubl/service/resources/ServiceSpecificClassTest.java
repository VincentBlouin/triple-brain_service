/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources;


import guru.bubl.service.resources.pattern.PatternConsumerResourceTest;
import guru.bubl.service.resources.pattern.PatternResourceTest;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
@Ignore
@RunWith(Suite.class)
@Suite.SuiteClasses({
        PatternResourceTest.class
})
public class ServiceSpecificClassTest extends ServiceTestRunner {
}
