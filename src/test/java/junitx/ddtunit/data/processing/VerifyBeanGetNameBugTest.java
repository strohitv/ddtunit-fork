//$Id: VerifyBeanGetNameBugTest.java 232 2006-04-05 22:02:47Z jg_hamburg $
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
package junitx.ddtunit.data.processing;

import junit.framework.TestCase;
import junit.framework.TestFailure;
import junit.framework.TestResult;
import junit.textui.TestRunner;
import junitx.ddtunit.DDTTestCase;

/**
 * if a class has a getName method, and I use hint=”bean”, this causes an error.
 * However, if I use hint=”fields” it works.
 * 
 * @author jg
 */
public class VerifyBeanGetNameBugTest extends TestCase {
    private final String LF = System.getProperty("line.separator");

    public VerifyBeanGetNameBugTest(String name) {
        super(name);
    }

    public void testGetNameBeanCreation() throws Exception {
        String expectedMessage = "junitx.ddtunit.DDTExceptionError executing setter junitx.ddtunit.resources.GetNameClazz.setName(...). Check if hint and setter are correct (Do not provide set-prefix!)."
                + LF
                + "Resource '/junitx/ddtunit/data/processing/DDT-DDTVerifyBeanGetNameBugTest.xml' line/column 14/25";

        TestResult result = TestRunner.run(new DDTVerifyBeanGetNameBugTest(
                "testCreateBean"));
        assertEquals("Wrong error count", 1, result.errorCount());
        assertEquals("Wrong error message", expectedMessage,
            ((TestFailure) result.errors().nextElement()).exceptionMessage());
    }

    private static class DDTVerifyBeanGetNameBugTest extends DDTTestCase {
        public DDTVerifyBeanGetNameBugTest(String name) {
            super(name);
        }

        public void initContext() {
            initTestData("DDTVerifyBeanGetNameBugTest");
        }

        public void testCreateBean() throws Exception {

        }
    }
}
