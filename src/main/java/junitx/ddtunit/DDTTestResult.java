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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import junit.framework.AssertionFailedError;
import junit.framework.Protectable;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestFailure;
import junit.framework.TestListener;
import junit.framework.TestResult;
import junitx.ddtunit.util.DDTConfiguration;
import junitx.ddtunit.util.PrivilegedAccessor;

/**
 * Extends class {@link TestResult}of JUnit <br/>This class will contain extra
 * results from test execution based on xml data testcases.
 * 
 * @author jg
 */
public class DDTTestResult extends TestResult {
	/**
	 * Vector containing all caught failures during test execution
	 */
	protected List<TestFailure> fMethodTestFailures;

	/**
	 * Vector containing all errors caught during test execution
	 */
	protected List<TestFailure> fMethodTestErrors;

	/**
	 * Number of executed tests inside of a testmethod
	 */
	protected int fRunMethodTests;

	/**
	 * 
	 */
	public DDTTestResult() {
		super();
		fMethodTestFailures = new ArrayList<TestFailure>();
		fMethodTestErrors = new ArrayList<TestFailure>();
		fRunMethodTests = 0;

		if (DDTConfiguration.getInstance().isActiveRunMonitor()
				&& !this.cloneListeners().contains(DDTRunMonitor.getInstance())) {
			this.addListener(DDTRunMonitor.getInstance());
		}
	}

