/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources;

import guru.bubl.service.resources.vertex.VertexPublicAccessResourceTest;
import guru.bubl.service.resources.vertex.VertexResourceTest;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
@Ignore
@RunWith(Suite.class)
@Suite.SuiteClasses({
        VertexPublicAccessResourceTest.class
})
public class ServiceSpecificClassTest extends ServiceTestRunner {
}
