// $Id: DDTTestCase.java 355 2009-03-29 12:08:01Z jg_hamburg $
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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junitx.ddtunit.data.AssertObject;
import junitx.ddtunit.data.DDTDataRepository;
import junitx.ddtunit.data.DDTTestDataException;
import junitx.ddtunit.data.ObjectAsserter;
import junitx.ddtunit.data.ResourceNameFactory;
import junitx.ddtunit.data.TestClusterDataSet;
import junitx.ddtunit.data.TypedObject;
import junitx.ddtunit.data.TypedObjectMap;
import junitx.ddtunit.util.ClassAnalyser;
import junitx.ddtunit.util.DDTConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is derived from {@link TestCase}from JUnit. <br/>It will
 * implement all neccessary features to run tests based on xml parameter data.
 * 
 * @author jg
 */
abstract public class DDTTestCase extends TestCase implements IDDTTestCase {
	private static final String LF = System.getProperty("line.separator");

	private Logger log = LoggerFactory.getLogger(DDTTestCase.class);

	TestClusterDataSet classDataSet;

	private String testName;

	private DDTTestResult testResult;

	private ExceptionHandler exHandler;

	private TypedObjectMap assertMap;

	private StringBuffer assertMessages;

	/**
	 * 
	 */
	public DDTTestCase() {
		super();
		this.assertMessages = new StringBuffer();
	}

	/**
	 * @param name
	 *            of testmethod to execute
	 */
	public DDTTestCase(String name) {
		super(name);
		this.assertMessages = new StringBuffer();
	}

	/**
	 * Run complete selected testmethod by generating a separate result und
	 * return this after execution. This contains all notification hooks to
	 * TestListener classes like TestRunner.
	 * 
	 * @return result of executed test
	 */
	public TestResult run() {
		try {
			log.debug("run() - START");

			DDTTestResult result = new DDTTestResult();

			this.testResult = result;
			run(result);

			return result;
		} finally {
			log.debug("run() - END");
		}
	}

	/**
	 * Run complete selected testmethod by generating a separate result und
	 * return this after execution. This contains all notification hooks to
	 * TestListener classes like TestRunner.
	 * 
	 * @param result
	 *            object generated externally, by a testrunner e.g..
	 */
	public void run(TestResult result) {
		log.debug("run(TestResult) - START");

		DDTTestResult ddtResult;

		if (DDTTestResult.class.isInstance(result)) {
			ddtResult = (DDTTestResult) result;
		} else {
			ddtResult = new DDTTestResult(result);
		}

		this.testResult = ddtResult;
		ddtResult.run(this);

		if (!DDTTestResult.class.isInstance(result)) {
			ddtResult.copyContent(result);
		}

		log.debug("run(TestResult) - END");
	}

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
	public void runBare() throws Throwable {
		log.debug("runBare() - START");
		DDTConfiguration.getInstance().load();
		// initialize xml testdata
		initContext();

		try {
			runMethodTest();
		} finally {
		}

		log.debug("runBare() - END");
	}

	/**
	 * Retrieve object with specified identifier on a per method-test basis.
	 * <br/>If no data exists an exception will be raised.
	 * 
	 * @param objectId
	 *            specifies key for retrieval
	 * 
	 * @return Object that is stored under identifier key
	 */
	protected Object getObject(String objectId) {
		Object obj = null;
		TypedObject typedObject;

		if (!this.classDataSet.containsKey(this.getName())) {
			throw new DDTException("No objects defined <" + objectId
					+ "> in method scope");
		}

		typedObject = this.classDataSet.getObject(this.getName(), this
				.getTestName(), objectId);

		if (typedObject == null) {
			throw new DDTTestDataException(
					"Error retrieving testdata, could not find object("
							+ objectId + ")");
		} else {
			obj = typedObject.getValue();
		}
		return obj;
	}

