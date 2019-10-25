/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources;


import guru.bubl.service.resources.center.CenterGraphElementsResourceTest;
import guru.bubl.service.resources.center.PublicCenterGraphElementsResourceTest;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
@Ignore
@RunWith(Suite.class)
@Suite.SuiteClasses({
        PublicCenterGraphElementsResourceTest.class
})
public class ServiceSpecificClassTest extends ServiceTestRunner {
}
