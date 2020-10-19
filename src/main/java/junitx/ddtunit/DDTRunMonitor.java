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

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.textui.ResultPrinter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements a simple monitor for testruns. <br/>It is implemented
 * as singleton so we can use it internally to protocol test progress and
 * results. <br/>Lateron there will be an implementaion of a TestRunner that
 * conformes to the interface DDTTestListener
 * 
 * @author jg
 */
public class DDTRunMonitor extends ResultPrinter implements DDTTestListener {
    private static DDTRunMonitor singletonRef;

    private Logger log = LoggerFactory.getLogger(DDTRunMonitor.class);

    private int testRuns = 0;

    private int testErrors = 0;

    private int testFailures = 0;

    /**
     * Create object of type DDTRunMonitor
     */
    private DDTRunMonitor() {
        super(System.out);
    }

    /**
     * Get singleton instance of this class
     * 
     * @return singleton instance
     */
    static public DDTRunMonitor getInstance() {
        if (singletonRef == null) {
            singletonRef = new DDTRunMonitor();
        }

        return singletonRef;
    }

    /**
     * Add error of executed test
     * 
     * @see junitx.ddtunit.DDTTestListener#addMethodTestError(junit.framework.Test,
     *      java.lang.String, java.lang.Throwable)
     */
    public void addMethodTestError(Test test, String testName,
            Throwable throwable) {
        String method = "";

        if (TestCase.class.isInstance(test)) {
            method = ((TestCase) test).getName();
        }

        log.info("[" + test.getClass() + "] Error method " + method + ", test "
                + testName);
        log.info("Cause: ", throwable);
        this.testErrors++;
    }

    /**
     * Add failure of executed test
     * 
     * @see junitx.ddtunit.DDTTestListener#addMethodTestFailure(junit.framework.Test,
     *      java.lang.String, junit.framework.AssertionFailedError)
     */
    public void addMethodTestFailure(Test test, String testName,
            AssertionFailedError assertFailed) {
        String method = "";

        if (TestCase.class.isInstance(test)) {
            method = ((TestCase) test).getName();
        }

        log.info("[" + test.getClass() + "] Failure method " + method
                + ", test " + testName);
        log.info("Cause: ", assertFailed);
        this.testFailures++;
    }

    /**
     * Notify about end of method test execution
     * 
     * @see junitx.ddtunit.DDTTestListener#endMethodTest(junitx.ddtunit.DDTTestCase,
     *      java.lang.String)
     */
    public void endMethodTest(IDDTTestCase test, String testName) {
        int testCount = test.countMethodTests();
        String method = test.getName();
        log.info("[" + test.getClass() + "] (" + this.testRuns + "/"
                + testCount + ") method \"" + method + "\", test \"" + testName
                + "\"");
    }

    /**
     * Notify about start of method test execution
     * 
     * @see junitx.ddtunit.DDTTestListener#startMethodTest(junitx.ddtunit.DDTTestCase,
     *      java.lang.String)
     */
    public void startMethodTest(IDDTTestCase test, String testName) {
        String method = test.getName();
        int testCount = test.countMethodTests();
        this.testRuns++;
        log.debug("[" + test.getClass() + "] Start method " + method + " ("
                + this.testRuns + " of " + testCount + ") test " + testName);
    }

    /**
     * Just let the standard runner do this work - do nothing
     * 
     * @see junit.framework.TestListener#addError(junit.framework.Test,
     *      java.lang.Throwable)
     */
    public void addError(Test test, Throwable t) {
        String method = "";

        if (TestCase.class.isInstance(test)) {
            method = ((TestCase) test).getName();
        }
        log.info("[" + test.getClass() + "] method " + method + " error:", t);
    }

    /**
     * Just let the standard runner do this work - do nothing
     * 
     * @see junit.framework.TestListener#addFailure(junit.framework.Test,
     *      junit.framework.AssertionFailedError)
     */
    public void addFailure(Test test, AssertionFailedError t) {
        String method = "";

        if (TestCase.class.isInstance(test)) {
            method = ((TestCase) test).getName();
        }

        log.info("[" + test.getClass() + "] Add Faiure method " + method, t);
    }

    /**
     * Just let the standard runner do this work - do nothing
     * 
     * @see junit.framework.TestListener#endTest(junit.framework.Test)
     */
    public void endTest(Test test) {
        String method = "";
        if (TestCase.class.isInstance(test)) {
            method = ((TestCase) test).getName();
        }
        if (IDDTTestCase.class.isInstance(test)) {
            int testCount = ((IDDTTestCase) test).countMethodTests();
            log.info("[" + test.getClass() + "] method \"" + method + "\": "
                    + testRuns + " of " + testCount + " test(s), " + testErrors
                    + " error(s), " + testFailures + " failure(s)");
        } else {
            log
                .info("[" + test.getClass() + "] method \"" + method + "\": "
                        + testErrors + " error(s), " + testFailures
                        + " failure(s)");
        }
    }

    /**
     * Just let the standard runner do this work - do nothing
     * 
     * @see junit.framework.TestListener#startTest(junit.framework.Test)
     */
    public void startTest(Test test) {
        String method = "";
        this.testRuns = 0;
        this.testErrors = 0;
        this.testFailures = 0;

        if (TestCase.class.isInstance(test)) {
            method = ((TestCase) test).getName();
        }
        log.debug("[" + test.getClass() + "] Start method \"" + method + "\"");
    }
}
