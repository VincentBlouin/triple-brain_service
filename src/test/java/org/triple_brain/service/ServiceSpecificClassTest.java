/*
 * Copyright Vincent Blouin under the Mozilla Public License 1.1
 */

package org.triple_brain.service;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        UserPasswordResourceTest.class
})
public class ServiceSpecificClassTest extends ServiceTestRunner{}
