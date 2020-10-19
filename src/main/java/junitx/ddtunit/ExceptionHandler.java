// $Id: ExceptionHandler.java 351 2008-08-14 20:20:56Z jg_hamburg $
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import junit.framework.AssertionFailedError;
import junitx.ddtunit.data.AssertObject;
import junitx.ddtunit.data.ExceptionAsserter;
import junitx.ddtunit.data.TypedObject;
import junitx.ddtunit.data.TypedObjectMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handler for processing of exceptions caught during tst execution.
 * 
 * @author jg
 */
class ExceptionHandler {

    private Logger log = LoggerFactory.getLogger(ExceptionHandler.class);

    private String methodName;

    private Map methodTestFailure;

    private Map methodTestError;

    private static final String LF = System.getProperty("line.separator");

    /**
     * Instanciate ExceptionHandler
     * 
     * @param methodName
     */
    public ExceptionHandler(String methodName) {
        this.methodName = methodName;
        this.methodTestFailure = new HashMap();
        this.methodTestError = new HashMap();
    }

    /**
     * Process caught exception of test
     * 
     * @param testId identifiing method test
     * @param aThrowable
     * @param assertMap containing infod about expected exceptions
     * @throws Throwable uncaught exception
     */
    public void process(String testId, Throwable aThrowable,
            TypedObjectMap assertMap) throws Throwable {
        log.debug("process(" + methodName + ", " + testId + ", "
                + aThrowable.getMessage() + ") - START");
        if (InvocationTargetException.class.isInstance(aThrowable)) {
            InvocationTargetException myEx = (InvocationTargetException) aThrowable;
            aThrowable.fillInStackTrace();
            addErrorFailure(testId, myEx.getTargetException(), assertMap);
        } else {
            addErrorFailure(testId, aThrowable, assertMap);
        }
        log.debug("process(" + methodName + ", " + testId + ", "
                + aThrowable.getMessage() + ") - END");
    }

    /**
     * @param testId
     * @param aThrowable
     * @param assertMap containing all expected exception infos
     */
    private void addErrorFailure(String testId, Throwable aThrowable,
            TypedObjectMap assertMap) throws Throwable {
        if (!ignoreExpected(testId, aThrowable, assertMap)) {
            if (AssertionFailedError.class.isInstance(aThrowable)) {
                AssertionFailedError assertError = (AssertionFailedError) aThrowable;
                this.methodTestFailure.put(testId, new DDTTestFailure(null,
                        testId, assertError));
                throw assertError;
            } else {
                this.methodTestError.put(testId, new DDTTestFailure(null,
                        testId, aThrowable));
                throw aThrowable;
            }
        }
    }

    /**
     * Check if provided exception is expected to be thrown during test
     * execution. <br/>If expected ignore this exception.
     * 
     * @param testId
     * @param aThrowable
     */
    private boolean ignoreExpected(String testId, Throwable aThrowable,
            TypedObjectMap assertMap) {
        log.debug("filter(" + methodName + ", " + testId + ", "
                + aThrowable.getMessage() + ") - START");
        boolean caughtExpectedException = false;

        if (assertMap != null) {
            Throwable lastCaughtException = null;
            for (Iterator iter = assertMap.entrySet().iterator(); iter
                .hasNext();) {
                Entry assertEntry = (Entry) iter.next();
                AssertObject assertObj = (AssertObject) assertEntry.getValue();
                if (ExceptionAsserter.class.isInstance(assertObj)) {
                    ExceptionAsserter exAssert = (ExceptionAsserter) assertObj;
                    try {
                        exAssert.setActualObject(aThrowable);
                        exAssert.validate(false);
                        caughtExpectedException = true;
                        break;
                        // } catch (DDTException ex) {
                        // if (!ex.getMessage().startsWith(
                        // "Class types of actual (")) {
                        // throw ex;
                        // }
                    } catch (Throwable ex) {
                        // catch any exception thrown by assertion
                        lastCaughtException = ex;
                        log
                            .debug(
                                "Exception caught during check on expected exception.",
                                lastCaughtException);
                    }
                }
            }
            if (caughtExpectedException) {
                log.debug("Caught expected exception in test " + testId + ":"
                        + aThrowable.getMessage());
            }
        }
        log.debug("filter(" + methodName + ", " + testId + ", "
                + aThrowable.getMessage() + ") found: "
                + caughtExpectedException + "- END");
        return caughtExpectedException;
    }

