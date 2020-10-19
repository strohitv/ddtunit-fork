//$Id: ProcessArrayDataTest.java 361 2009-08-09 18:27:01Z jg_hamburg $
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

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;
import junitx.ddtunit.DDTTestCase;
import junitx.ddtunit.resources.SimpleVO;

/**
 * Test array data types like Integer[], String[] ...
 * 
 * @author jg
 */
public class ProcessArrayDataTest extends DDTTestCase {

    /**
     * @param name
     */
    public ProcessArrayDataTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(ProcessArrayDataTest.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see junitx.ddtunit.DDTTestCase#initContext()
     */
    protected void initContext() {
        initTestData("ProcessArrayDataTest", "ProcessArrayDataTest");
    }

    /**
     * Test reading and asserting String objects.
     */
    public void testArrayObjectType() {
        Object[] myArray = (Object[]) getObject("myArray");
        addObjectToAssert("count", new Integer(myArray.length));
        addObjectToAssert("result", myArray);
    }

    /**
     * Test reading and asserting String objects.
     */
    public void testSpecialCaseArrayObjectType() {
        addObjectToAssert("result", getObject("myArray"));
    }

    public void testArrayOfCollections() throws Exception {
        List[] myArray = (List[]) getObject("myArray");
        addObjectToAssert("count", new Integer(myArray.length));
        addObjectToAssert("result", myArray[1].get(1));
    }

    public void testArrayOfBeanObjects() throws Exception {
        SimpleVO[] myArray = (SimpleVO[]) getObject("myArray");
        addObjectToAssert("count", new Integer(myArray.length));
        SimpleVO result1 = myArray[0];
        SimpleVO result2 = myArray[1];
        addObjectToAssert("result1", result1.getIntegerValue());
        addObjectToAssert("result2", result2.getIntegerValue());
    }
}
