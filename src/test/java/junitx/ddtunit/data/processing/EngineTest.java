//$Id: EngineTest.java 165 2005-07-22 22:37:58Z jg_hamburg $
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
import junitx.ddtunit.data.TestClusterDataSet;

import org.xml.sax.helpers.AttributesImpl;

/**
 * Verify processing rules of SAX event consumer engine on creation of objects
 * 
 * @author jg
 */
public class EngineTest extends TestCase {
    private Engine engine;

    private TestClusterDataSet clusterDataSet;

    private String classId;

    public void setUp() {
        this.classId = "junitx.ddtunit.parser.processing.EngineTest";
        this.clusterDataSet = new TestClusterDataSet(classId, null);
        this.engine = new Engine(this.clusterDataSet);
    }

    public void testObjectProcessing() {
        String testContent = "Hallo World";
        AttributesImpl attribs = new AttributesImpl();
        attribs.addAttribute(null, ParserConstants.XML_ATTR_ID,
            ParserConstants.XML_ATTR_ID, "string", "object");
        attribs.addAttribute(null, ParserConstants.XML_ATTR_TYPE,
            ParserConstants.XML_ATTR_TYPE, "string",
            "junitx.ddtunit.resources.SimpleVO");
        this.engine.processStartElement("obj", attribs, Engine.LEVEL_TEST_OBJ);
        AttributesImpl attribs2 = new AttributesImpl();
        attribs2.addAttribute(null, ParserConstants.XML_ATTR_ID,
            ParserConstants.XML_ATTR_ID, "string", "object");
        attribs2.addAttribute(null, ParserConstants.XML_ATTR_TYPE,
            ParserConstants.XML_ATTR_TYPE, "string", "java.lang.String");
        this.engine.processStartElement("stringValue", attribs2,
            Engine.LEVEL_TEST_OBJ + 1);
        this.engine.processCharacters(testContent.toCharArray(), 0, testContent
            .length());
        this.engine.processEndElement("stringValue", Engine.LEVEL_TEST_OBJ + 1);
        this.engine.processEndElement("obj", Engine.LEVEL_TEST_OBJ);
    }
}