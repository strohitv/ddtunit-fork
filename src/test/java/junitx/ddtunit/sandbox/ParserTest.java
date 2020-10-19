//$Id: ParserTest.java 351 2008-08-14 20:20:56Z jg_hamburg $
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
package junitx.ddtunit.sandbox;

import junit.framework.TestCase;
import junitx.ddtunit.data.TypedObject;
import junitx.ddtunit.data.processing.IParser;
import junitx.ddtunit.data.processing.parser.ParserImpl;
import junitx.ddtunit.resources.SimpleVO;

/**
 * Read object definition from xml resource and generate single object instance.
 * 
 * @author jg
 */
public class ParserTest extends TestCase {
    private String expectedString = "Hallo obj5";

    private int expectedInt = 4711;

    private String objDef = "<obj id=\"myObj\" type=\"string\">"
            + expectedString + "</obj>";

    private String obj2Def = "<obj id=\"myObj\" type=\"junitx.ddtunit.resources.SimpleVO\">"
            + "<stringValue>" + expectedString + "</stringValue>" + "</obj>";

    private String obj3Def = "<obj id=\"myObj\" baseid=\"obj5\" "
            + "type=\"junitx.ddtunit.resources.SimpleVO\">" + "<integerValue>"
            + expectedInt + "</integerValue>" + "</obj>";

    private static final String XML_HEADER = "<?xml version=\"1.0\"?>";

    public ParserTest(String name) {
        super(name);
//        BasicConfigurator.configure();
    }

    /**
     * Provide an object instance that is used as base for object creation.<br/>
     * The idea is like cloning and extending but without using Clonable.
     * 
     * @throws Exception
     */
    public void testParseElementWithBaseObject() throws Exception {
        IParser parser = new ParserImpl();
        TypedObject baseObj = parser.parseElement(obj2Def, true);
        TypedObject extendedObj = parser.parseElement(obj3Def, true);
        assertNotNull("Generated object should not be null", baseObj);
        assertNotNull("Generated value should not be null", baseObj.getValue());
        SimpleVO simple = (SimpleVO) extendedObj.getValue();
        assertEquals("Wrong object generated", expectedString, simple
            .getStringValue());
        assertEquals("Wrong object generated", new Integer(expectedInt), simple
            .getIntegerValue());
    }
}
