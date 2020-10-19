// $Id: DDTTestCase.java 351 2008-08-14 20:20:56Z jg_hamburg $
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

import junit.framework.TestResult;

public interface IDDTTestCase {

	/**
	 * Run complete selected testmethod by generating a separate result und
	 * return this after execution. This contains all notification hooks to
	 * TestListener classes like TestRunner.
	 * 
	 * @return result of executed test
	 */
	public abstract TestResult run();

	/**
	 * Run complete selected testmethod by generating a separate result und
	 * return this after execution. This contains all notification hooks to
	 * TestListener classes like TestRunner.
	 * 
	 * @param result
	 *            object generated externally, by a testrunner e.g..
	 */
	public abstract void run(TestResult result);

	/**
	 * Run a bare method cycle as defined in JUnit. Here the testdata
	 * initialization is performed. Because every testmethod should be run under
	 * its own fixture the execution of setUp and tearDown is inside of a
	 * subroutine. These methods will be executed inside of around every test
	 * representation of xml testdata definition.
	 * 
	 * @throws Throwable
	 *             that might come up during testmethod execution.
	 */
	public abstract void runBare() throws Throwable;

	/**
	 * Count number of test datasets provided for method methodName. <br/>If
	 * dataset for this method is null, 1 will be returned (a standard JUnit
	 * method)
	 * 
	 * @return Count of tests under method methodName
	 */
	public abstract int countMethodTests();

	/**
	 * @return Information about actual run test
	 */
	public abstract String runInfo();

	/**
	 * @return   Returns the testName.
	 * @uml.property   name="testName"
	 */
	public abstract String getTestName();

	/**
	 * @param testName   The testName to set.
	 * @uml.property   name="testName"
	 */
	public abstract void setTestName(String testName);

	public abstract String getName();

}