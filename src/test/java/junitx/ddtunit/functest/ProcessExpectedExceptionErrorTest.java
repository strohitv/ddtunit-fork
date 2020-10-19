// $Id: ProcessExpectedExceptionErrorTest.java 202 2005-11-22 19:21:28Z jg_hamburg $
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
package junitx.ddtunit.functest;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestFailure;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import junitx.ddtunit.DDTException;
import junitx.ddtunit.DDTTestCase;

/**
 * Test class to verify behavior if an expected exception is stated in the test
 * resource but not raised during test execution.
 * 
 * @author jg
 */
public class ProcessExpectedExceptionErrorTest extends TestCase {
    private static final String LF = System.getProperty("line.separator");

    /**
     * @param name
     */
    public ProcessExpectedExceptionErrorTest(String name) {
        super(name);
    }

    /**
     * Define tests to run
     * 
     * @return
     */
    public static Test suite() {
        return new TestSuite(ProcessExpectedExceptionErrorTest.class);
    }

    /**
     * Test behavior on defined but not raised expected exception. An exception
     * should be raised stating the missing exception.
     */
    public void testDefinedbutNotRaisedException() {
        String expectedMessage = "xxx"
                + LF
                + "Caused by:junit.framework.AssertionFailedError - method testDefinedbutNotRaisedException - Total: 1, Errors: 0, Failures: 1"
                + LF
                + "F-(myFirstTestCase) There is/are 1 expected exception(s) defined in test 'myFirstTestCase'"
                + LF
                + " (last as hint): java.lang.RuntimeException - My Exception";

        TestResult result = TestRunner.run(new MyDDTTestCase(
                "testDefinedbutNotRaisedException"));

        assertEquals("Wrong number of errors", 0, result.errorCount());
        assertEquals("Wrong number of failures", 1, result.failureCount());

        Throwable expectedException = ((TestFailure) result.failures()
            .nextElement()).thrownException();
        DDTException expected = DDTException.create(new StringBuffer("xxx"),
            expectedException);
        assertEquals("Wrong exception message", expectedMessage, expected
            .getMessage());
    }

    private static class MyDDTTestCase extends DDTTestCase {
        /**
         * instanciate testcase for testmethod defined by name
         * 
         * @param name of testmethod to execute
         */
        public MyDDTTestCase(String name) {
            super(name);
        }

        /**
         * @see junitx.ddtunit.DDTTestCase#initContext()
         */
        protected void initContext() {
            initTestData("ProcessExpectedExceptionTest",
                "ProcessUnexpectedExceptionTest");
        }

        /**
         * Test behavior on defined but not raised expected exception. An
         * exception should be raised stating the missing exception.
         */
        public void testDefinedbutNotRaisedException() {
            assertTrue("Should allways be true", true);
        }
    }
}