	/**
	 * Retrieve object with specified identifier on a per method-test basis.
	 * <br/>If no data exists an exception will be raised.
	 * 
	 * @param objectId
	 *            specifies key for retrieval
	 * 
	 * @return Object that is stored under identifier key
	 */
	protected Object getObject(String objectId, String objectType) {
		Object obj = null;
		TypedObject typedObject;

		if (!this.classDataSet.containsKey(this.getName())) {
			throw new DDTException("No objects defined <" + objectId
					+ "> in method scope");
		}

		typedObject = this.classDataSet.getObject(this.getName(), this
				.getTestName(), objectId, objectType);

		if (typedObject == null) {
			throw new DDTTestDataException(
					"Error retrieving testdata, could not find object("
							+ objectId + " of type " + objectType + ")");
		} else {
			obj = typedObject.getValue();
		}
		return obj;
	}

	/**
	 * Retrieve object with specified identifier on a per class basis. <br/>If
	 * no data exists an exception will be raised.
	 * 
	 * @param objectId
	 *            specifies key for retrieval
	 * 
	 * @return Object that is stored under identifier key
	 */
	protected Object getGlobalObject(String objectId) {
		Object obj = null;
		TypedObject typedObject = this.classDataSet.findObject(objectId);

		if (typedObject == null) {
			throw new DDTTestDataException(
					"Error retrieving testdata, could not find object.");
		} else {
			obj = typedObject.getValue();
		}
		return obj;
	}

	/**
	 * Retrieve object with specified identifier on a per class basis. <br/>If
	 * no data exists an exception will be raised.
	 * 
	 * @param objectId
	 *            specifies key for retrieval
	 * 
	 * @return Object that is stored under identifier key
	 */
	protected Object getGlobalObject(String objectId, String objectType) {
		Object obj = null;
		TypedObject typedObject = this.classDataSet.findObject(objectId,
				objectType);

		if (typedObject != null) {
			obj = typedObject.getValue();
		} else {
			throw new DDTTestDataException(
					"Error retrieving testdata, could not find object.");
		}
		return obj;
	}

	/**
	 * Retrieve object with specified identifier on class independend basis.
	 * <br/>If no data exists an exception will be raised.
	 * 
	 * @param objectId
	 *            specifies key for retrieval
	 * 
	 * @return Object that is stored under identifier key
	 */
	protected Object getResourceObject(String objectId) {
		Object obj = null;
		TypedObject typedObject = DDTDataRepository.getInstance().getObject(
				objectId);
		if (typedObject == null) {
			throw new DDTTestDataException(
					"Error retrieving testdata, could not find object.");
		}
		obj = typedObject.getValue();
		return obj;
	}

	/**
	 * Retrieve object with specified identifier on class independend basis.
	 * <br/>If no data exists an exception will be raised.
	 * 
	 * @param objectId
	 *            specifies key for retrieval
	 * 
	 * @return Object that is stored under identifier key
	 */
	protected Object getResourceObject(String objectId, String objectType) {
		Object obj = null;
		TypedObject typedObject = DDTDataRepository.getInstance().getObject(
				objectId, objectType);
		obj = typedObject.getValue();
		return obj;
	}

	/**
	 * Add object to make assertion against assert definition identified by
	 * assertId
	 * 
	 * @param assertId
	 *            to identify assert
	 * @param object
	 *            used as actual object against expected object defined in
	 *            assertion
	 */
	protected void addObjectToAssert(String assertId, Object object) {
		addAssertInfo(assertId, object);
	}

	/**
	 * Add actual object information to internal assert record.
	 * 
	 * @param assertId
	 *            to identify assert record
	 * @param obj
	 *            value of actual value to assert
	 */
	private AssertObject addAssertInfo(String assertId, Object object) {
		AssertObject ar;

		if (!this.classDataSet.containsKey(this.getName())) {
			throw new DDTException("No asserts defined in method scope");
		}

		ar = (AssertObject) this.assertMap.get(assertId);

		if (ar == null) {
			throw new DDTException("Assert \"" + assertId
					+ "\" does not exist in resource.");
		}

		ar.setActualObject(object);
		return ar;
	}

	/**
	 * Directly assert expected against actual object during method execution.
	 * Do not wait till the end of test execution.
	 * 
	 * @param assertId
	 *            to retrieve expected assert
	 * @param obj
	 *            to assert against expected value
	 */
	protected void assertObject(String assertId, Object obj) {
		assertObject(assertId, obj, true);
	}

