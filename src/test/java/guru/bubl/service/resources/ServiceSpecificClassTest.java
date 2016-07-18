/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources;

import guru.bubl.service.resources.center.CenterGraphElementResourceTest;
import guru.bubl.service.resources.center.PublicCenterGraphElementsResource;
import guru.bubl.service.resources.center.PublicCenterGraphElementsResourceTest;
import guru.bubl.service.resources.schema.SchemaNonOwnedResource;
import guru.bubl.service.resources.schema.SchemaNonOwnedResourceTest;
import guru.bubl.service.resources.schema.SchemaPropertyResource;
import guru.bubl.service.resources.schema.SchemaPropertyResourceTest;
import guru.bubl.service.resources.vertex.*;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@Ignore
@RunWith(Suite.class)
@Suite.SuiteClasses({
        EdgeResourceTest.class
})
public class ServiceSpecificClassTest extends ServiceTestRunner {
}