    /**
     * Summarize problems of method tests. <br/>If any errors were detected a
     * method error will be thrown, <br/>if only assertion errors were detected
     * only an assertion error will be thrown. <br/>First line contains summary
     * statistics, following lines
     * 
     * @param testCount under testmethod
     */
    void summarizeProblems(int testCount) {
        int errorCount = this.methodTestError.size();
        int failureCount = this.methodTestFailure.size();
        if (errorCount == 0 && failureCount == 0) {
            return;
        }
        StringBuffer sb = new StringBuffer("method ").append(this.methodName)
            .append(" - ");
        sb.append("Total: " + testCount + ", Errors: " + errorCount
                + ", Failures: " + failureCount + LF);
        Throwable testError = null;
        if (errorCount > 0) {
            testError = summariesErrors(sb, testError);
        }
        if (failureCount > 0) {
            if (errorCount > 0) {
                sb.append(LF);
            }
            testError = summariesFailures(sb, testError);
        }

        if (errorCount == 0) {
            AssertionFailedError afEx = new AssertionFailedError(sb.toString());
            afEx.setStackTrace(testError.getStackTrace());
            throw afEx;
        } else {
            DDTException processError = new DDTException(sb.toString(),
                    testError);
            processError.setStackTrace(testError.getStackTrace());
            throw processError;
        }
    }

    /**
     * Collect all information of errors occured under one JUnit method by
     * multiple xml testcases in one throwable and return it.
     * 
     * @param sb
     * @param testError
     * @return
     */
    private Throwable summariesFailures(StringBuffer sb, Throwable testError) {
        DDTTestFailure testFailure;
        for (Iterator iter = this.methodTestFailure.values().iterator(); iter
            .hasNext();) {
            testFailure = (DDTTestFailure) iter.next();
            sb.append("F-(").append(testFailure.getMethodTest()).append(") ")
                .append(testFailure.exceptionMessage());
            if (iter.hasNext()) {
                sb.append(LF);
            }
            if (testError == null) {
                testError = testFailure.thrownException();
            }
        }
        return testError;
    }

    /**
     * Collect all information of errors occured under one JUnit method by
     * multiple xml testcases in one throwable and return it.
     * 
     * @param sb
     * @param testError
     * @return throwable that contains summary of all occured errors/failures
     *         during execution of testmethod
     */
    private Throwable summariesErrors(StringBuffer sb, Throwable testError) {
        DDTTestFailure testFailure;
        for (Iterator iter = this.methodTestError.values().iterator(); iter
            .hasNext();) {
            testFailure = (DDTTestFailure) iter.next();
            sb.append("E-(").append(testFailure.getMethodTest()).append(") ")
                .append(testFailure.exceptionMessage());
            if (iter.hasNext()) {
                sb.append(LF);
            }
            if (testError == null) {
                testError = testFailure.thrownException();
            }
        }
        return testError;
    }

    /**
     * Check if an expected exception was not thrown during test execution. If
     * an expected exception was defined but not raised throw an appropriate
     * exception.
     * 
     * @param testId of executed test under testmethod.
     * @param assertMap containing all assert information incl. expected
     *        exceptions
     */
    public void checkOnExpectedException(String testId, TypedObjectMap assertMap) {
        if (assertMap != null) {
            int numberOfExpectedExceptions = 0;
            Throwable firstException = null;
            for (Iterator iter = assertMap.entrySet().iterator(); iter
                .hasNext();) {
                Entry assertEntry = (Entry) iter.next();
                String exceptKey = (String) assertEntry.getKey();
                TypedObject assertObj = assertMap.get(exceptKey);
                if (ExceptionAsserter.class.isInstance(assertObj)) {
                    numberOfExpectedExceptions++;
                    firstException = (Throwable) assertObj.getValue();
                }
            }
            if (numberOfExpectedExceptions > 0) {
                StringBuffer sb = new StringBuffer("There is/are ").append(
                    numberOfExpectedExceptions).append(
                    " expected exception(s) defined in test '").append(testId)
                    .append("'");
                sb.append(LF).append(" (last as hint): ").append(
                    firstException.getClass().getName());
                if (firstException.getMessage() != null) {
                    sb.append(" - ").append(firstException.getMessage());
                }
                throw new AssertionFailedError(sb.toString());
            }
        }
    }
}