	/**
	 * Directly assert expected against actual object during method execution.
	 * Do not wait till the end of test execution.
	 * 
	 * @param assertId
	 *            to retrieve expected assert
	 * @param obj
	 *            to assert against expected value
	 * @param mark
	 *            true if validation should be marked as executed
	 */
	protected void assertObject(String assertId, Object obj, boolean mark) {
		AssertObject ar = addAssertInfo(assertId, obj);
		ar.validate(mark);
	}

	/**
	 * Validate all assertions concerning the active method-test dataset. All
	 * asserts that are not marked as allready processed are validated and
	 * marked as processed.
	 */
	protected void validateAsserts(boolean assertSupport) {
		if (assertSupport
				&& this.classDataSet.containsTest(this.getName(), this
						.getTestName())) {
			for (Iterator iter = assertMap.entrySet().iterator(); iter
					.hasNext();) {
				Entry assertEntry = (Entry) iter.next();
				TypedObject assertObj = (TypedObject) assertEntry.getValue();
				if (ObjectAsserter.class.isInstance(assertObj)) {
					ObjectAsserter oa = (ObjectAsserter) assertObj;
					if (!oa.isValidated()) {
						try {
							oa.validate(true);
						} catch (AssertionFailedError ex) {
							if (DDTConfiguration.getInstance()
									.isSpecificationAssert()) {
								if (!"".equals(this.assertMessages)) {
									this.assertMessages.append(LF);
								}
								this.assertMessages.append(oa).append(
										ex.getMessage());
							} else {
								throw ex;
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Implement method for initializing test context. Especially retrieving
	 * test data resource. <br/>The easies way to do this is just to use the
	 * following code snipplet: <code><pre>
	 * void initContext() {
	 * 	initTestData(&quot;/mySpecialResource.xml&quot;, &quot;ClassIdInResourceToUse&quot;);
	 * }
	 * </pre></code>
	 */
	abstract protected void initContext();

	/**
	 * Initialize xml test data for specified classId in resource associagted to
	 * same name as classId using {@link ResourceNameFactory}.
	 * 
	 * @param resource
	 *            of xml based test data
	 */
	protected void initTestData(String classId) {
		initTestData(classId, classId);
	}

	/**
	 * Initialize xml test data for specified classId in resource
	 * 
	 * @param resource
	 *            of xml based test data
	 * @param classId
	 *            of test data to process
	 */
	protected void initTestData(String resource, String classId) {
		String resourceName = ResourceNameFactory.getInstance().getName(
				ClassAnalyser.classPackage(this), resource);
		log.debug("parse() - resource to process: " + resourceName);
		this.classDataSet = DDTDataRepository.getInstance().get(resourceName,
				classId);
	}

	/**
	 * Do not use this method to define tests. Use <code>test&lt;name&gt;()
	 * </code>
	 * instead.
	 */
	protected void runTest() {
		throw new DDTException("It is forbidden to use DDTTestCase.runTest()");
	}

	/**
	 * Execute the testmethod without extra setUp and tearDown methods and no
	 * hooks to TestListener classes like TestRunner. <br/>This method contains
	 * the iteration over the xml defined tests per method.
	 * 
	 * @throws Throwable
	 *             on any exception that occures
	 */
	protected void runMethodTest() throws Throwable {
		log.debug("runMethodTest() - START");

		String fName = getName();

		assertNotNull(fName);

		Method runMethod = null;
		try {
			// use getMethod to get all public inherited
			// methods. getDeclaredMethods returns all
			// methods of this class but excludes the
			// inherited ones.
			runMethod = getClass().getMethod(fName, null);
		} catch (NoSuchMethodException e) {
			fail("Method \"" + fName + "\" not found");
		}

		if (!Modifier.isPublic(runMethod.getModifiers())) {
			fail("Method \"" + fName + "\" should be public");
		}

		// process iteration over tests per method. if no data available (JUnit
		// TestCase)
		// just do simple method invokation
		this.exHandler = new ExceptionHandler(this.getName());
		if (this.classDataSet.get(this.getName()) == null
				|| this.classDataSet.size(this.getName()) == 0) {
			testResult.startMethodTest(this, "no-testdata");
			processMethodTest(runMethod, this.getName(), null);
		} else {
			List<String> orderedTestKeys = this.classDataSet
					.getOrderedTestKeys(this.getName());

			for (String testId : orderedTestKeys) {
				testResult.startMethodTest(this, testId);
				this.testName = testId;
				this.assertMap = (TypedObjectMap) this.classDataSet
						.getAssertMap(this.getName(), testId).clone();
				// reset assert error buffer of one testcase execution
				this.assertMessages.delete(0, this.assertMessages.length());
				processMethodTest(runMethod, testId, this.assertMap);
			}
		}
		int totalCount = this.classDataSet.size(this.getName());
		this.exHandler.summarizeProblems(totalCount == 0 ? 1 : totalCount);

		log.debug("runMethodTest() - END");
	}

	/**
	 * Process one xml based test of runMethod by executing setUp() and
	 * tearDown() around method execution.
	 * 
	 * @param runMethod
	 *            to process from testclass
	 * @param testId
	 *            of test to run
	 * @param assertMap
	 *            containing all info about expected exception
	 * @throws Throwable
	 */
	private void processMethodTest(Method runMethod, String testId,
			TypedObjectMap assertMap) throws Throwable {
		boolean executeMethod = false;
		try {
			this.setUp();
			executeMethod = true;
		} catch (Throwable ex) {
			DDTException ddtEx;
			if (DDTException.class.isInstance(ex)) {
				ddtEx = (DDTException) ex;
			} else {
				ddtEx = new DDTSetUpException(ex.getMessage(), ex);
			}
			this.testResult.addMethodTestError(this, testId, ddtEx);
		}
		// only if no error is raised the method trunk should be executed
		if (executeMethod) {
			try {
				runMethod.setAccessible(true);
				runMethod.invoke(this, new Object[0]);
				validateAsserts(DDTConfiguration.getInstance()
						.isActiveAsserts());
				// a set of assert errors where catched during processing
				if (!"".equals(this.assertMessages.toString())) {
					throw new AssertionFailedError(this.assertMessages
							.toString());
				}
				this.exHandler.checkOnExpectedException(testId, assertMap);
			} catch (Throwable ex1) {
				try {
					this.exHandler.process(testId, ex1, assertMap);
				} catch (AssertionFailedError ex) {
					this.testResult.addMethodTestFailure(this, testId, ex);
				} catch (Throwable ex) {
					this.testResult.addMethodTestError(this, testId, ex);
				}
			} finally {
				this.assertMessages = new StringBuffer();
				try {
					this.tearDown();
				} catch (Throwable ex) {
					DDTException ddtEx;
					if (DDTException.class.isInstance(ex)) {
						ddtEx = (DDTException) ex;
					} else {
						ddtEx = new DDTTearDownException(ex.getMessage(), ex);
					}
					this.testResult.addMethodTestError(this, testId, ddtEx);
				}
				testResult.endMethodTest(this, testId);
				log.debug("runTest() - processed method \"" + getName()
						+ "\"  testId \"" + testId + "\"");
			}
		}
	}

	/**
	 * Count number of test datasets provided for method methodName. <br/>If
	 * dataset for this method is null, 1 will be returned (a standard JUnit
	 * method)
	 * 
	 * @return Count of tests under method methodName
	 */
	public int countMethodTests() {
		int testCount = 1;

		if (this.classDataSet != null
				&& this.classDataSet.size(this.getName()) > 0) {
			testCount = this.classDataSet.size(this.getName());
		} else if (this.classDataSet == null) {
			testCount = -1;
		}

		return testCount;
	}

	/**
	 * @return Information about actual run test
	 */
	public String runInfo() {
		StringBuffer sb = new StringBuffer();

		sb.append("Test class: ").append(this.getClass().getName()).append(
				", method: ").append(this.getName()).append(LF);

		return sb.toString();
	}

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

}
