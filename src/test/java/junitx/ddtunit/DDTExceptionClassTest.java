//$Id: DDTExceptionClassTest.java 148 2005-05-24 23:04:20Z jg_hamburg $
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
import junit.framework.TestSuite;

/**
 * Check functionality provided by {@link junitx.ddtunit.DDTException}.
 *
 * @author jg
 */
public class DDTExceptionClassTest extends TestCase
{
    private final String LF = System.getProperty("line.separator");

    /**
     * Instanciate unit test with method name to execute
     *
     * @param name of testmethod to execute
     */
    public DDTExceptionClassTest(String name)
    {
        super(name);
    }

    /**
     * Create test suite defined by this class
     *
     * @return Test containing all tests provided by this class
     */
    public static Test suite()
    {
        return new TestSuite(DDTExceptionClassTest.class);
    }

    /**
     * Test functionallity of
     * {@link DDTException#create(StringBuffer, Throwable)}using simple
     * exception without a cause exception
     */
    public void testCreateSingleException()
    {
        String msg = "My single exception";
        Throwable single = new RuntimeException(msg);
        StringBuffer sb = new StringBuffer(msg);
        DDTException ddtException = DDTException.create(sb, single);
        StringBuffer expectedMessage = new StringBuffer(sb.toString()).append(LF)
                                                                      .append("Caused by:java.lang.RuntimeException - "
                + msg);

        assertEquals("Wrong exception message", expectedMessage.toString(),
            ddtException.getMessage());
    }

    /**
     * Test functionallity of
     * {@link DDTException#create(StringBuffer, Throwable)}using exception with
     * one cause exception
     */
    public void testCreateDoubleException()
    {
        String msg = "My exception";
        Throwable single = new RuntimeException(msg);
        NullPointerException nullEx = new NullPointerException("Root cause");

        single.initCause(nullEx);

        StringBuffer sb = new StringBuffer("First line of message");
        DDTException ddtException = DDTException.create(sb, single);
        StringBuffer expectedMessage = new StringBuffer(sb.toString()).append(LF)
                                                                      .append("Caused by:java.lang.RuntimeException - ")
                                                                      .append(msg)
                                                                      .append(LF)
                                                                      .append("Caused by (java.lang.NullPointerException) Root cause");

        assertEquals("Wrong exception message", expectedMessage.toString(),
            ddtException.getMessage());
    }
}
