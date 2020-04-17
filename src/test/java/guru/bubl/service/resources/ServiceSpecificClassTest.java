/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources;


import guru.bubl.service.resources.edge.EdgeResource;
import guru.bubl.service.resources.vertex.VertexMergeResourceTest;
import guru.bubl.service.resources.vertex.VertexResource;
import guru.bubl.service.resources.vertex.VertexResourceTest;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
@Ignore
@RunWith(Suite.class)
@Suite.SuiteClasses({
        VertexMergeResourceTest.class
})
public class ServiceSpecificClassTest extends ServiceTestRunner {
}
