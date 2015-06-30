/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources;

import com.googlecode.junittoolbox.SuiteClasses;
import com.googlecode.junittoolbox.WildcardPatternSuite;
import org.junit.Ignore;
import org.junit.runner.RunWith;

@RunWith(WildcardPatternSuite.class)
@SuiteClasses("**/*Test.class")
public class ServiceTests extends ServiceTestRunner{}
