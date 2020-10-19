// $Id: ExecutionResultTest.java 326 2007-09-21 18:43:13Z jg_hamburg $
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

import junit.framework.TestCase;
import junit.framework.TestFailure;
import junitx.ddtunit.resources.RunnerMonitor;

/**
 * Test different execution runs with different DDTTestResults.
 * 
 * @author jg
 */
public class ExecutionResultTest extends TestCase {
	private static final String LF = System.getProperty("line.separator");

	private DDTTestResult result;

	private String testResource;

	private String classId;

	/**
	 * Constructor of ExecutionResultTest
	 * 
	 * @param name
	 */
	public ExecutionResultTest(String name) {
		super(name);
	}

	/**
	 * Setup test environment
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	public void setUp() {
		result = new DDTTestResult();
		result.addListener(RunnerMonitor.getInstance());
	}

	/**
	 * Execute internal DDTTestCase with one method and three xml based tests,
	 * no execution exception.
	 * 
	 */
	public void testRunThreeMethodTestsNoProblem() {
		this.testResource = "ExecutionResultTest";
		this.classId = "ExecutionResultNoProblem";

		String testMethod = "testThreeTestsNoProblem";
		DDTExecutionTest test = new DDTExecutionTest(testMethod);

		test.run(result);
		assertEquals("Wrong number of methods executed", 1, result.runCount());
		assertEquals("No failures expected", 0, result.failureCount());
		assertEquals("No errors expected", 0, result.errorCount());

		//
		assertEquals("Wrong number of method tests.", 3, result
				.runMethodTestCount());
		assertEquals("No failures in method test execution expected.", 0,
				result.methodTestFailureCount());
		assertEquals("No errors in method test execution expected.", 0, result
				.methodTestErrorCount());
	}

	/**
	 * Execute internal DDTTestCase with one method and three xml based tests,
	 * one AssertionFailed exception.
	 * 
	 */
	public void testRunThreeMethodTestsOneAssertFailedException() {
		this.testResource = "ExecutionResultTest";
		this.classId = "ExecutionResultNoProblem";

		String testMethod = "testThreeTestsOneAssertFailed";
		DDTExecutionTest test = new DDTExecutionTest(testMethod);

		test.run(result);
		assertEquals("Wrong number of methods executed", 1, result.runCount());
		assertEquals("No errors expected", 0, result.errorCount());
		assertEquals("One failures expected", 1, result.failureCount());

		// check returned failure message
		String message = ((TestFailure) result.failures().nextElement())
				.exceptionMessage();
		String expected = "method testThreeTestsOneAssertFailed - Total: 3, Errors: 0, Failures: 1"
				+ LF
				+ "F-(my-second-testcase) Wrong isEqual assert on (multiline) string [position 0] expected:<my-unexpected-testcase> but was:<my-second-testcase>";
		assertEquals("Wrong exepction message in JUnit failure", expected,
				message);
		assertEquals("Wrong number of method tests.", 3, result
				.runMethodTestCount());
		assertEquals("No errors in method test execution expected.", 0, result
				.methodTestErrorCount());
		assertEquals("One failures in method test execution expected.", 1,
				result.methodTestFailureCount());
		message = ((TestFailure) result.methodTestFailures().next())
				.exceptionMessage();
		expected = "Wrong isEqual assert on (multiline) string [position 0] "
				+ "expected:<my-unexpected-testcase> but was:<my-second-testcase>";
		assertEquals("Wrong exception message of method test failure",
				expected, message);
	}

	/**
	 * Execute internal DDTTestCase with one method and three xml based tests,
	 * one AssertionFailed and one other exception.
	 * 
	 */
	public void testRunThreeMethodTestsOneAssertFailedOneException() {
		this.testResource = "ExecutionResultTest";
		this.classId = "ExecutionResultNoProblem";

		String testMethod = "testThreeTestsOneAssertFailedOneException";
		DDTExecutionTest test = new DDTExecutionTest(testMethod);

		test.run(result);
		assertEquals("Wrong number of methods executed", 1, result.runCount());
		assertEquals("One errors expected", 1, result.errorCount());
		assertEquals("One failures expected", 0, result.failureCount());
		assertEquals("Wrong number of method tests executed", 3, result
				.runMethodTestCount());
		assertEquals("One errors expected", 1, result.methodTestErrorCount());
		assertEquals("One failures expected", 1, result
				.methodTestFailureCount());

		// check returned error message
		String message = ((TestFailure) result.errors().nextElement())
				.exceptionMessage();
		String expected = "method testThreeTestsOneAssertFailedOneException - "
				+ "Total: 3, Errors: 1, Failures: 1"
				+ LF
				+ "E-(my-first-testcase) Throw expected exception"
				+ LF
				+ "F-(my-second-testcase) Wrong isEqual assert on (multiline) "
				+ "string [position 0] expected:<my-unexpected-testcase> but was:"
				+ "<my-second-testcase>";
		assertEquals("Wrong exception message in JUnit failure", expected,
				message);
		assertEquals("One failures in method test execution expected.", 1,
				result.methodTestFailureCount());
		message = (result.methodTestFailures().next()).exceptionMessage();
		expected = "Wrong isEqual assert on (multiline) string [position 0]"
				+ " expected:<my-unexpected-testcase> but was:<my-second-testcase>";
		assertEquals("Wrong exception message of method test failure",
				expected, message);
	}

	/**
	 * Internal DDTTestCase class for starting test execution. <br/>XML test
	 * resource and classId are parametrised using outer class fields.
	 * 
	 * @author jg
	 */
	private class DDTExecutionTest extends DDTTestCase {
		/**
		 * Instanciate DDTTestCase with method name to execute
		 * 
		 * @param name
		 */
		public DDTExecutionTest(String name) {
			super(name);
		}

		/**
		 * @see junitx.ddtunit.DDTTestCase#initTestData()
		 */
		protected void initContext() {
			initTestData(testResource, classId);
		}

		/**
		 * Run test method with three xml based tests and no exceptions expected
		 * during execution.
		 */
		public void testThreeTestsNoProblem() {
		}

		/**
		 * Run test method with three xml based tests and one assertino
		 * exception during execution
		 */
		public void testThreeTestsOneAssertFailed() {
			addObjectToAssert("result", getObject("objId"));
		}

		/**
		 * Run test method with three xml based tests and one assertion and one
		 * other exception during execution
		 */
		public void testThreeTestsOneAssertFailedOneException() {
			String value = (String) getObject("objId");

			addObjectToAssert("result", value);

			if ("my-first-testcase".equals(value)) {
				throw new DDTException("Throw expected exception");
			}
		}
	}
}