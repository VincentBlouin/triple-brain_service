/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources;

import guru.bubl.service.resources.center.CenterGraphElementResourceTest;
import guru.bubl.service.resources.center.PublicCenterGraphElementsResource;
import guru.bubl.service.resources.center.PublicCenterGraphElementsResourceTest;
import guru.bubl.service.resources.schema.SchemaPropertyResource;
import guru.bubl.service.resources.schema.SchemaPropertyResourceTest;
import guru.bubl.service.resources.vertex.VertexNonOwnedSurroundGraphResource;
import guru.bubl.service.resources.vertex.VertexNonOwnedSurroundGraphResourceTest;
import guru.bubl.service.resources.vertex.VertexSuggestionResource;
import guru.bubl.service.resources.vertex.VertexSuggestionResourceTest;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
@RunWith(Suite.class)
@Suite.SuiteClasses({
        SchemaPropertyResourceTest.class
})
public class ServiceSpecificClassTest extends ServiceTestRunner {
}
