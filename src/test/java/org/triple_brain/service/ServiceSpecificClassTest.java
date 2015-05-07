/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package org.triple_brain.service;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.triple_brain.service.vertex.VertexResourceTest;

@Ignore
@RunWith(Suite.class)
@Suite.SuiteClasses({
        VertexResourceTest.class
})
public class ServiceSpecificClassTest extends ServiceTestRunner {}
