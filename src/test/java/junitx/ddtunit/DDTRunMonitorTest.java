//$Id: DDTRunMonitorTest.java 280 2007-07-19 21:54:00Z jg_hamburg $
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
 *     + Redistributions of source code must retain the above copyright
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
package junitx.ddtunit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import junitx.ddtunit.report.DDTTextTestRunner;

public class DDTRunMonitorTest extends TestCase {

	private static DDTRunMonitor monitor;

	protected void setUp() throws Exception {
		monitor = DDTRunMonitor.getInstance();
	}

	public void testRunStandardTestCase() throws Exception {
		TestResult result = TestRunner.run(MyTest.suite());
		assertEquals("Wrong number of testmethods", 2, result.runCount());
		assertEquals("Wrong number of errors", 1, result.errorCount());
		assertEquals("Wrong number of failures", 0, result.failureCount());
	}

	public void testRunDDTTestCase() {
		TestRunner runner = new DDTTextTestRunner();
		runner.setPrinter(DDTRunMonitor.getInstance());
		DDTTestResult result = (DDTTestResult) runner.doRun(MyDDTTest.suite());
		assertEquals("Wrong number of testmethods", 2, result.runCount());
		assertEquals("Wrong number of total testcases", 5, result
				.runMethodTestCount());
		assertEquals("Wrong number of errors", 0, result.errorCount());
		assertEquals("Wrong number of failures", 2, result.failureCount());
		assertEquals("Wrong number of failures of testcases per methods", 3,
				result.methodTestFailureCount());
	}

	public static class MyTest extends TestCase {
		public MyTest(String name) {
			super(name);
		}

		public static Test suite() {
			return new TestSuite(MyTest.class);
		}

		protected void setUp() throws Exception {
			monitor = DDTRunMonitor.getInstance();
		}

		public void testCorrectMethod() throws Exception {
			assertTrue("Should allways be true", true);
		}

		public void testWrongMethod() throws Exception {
			throw new RuntimeException("Test failed expectedly");
		}
	}

	public static class MyDDTTest extends DDTTestCase {
		public MyDDTTest(String name) {
			super(name);
		}

		public static Test suite() {
			return new TestSuite(MyDDTTest.class);
		}

		protected void setUp() throws Exception {
			monitor = DDTRunMonitor.getInstance();
		}

		public void testZCorrectMethod() throws Exception {
			addObjectToAssert("result", getObject("myObj"));
		}

		public void testWrongMethod() throws Exception {
			fail("Should allways fail");
		}

		protected void initContext() {
			initTestData("DDTRunMonitorTest", "DDTRunMonitorTest");
		}
	}

	public void testGetInstance() {
		DDTRunMonitor monitor = DDTRunMonitor.getInstance();
		assertNotNull(monitor);
		assertSame("There should be a singleton", monitor, DDTRunMonitor
				.getInstance());
	}
}
