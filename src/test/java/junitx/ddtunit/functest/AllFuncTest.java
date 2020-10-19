//$Id: AllFuncTest.java 345 2008-02-20 23:30:51Z jg_hamburg $
/********************************************************************************
 * DDTUnit, a Datadriven Approach to Unit- and Moduletesting
 * Copyright (c) 2004, Joerg and Kai Gellien
 * All rights reserved.
 *
 * The Software is provided under the terms of the Common Public License 1.0
 * as provided with the distribution of DDTUnit in the file cpl-v10.html.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *     + Redistributions of source cwwde must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     + Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 *
 *     + Neither the name of the authors or DDTUnit, nor the
 *       names of its contributors may be used to endorse or promote
 *       products derived from this software without specific prior
 *       written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ********************************************************************************/
package junitx.ddtunit.functest;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * @author jg
 */
public class AllFuncTest extends TestCase {

	public static void main(String[] argv) {
		TestRunner.run(AllFuncTest.class);
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for junitx.ddtunit.functest");
		// $JUnit-BEGIN$
		suite.addTest(ProcessStringDataTest.suite());
		suite.addTest(ProcessGenericValuesTest.suite());
		suite.addTestSuite(ProcessMapTypeTest.class);
		suite.addTest(ProcessExpectedExceptionErrorTest.suite());
		suite.addTest(ProcessExpectedExceptionTest.suite());
		suite.addTestSuite(ProcessConstantValuesTest.class);
		suite.addTestSuite(ProcessConstructorValuesTest.class);
		suite.addTestSuite(ProcessCollectionTypeTest.class);
		suite.addTestSuite(ProcessSetTypeTest.class);
		suite.addTestSuite(ProcessArrayDataTest.class);
		suite.addTestSuite(ProcessDateDataTest.class);
		suite.addTestSuite(ProcessComparableDataTest.class);
		suite.addTestSuite(ProcessObjectReferenceTest.class);
		suite.addTestSuite(ProcessBeansValuesTest.class);
		// $JUnit-END$
		return suite;
	}
}
