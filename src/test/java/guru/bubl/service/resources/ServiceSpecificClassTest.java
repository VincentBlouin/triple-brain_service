/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources;


import guru.bubl.service.resources.center.CenterGraphElementResourceTest;
import guru.bubl.service.resources.meta.IdentifierResourceTest;
import guru.bubl.service.resources.schema.SchemaPropertyResourceTest;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
@Ignore
@RunWith(Suite.class)
@Suite.SuiteClasses({
        CenterGraphElementResourceTest.class
})
public class ServiceSpecificClassTest extends ServiceTestRunner {
}
