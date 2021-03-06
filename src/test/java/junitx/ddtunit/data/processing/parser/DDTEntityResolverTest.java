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
package junitx.ddtunit.data.processing.parser;

import java.io.IOException;

import junit.framework.TestCase;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author jg
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DDTEntityResolverTest extends TestCase {
    private EntityResolver resolver;

    /**
     * @param name of testmethod to run
     */
    public DDTEntityResolverTest(String name) {
        super(name);
    }

    /**
     * DOCUMENT ME!
     */
    public void setUp() {
        resolver = new EntityResolver();
    }

    /**
     * DOCUMENT ME!
     * 
     * @throws SAXException DOCUMENT ME!
     * @throws IOException DOCUMENT ME!
     */
    public void testSystemIdMapping() throws SAXException, IOException {
        InputSource source = resolver.resolveEntity(null, "mytest");

        assertNull("InputSource should be null", source);
    }

    /**
     * DOCUMENT ME!
     * 
     * @throws SAXException DOCUMENT ME!
     * @throws IOException DOCUMENT ME!
     */
    public void testDDTSystemIdMapping() throws SAXException, IOException {
        InputSource source = resolver.resolveEntity(null, ParserImpl.XSD_URL);

        assertNotNull("InputSource should not be null", source);
        assertNotNull("Wrong systenId found", source.getSystemId());
        assertTrue("Wrong systemId found", source.getSystemId().indexOf(
            ParserImpl.XSD_RESOURCE_PATH) > -1);
    }
}