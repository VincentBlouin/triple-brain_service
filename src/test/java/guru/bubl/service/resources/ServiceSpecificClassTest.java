/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources;

import guru.bubl.service.resources.vertex.GraphElementIdentificationResourceTest;
import guru.bubl.service.resources.vertex.VertexGroupResource;
import guru.bubl.service.resources.vertex.VertexGroupResourceTest;
import guru.bubl.service.resources.vertex.VertexResourceTest;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        GraphElementIdentificationResourceTest.class
})
public class ServiceSpecificClassTest extends ServiceTestRunner {}
