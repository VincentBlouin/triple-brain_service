/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources;


import guru.bubl.service.resources.center.PublicCenterGraphElementsResourceTest;
import guru.bubl.service.resources.tag.TagResourceTest;
import guru.bubl.service.resources.tag.UserTagsResourceTest;
import guru.bubl.service.resources.vertex.*;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
@Ignore
@RunWith(Suite.class)
@Suite.SuiteClasses({
        UserSessionResourceTest.class
})
public class ServiceSpecificClassTest extends ServiceTestRunner {
}
