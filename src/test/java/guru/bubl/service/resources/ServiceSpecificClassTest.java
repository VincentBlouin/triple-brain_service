/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources;


import guru.bubl.service.resources.fork.ForkCollectionResourceTest;
import guru.bubl.service.resources.tree_copier.TreeCopierResourceTest;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
@Ignore
@RunWith(Suite.class)
@Suite.SuiteClasses({
        NotificationResourceTest.class
})
public class ServiceSpecificClassTest extends ServiceTestRunner {
}
