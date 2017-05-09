/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources;

import guru.bubl.service.resources.meta.UserMetasResourceTest;
import guru.bubl.service.resources.sort.GraphElementSortResourceTest;
import guru.bubl.service.resources.vertex.*;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
@Ignore
@RunWith(Suite.class)
@Suite.SuiteClasses({
        UserMetasResourceTest.class
})
public class ServiceSpecificClassTest extends ServiceTestRunner {
}
