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
package junitx.ddtunit.resources;

import java.util.List;
import java.util.Vector;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junitx.ddtunit.DDTTestListener;
import junitx.ddtunit.IDDTTestCase;

/**
 * TestRunner implemantation for evaluation of DDTTestResult functionality.
 *
 * @author jg
 */
public class RunnerMonitor implements DDTTestListener
{
    private static RunnerMonitor singleton;
    private List errors;
    private List failures;
    private StringBuffer buffer;

    /**
     * Private Constructor for instanciation singleton.
     */
    private RunnerMonitor()
    {
        this.errors = new Vector();
        this.failures = new Vector();
        this.buffer = new StringBuffer();
    }

    /**
     * Create singleton instance of TestRunner
     * @return instance of TestRunner
     */
    public static RunnerMonitor getInstance()
    {
        if (singleton == null)
        {
            singleton = new RunnerMonitor();
        }

        return singleton;
    }

    /**
     * TODO Comment for overridden method
     *
     * @see junitx.ddtunit.DDTTestListener#addMethodTestError(junit.framework.Test,
     *      java.lang.String, java.lang.Throwable)
     */
    public void addMethodTestError(Test test, String testName,
        Throwable throwable)
    {
        // TODO Auto-generated method stub
    }

    /**
     * TODO Comment for overridden method
     *
     * @see junitx.ddtunit.DDTTestListener#addMethodTestFailure(junit.framework.Test,
     *      java.lang.String, junit.framework.AssertionFailedError)
     */
    public void addMethodTestFailure(Test test, String testName,
        AssertionFailedError assertFailed)
    {
        // TODO Auto-generated method stub
    }

    /**
     * TODO Comment for overridden method
     *
     * @see junitx.ddtunit.DDTTestListener#endMethodTest(junitx.ddtunit.DDTTestCase,
     *      java.lang.String)
     */
    public void endMethodTest(IDDTTestCase test, String testName)
    {
        // TODO Auto-generated method stub
    }

    /**
     * TODO Comment for overridden method
     *
     * @see junitx.ddtunit.DDTTestListener#startMethodTest(junitx.ddtunit.DDTTestCase,
     *      java.lang.String)
     */
    public void startMethodTest(IDDTTestCase test, String testName)
    {
        // TODO Auto-generated method stub
    }

    /**
     * TODO Comment for overridden method
     *
     * @see junit.framework.TestListener#addError(junit.framework.Test,
     *      java.lang.Throwable)
     */
    public void addError(Test test, Throwable t)
    {
        // TODO Auto-generated method stub
    }

    /**
     * TODO Comment for overridden method
     *
     * @see junit.framework.TestListener#addFailure(junit.framework.Test,
     *      junit.framework.AssertionFailedError)
     */
    public void addFailure(Test test, AssertionFailedError t)
    {
        // TODO Auto-generated method stub
    }

    /**
     * TODO Comment for overridden method
     *
     * @see junit.framework.TestListener#endTest(junit.framework.Test)
     */
    public void endTest(Test test)
    {
        // TODO Auto-generated method stub
    }

    /**
     * TODO Comment for overridden method
     *
     * @see junit.framework.TestListener#startTest(junit.framework.Test)
     */
    public void startTest(Test test)
    {
        // TODO Auto-generated method stub
    }

    /**
     * @return Returns the errors.
     */
    public List getErrors()
    {
        return this.errors;
    }

    /**
     * @return Returns the failures.
     */
    public List getFailures()
    {
        return this.failures;
    }

    /**
     * @return Returns the buffer.
     */
    public StringBuffer getBuffer()
    {
        return this.buffer;
    }

    /**
     */
    public void resetBuffer()
    {
        this.buffer.replace(0, this.buffer.length(), "");
    }
}
