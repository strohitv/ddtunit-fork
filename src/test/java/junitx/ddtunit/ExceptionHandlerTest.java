// $Id: ExceptionHandlerTest.java 216 2006-03-02 23:17:18Z jg_hamburg $
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

import java.lang.reflect.InvocationTargetException;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import junitx.ddtunit.data.ExceptionAsserter;
import junitx.ddtunit.data.ObjectAsserter;
import junitx.ddtunit.data.TestClusterDataSet;
import junitx.ddtunit.data.processing.ClusterDataSetTestCreator;

/**
 * TODO Comment for DDTExceptionHandlerTest
 * 
 * @author jg
 */
public class ExceptionHandlerTest extends TestCase {
    private ExceptionHandler handler;

    private String testClass;

    private String testMethod;

    private String testId;

    private TestClusterDataSet classDataSet;

    private static final String LF = System.getProperty("line.separator");

    /**
     * Constructor for DDTExceptionHandlerTest.
     * 
     * @param name
     */
    public ExceptionHandlerTest(String name) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        testClass = "ExceptionHandlerTest";
        testMethod = "methodName";
        testId = "testId";
        classDataSet = ClusterDataSetTestCreator.getClusterDataSet(testClass,
            testMethod, testId);
        handler = new ExceptionHandler(testMethod);
    }

    /**
     * Test behavior of method DDTExceptionHandler.process() by providing an
     * unknown exception.
     * 
     */
    public void testProcessUnknownException() throws Throwable {
        String message = "Uncaught exception";
        try {
            handler.process(testId, new DDTException(message),
                this.classDataSet.getAssertMap(this.testMethod, this.testId));
            handler.summarizeProblems(this.classDataSet.size(this.testMethod));
            fail("Should throw unexpected exception.");
        } catch (DDTException ex) {
            assertEquals("Wrong exception message", message, ex.getMessage());
        }
    }

    /**
     * Test behavior of method DDTExceptionHandler.process() by providing an
     * unknown exception and no valid assertion data.
     * 
     */
    public void testProcessInvocationTargetExNoAsserts() throws Throwable {
        String message = "Uncaught exception";
        try {
            Exception testEx = new InvocationTargetException(new DDTException(
                    message));

            handler.process(testId, testEx, null);
            handler.summarizeProblems(this.classDataSet.size(this.testMethod));
            fail("Should throw unexpected exception.");
        } catch (DDTException ex) {
            assertEquals("Wrong exception message", message, ex.getMessage());
        }
    }

    /**
     * Test behavior of method DDTExceptionHandler.process() by providing a
     * known exception.
     * 
     */
    public void testProcessExpectedException() {
        try {
            String exceptionId = "exception0";
            String exceptionMessage = "Expected exception";
            ExceptionAsserter assertObject = new ExceptionAsserter(exceptionId,
                    "java.lang.Exception", "ISEQUAL");

            assertObject.setValue(new Exception(exceptionMessage));
            this.classDataSet.getTestDataSet(this.testMethod, testId)
                .getAssertMap().put(exceptionId, assertObject);
            // Exceptin to catch
            Exception myEx = new Exception(exceptionMessage);
            Throwable actualEx = new InvocationTargetException(myEx);
            assertObject.setActualObject(myEx);
            handler.process(this.testId, actualEx, this.classDataSet
                .getAssertMap(this.testMethod, this.testId));
            handler.summarizeProblems(this.classDataSet.size(this.testMethod));
        } catch (Throwable e) {
            fail("Should not throw exception. " + e.getClass().getName());
        }
    }

    /**
     * Test if defined but not caught exception is detected ass failure.
     */
    public void testCheckOnExpectedException() {
        String exceptionId = "exception0";
        String exceptionMessage = "Expected exception";
        String expectedMessage = "There is/are 1 expected exception(s) defined in test 'testId'"
                + LF
                + " (last as hint): java.lang.Exception - Expected exception";

        ExceptionAsserter assertObject = new ExceptionAsserter(exceptionId,
                "java.lang.Exception", ObjectAsserter.ASSERT_ACTION_ISEQUAL);

        assertObject.setValue(new Exception(exceptionMessage));
        this.classDataSet.getTestDataSet(this.testMethod, testId)
            .getAssertMap().put(exceptionId, assertObject);
        // Exceptin to catch
        Exception myEx = new Exception(exceptionMessage);
        assertObject.setActualObject(myEx);
        try {
            handler.checkOnExpectedException(this.testId, this.classDataSet
                .getAssertMap(this.testMethod, this.testId));
            fail("Expected AssertionFailedError");
        } catch (AssertionFailedError ex) {
            assertEquals("Wrong exception message", expectedMessage, ex
                .getMessage());
        }
    }
}