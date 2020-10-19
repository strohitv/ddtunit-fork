//$Id: SubelementCreatorActionTest.java 198 2005-11-06 23:15:52Z jg_hamburg $
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

import java.util.HashMap;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junitx.ddtunit.data.TypedObject;

/**
 * Test to check functionallity of {@link SubelementCreatorAction}.<br/>
 * Contract of Action class: <br/>
 * <ul>
 * <li>Subelement action are not allowed to be first in list. There must be a
 * root action.</li>
 * <li>Subelement action will create a {@link CollectionCreatorAction}action
 * containing all field data processed for basic root element from xml
 * resource..</li>
 * <li>&nbsp;</li>
 * </ul>
 * 
 * @author jg
 */
public class SubelementCreatorActionTest extends TestCase {
    private ActionStack actionStack;

    private String testContent;

    /**
     * Instanciate test providing method to test
     * 
     * @param name of method to execute
     */
    public SubelementCreatorActionTest(String name) {
        super(name);
    }

    /**
     * @return suite of tests to execute
     */
    public static Test suite() {
        return new TestSuite(SubelementCreatorActionTest.class);
    }

    public void setUp() {
        this.actionStack = new ActionStack();
        // prepare object element to use subelement on
        Map rootAttrbs = new HashMap();
        rootAttrbs.put(ParserConstants.XML_ATTR_ID, "object");
        rootAttrbs.put(ParserConstants.XML_ATTR_TYPE,
            "junitx.ddtunit.resources.SimpleVO");
        ActionBase rootAction = new SubelementCreatorAction(rootAttrbs);
        rootAction.inject();
        this.actionStack.push(rootAction);
        // prepare subelement to use content on
        Map subAttrbs = new HashMap();
        subAttrbs.put(ParserConstants.XML_ATTR_ID, "stringValue");
        subAttrbs.put(ParserConstants.XML_ATTR_TYPE, "java.lang.String");
        ActionBase subAction = new SubelementCreatorAction(subAttrbs);
        subAction.inject();
        this.actionStack.push(subAction);
        this.testContent = "Hallo World";
        Map attribs = new HashMap();
        attribs.put("id", "content");
        attribs.put("hint", "content");
        attribs.put("type", "java.lang.StringBuffer");
        attribs.put("content", new StringBuffer(testContent));
        ActionBase action = new ContentCreatorAction(attribs);
        assertNull("Internal object should be null", action.getObject());
        action.inject();
        this.actionStack.push(action);
    }

    /**
     * Test processing operation of {@link SubelementCreatorAction}
     */
    public void testActionSubelementCreator() {
        // start with content processing on field element
        TypedObject obj = this.actionStack.peek().getObject();
        assertNotNull("Internal object should not be null", obj);
        assertEquals("Wrong type of internal object", "java.lang.StringBuffer",
            obj.getType());
        this.actionStack.process();
        assertNotNull("Internal object should not be null", this.actionStack
            .peek().getValue());
        assertEquals("Wrong class of internal object", "java.util.Vector",
            this.actionStack.peek().getType());
        assertEquals("Wrong id of internal object", "attrlist",
            this.actionStack.peek().getId());
        // process implicite field container on object element
        IAction base = this.actionStack.process();
        assertEquals("Wrong Action type",
            "junitx.ddtunit.data.processing.SubelementCreatorAction", base
                .getClass().getName());
        assertEquals("Wrong type of created object",
            "junitx.ddtunit.resources.SimpleVO", base.getType());
    }

}