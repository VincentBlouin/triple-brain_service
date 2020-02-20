/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources;


import guru.bubl.service.resources.vertex.NotOwnedSurroundGraphResource;
import guru.bubl.service.resources.vertex.NotOwnedSurroundGraphResourceTest;
import guru.bubl.service.resources.vertex.VertexCollectionResourceTest;
import guru.bubl.service.resources.vertex.VertexMergeResourceTest;
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
