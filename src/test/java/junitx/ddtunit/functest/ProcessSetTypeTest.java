//$Id: ProcessSetTypeTest.java 240 2006-05-03 22:45:16Z jg_hamburg $
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

import java.util.Collection;
import java.util.Set;

import junitx.ddtunit.DDTTestCase;

/**
 * Test class for checking read facilities of collection type xml resource
 * objects.
 * 
 * @author jg
 */
public class ProcessSetTypeTest extends DDTTestCase {

    /*
     * (non-Javadoc)
     * 
     * @see junitx.ddtunit.DDTTestCase#initContext()()
     */
    protected void initContext() {
        initTestData("ProcessSetTypeTest", "ProcessSetTypeTest");
    }

    /**
     * Test reading simple <code>java.util.Vector</code> object from xml
     * resource.
     * 
     */
    public void testReadSet() {
        Set set = (Set) getObject("mySet");
        assertNotNull("Set should not be null", set);
        addObjectToAssert("count", new Integer(set.size()));
        if (set.size() > 0) {
            addObjectToAssert("expected", set.toArray()[0]);
        } else {
            addObjectToAssert("expected", null);
        }
    }

    public void testReadNullSet() {
        Set set = (Set) getObject("mySet");
        addObjectToAssert("result", set);
    }

    /**
     * Test reading of nested collections.
     * 
     */
    public void testReadNestedSet() {
        Set set = (Set) getObject("mySet");
        assertNotNull("Set should not be null", set);
        assertObject("count", new Integer(set.size()), false);
        Object[] setElements = set.toArray();
        Collection first = (Collection) setElements[0];
        Collection second = (Collection) setElements[1];
        assertObject("count", new Integer(first.size()), false);
        assertObject("count", new Integer(second.size()));
    }

    /**
     * Test reading of nested collections.
     * 
     */
    public void testReadNestedMixedType() {
        Set set = (Set) getObject("mySet");
        assertNotNull("Set should not be null", set);
        assertObject("count", new Integer(set.size()), false);
        Object[] setElements = set.toArray();
        Collection first = (Collection) setElements[0];
        addObjectToAssert("count", new Integer(first.size()));
        Object[] firstElements = first.toArray();
        addObjectToAssert("firstEntry", firstElements[0]);
        addObjectToAssert("secondEntry", firstElements[1]);
    }

    public void testArrayFromSet() {
        Set set = (Set) getObject("mySet");
        addObjectToAssert("count", new Integer(set.size()));
        addObjectToAssert("result", set.toArray()[0]);
    }

}