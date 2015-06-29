/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import guru.bubl.service.resources.vertex.VertexResourceTest;

@Ignore
@RunWith(Suite.class)
@Suite.SuiteClasses({
        VertexResourceTest.class
})
public class ServiceSpecificClassTest extends ServiceTestRunner {}
