/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources;


import guru.bubl.service.resources.tag.TagResourceTest;
import guru.bubl.service.resources.vertex.OwnedSurroundGraphResouceTest;
import guru.bubl.test.module.model.graph.meta.TagOperatorTest;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
@Ignore
@RunWith(Suite.class)
@Suite.SuiteClasses({
        GraphResourceTest.class
})
public class ServiceSpecificClassTest extends ServiceTestRunner {
}