	/**
	 * Instanciate DDTTestResult by using JUnit TestResult as base info.
	 * 
	 * @param result
	 *            of JUnit TestResult
	 * @throws DDTException
	 */
	public DDTTestResult(TestResult result) throws DDTException {
		try {
			PrivilegedAccessor.setFieldValue(this, "fErrors",
					PrivilegedAccessor.getFieldValue(result, "fErrors"));
			PrivilegedAccessor.setFieldValue(this, "fFailures",
					PrivilegedAccessor.getFieldValue(result, "fFailures"));
			PrivilegedAccessor.setFieldValue(this, "fListeners",
					PrivilegedAccessor.getFieldValue(result, "fListeners"));
			this.fRunTests = ((Integer) PrivilegedAccessor.getFieldValue(
					result, "fRunTests")).intValue();
			fMethodTestFailures = new Vector<TestFailure>();
			fMethodTestErrors = new Vector<TestFailure>();
			fRunMethodTests = 0;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DDTException(
					"Error on transforming TestResult to DDTResult", e);
		}

		if (DDTConfiguration.getInstance().isActiveRunMonitor()
				&& !this.cloneListeners().contains(DDTRunMonitor.getInstance())) {
			this.addListener(DDTRunMonitor.getInstance());
		}
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param result
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public TestResult copyContent(TestResult result) {
		try {
			PrivilegedAccessor.setFieldValue(result, "fErrors",
					PrivilegedAccessor.getFieldValue(this, "fErrors"));
			PrivilegedAccessor.setFieldValue(result, "fFailures",
					PrivilegedAccessor.getFieldValue(this, "fFailures"));
			PrivilegedAccessor.setFieldValue(result, "fListeners",
					PrivilegedAccessor.getFieldValue(this, "fListeners"));
			PrivilegedAccessor.setFieldValue(result, "fRunTests", new Integer(
					this.fRunTests));
		} catch (Exception e) {
			e.printStackTrace();
			throw new DDTException(
					"Error on transforming TestResult to DDTResult", e);
		}

		return result;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 * 
	 * @throws DDTException
	 *             DOCUMENT ME!
	 */
	TestResult createTestResult() throws DDTException {
		TestResult result = new TestResult();

		try {
			PrivilegedAccessor.setFieldValue(result, "fError", this.fErrors);
			PrivilegedAccessor.setFieldValue(result, "fFailures",
					this.fFailures);
			PrivilegedAccessor.setFieldValue(result, "fListeners",
					this.fListeners);
			PrivilegedAccessor.setFieldValue(result, "fRunTests", new Integer(
					this.fRunTests));
		} catch (Exception e) {
			e.printStackTrace();
			throw new DDTException(
					"Error during conversion from DDTResult to TestResult");
		}

		return result;
	}

	/**
	 * Runs a TestCase.
	 * 
	 * @param test
	 *            to run
	 */
	protected void run(final TestCase test) {
		startTest(test);

		Protectable p = new Protectable() {
			public void protect() throws Throwable {
				test.runBare();
			}
		};

		runProtected(test, p);

		endTest(test);
	}

	/**
	 * Runs a TestCase.
	 * 
	 * @param test
	 *            to run
	 * @param protectedRunner
	 *            executes testmethod and and throws Throwable if any error
	 *            occures
	 */
	public void runProtected(final Test test, Protectable protectedRunner) {
		try {
			protectedRunner.protect();
		} catch (AssertionFailedError e) {
			addFailure(test, e);
		} catch (ThreadDeath e) { // don't catch ThreadDeath by accident
			throw e;
		} catch (Throwable e) {
			addError(test, e);
		}
	}

	/**
	 * Add error of a method test execution for later statistic
	 * 
	 * @param test
	 *            to run
	 * @param testName
	 *            of dataset actually processed in testmethod
	 * @param throwable
	 *            caught during execution of test
	 * 
	 * @see junitx.ddtunit.DDTTestListener#addMethodTestError(junit.framework.Test,
	 *      java.lang.String, java.lang.Throwable)
	 */
	public void addMethodTestError(Test test, String testName,
			Throwable throwable) {
		fMethodTestErrors.add(new DDTTestFailure(test, testName, throwable));

		for (Iterator e = cloneListeners().listIterator(); e.hasNext();) {
			TestListener listener = (TestListener) e.next();

			if (DDTTestListener.class.isInstance(listener)) {
				((DDTTestListener) listener).addMethodTestError(test, testName,
						throwable);
			}
		}
	}

	/**
	 * Add assertion failure of a method test execution for later statistic
	 * 
	 * @param test
	 *            to run
	 * @param testName
	 *            of dataset actually processed in testmethod
	 * @param assertError
	 *            caught during execution of test
	 * 
	 * @see junitx.ddtunit.DDTTestListener#addMethodTestFailure(junit.framework.Test,
	 *      java.lang.String, junit.framework.AssertionFailedError)
	 */
	public void addMethodTestFailure(Test test, String testName,
			AssertionFailedError assertError) {
		fMethodTestFailures
				.add(new DDTTestFailure(test, testName, assertError));

		for (Iterator e = cloneListeners().listIterator(); e.hasNext();) {
			TestListener listener = (TestListener) e.next();

			if (DDTTestListener.class.isInstance(listener)) {
				((DDTTestListener) listener).addMethodTestFailure(test,
						testName, assertError);
			}
		}
	}

	/**
	 * Notify about end of method test
	 * 
	 * @param test
	 *            to run
	 * @param methodName
	 *            of actually processed method
	 * 
	 * @see junitx.ddtunit.DDTTestListener#endMethodTest(junitx.ddtunit.DDTTestCase,
	 *      java.lang.String)
	 */
	public void endMethodTest(IDDTTestCase test, String methodName) {
		for (Iterator e = cloneListeners().listIterator(); e.hasNext();) {
			TestListener listener = (TestListener) e.next();

			if (DDTTestListener.class.isInstance(listener)) {
				((DDTTestListener) listener).endMethodTest(test, methodName);
			}
		}
	}

	/**
	 * Notify about start of method test
	 * 
	 * @param test
	 *            to run
	 * @param methodName
	 *            of actually processed method
	 * 
	 * @see junitx.ddtunit.DDTTestListener#startMethodTest(junitx.ddtunit.DDTTestCase,
	 *      java.lang.String)
	 */
	public void startMethodTest(IDDTTestCase test, String methodName) {
		final int count = 1;

		synchronized (this) {
			fRunMethodTests += count;
		}

		for (Iterator e = cloneListeners().listIterator(); e.hasNext();) {
			TestListener listener = (TestListener) e.next();

			if (DDTTestListener.class.isInstance(listener)) {
				((DDTTestListener) listener).startMethodTest(test, methodName);
			}
		}
	}

	/**
	 * Returns a copy of the TestListeners
	 * 
	 * @return Vector of TestListeners
	 */
	private synchronized List<TestListener> cloneListeners() {
		List<TestListener> newListeners = new ArrayList<TestListener>();
		try {
			newListeners.addAll((List<TestListener>) PrivilegedAccessor
					.getFieldValue(this, "fListeners"));
		} catch (IllegalAccessException ex) {
			throw new DDTSetUpException("Error preparing listeners", ex);
		} catch (NoSuchFieldException ex) {
			throw new DDTSetUpException("Error preparing listeners", ex);
		}
		return newListeners;
	}

	/**
	 * @return number of xml based tests run
	 */
	public int runMethodTestCount() {
		return fRunMethodTests;
	}

	/**
	 * @return number of method test failures were caught during execution
	 */
	public int methodTestFailureCount() {
		return fMethodTestFailures.size();
	}

	/**
	 * @return number of method test errors were caught during execution
	 */
	public int methodTestErrorCount() {
		return fMethodTestErrors.size();
	}

	public ListIterator<TestFailure> methodTestFailures() {
		ListIterator<TestFailure> iter = this.fMethodTestFailures
				.listIterator();
		return iter;
	}

}