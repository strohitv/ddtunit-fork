//$Id: ContentCreatorActionTest.java 198 2005-11-06 23:15:52Z jg_hamburg $
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
 * Test to check functionallity of {@link ContentCreatorActionTest}.<br/>
 * Contract of Action class: <br/>
 * <ul>
 * <li>Content action must be the last on action stack</li>
 * <li>There must be a valid root action distinct from content action.</li>
 * <li>&nbsp;</li>
 * </ul>
 * 
 * @author jg
 */
public class ContentCreatorActionTest extends TestCase {
    private ActionStack actionStack;

    /**
     * Instanciate test providing method to test
     * 
     * @param name of method to execute
     */
    public ContentCreatorActionTest(String name) {
        super(name);
    }

    /**
     * @return suite of tests to execute
     */
    public static Test suite() {
        return new TestSuite(ContentCreatorActionTest.class);
    }

    public void setUp() {
        this.actionStack = new ActionStack();
    }

    /**
     * Test processing operation of {@link CollectionCreatorAction}
     */
    public void testSingleContentForSubelement() {
        // prepare object element
        Map objAttribs = new HashMap();
        objAttribs.put("id", "object");
        objAttribs.put("type", "junitx.ddtunit.resources.ComplexVO");
        ActionBase baseAction = new SubelementCreatorAction(objAttribs);
        baseAction.inject();
        this.actionStack.push(baseAction);
        // prepare subelement to use content on
        Map rootAttribs = new HashMap();
        rootAttribs.put("id", "text");
        rootAttribs.put("type", "java.lang.String");
        ActionBase rootAction = new SubelementCreatorAction(rootAttribs);
        rootAction.inject();
        this.actionStack.push(rootAction);
        // prepare content action
        String testContent = "Hallo World";
        Map attribs = new HashMap();
        attribs.put("id", "content");
        attribs.put("hint", "content");
        attribs.put("type", "java.lang.StringBuffer");
        attribs.put("content", new StringBuffer(testContent));
        ActionBase action = new ContentCreatorAction(attribs);
        assertNull("Internal object should be null", action.getObject());
        action.inject();
        this.actionStack.push(action);
        TypedObject obj = action.getObject();
        assertNotNull("Internal object should not be null", obj);
        assertEquals("Wrong type of internal object", "java.lang.StringBuffer",
            obj.getType());
        assertNotNull("Value of internal object should not be null", obj
            .getValue());
        IAction cAction = this.actionStack.peek();
        assertSame("Actions should be same.", action, cAction);
        // process content action
        cAction.process();
        assertEquals("Wrong class of internal object", "java.util.Vector",
            this.actionStack.peek().getType());
        assertEquals("Wrong id of internal object", "attrlist",
            this.actionStack.peek().getId());
    }

    public void testMultipleContentActionProcessing() {
        // prepare object element
        Map objAttribs = new HashMap();
        objAttribs.put("id", "object");
        objAttribs.put("type", "junitx.ddtunit.resources.ComplexVO");
        ActionBase baseAction = new SubelementCreatorAction(objAttribs);
        baseAction.inject();
        this.actionStack.push(baseAction);
        // prepare subelement
        Map rootAttribs = new HashMap();
        rootAttribs.put("id", "text");
        rootAttribs.put("type", "java.lang.String");
        ActionBase rootAction = new SubelementCreatorAction(rootAttribs);
        rootAction.inject();
        this.actionStack.push(rootAction);
        // prepare content action
        Map attribs = new HashMap();
        attribs.put("id", "content");
        attribs.put("hint", "content");
        attribs.put("type", "java.lang.StringBuffer");
        attribs.put("content", new StringBuffer("First line\n"));
        IAction action = new ContentCreatorAction(attribs);
        assertNull("Internal object should be null", action.getObject());
        action.inject();
        this.actionStack.push(action);
        // second content info
        attribs = new HashMap();
        attribs.put("id", "content");
        attribs.put("hint", "content");
        attribs.put("type", "java.lang.StringBuffer");
        attribs.put("content", new StringBuffer("Second line\n"));
        action = new ContentCreatorAction(attribs);
        assertNull("Internal object should be null", action.getObject());
        action.inject();
        this.actionStack.push(action);
        // process content action
        action = this.actionStack.peek();
        action.process();
        assertEquals("Wrong class of internal object", "java.util.Vector",
            this.actionStack.peek().getType());
        assertEquals("Wrong id of internal object", "attrlist",
            this.actionStack.peek().getId());
    }
}