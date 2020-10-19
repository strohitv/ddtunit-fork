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

/**
 * @author jg
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class TestData {
    /**
     * objectId identifying xml object to parse
     */
    public String objectId = "myObj";

    private static final String XML_HEAD = "<?xml version=\"1.0\"?>";

    private static final String RESOURCE_START = "<ddtunit><class id=\"testclass\">"
            + "<method id=\"testmethod\"><test id=\"testcase\"><objs>";

    private static final String RESOURCE_END = "</objs></test></method></class></ddtunit>";

    /**
     * Instanciate TestData - no special processing
     */
    public TestData() {
        // no special processing
    }

    /**
     * DOCUMENT ME!
     * 
     * @param testId DOCUMENT ME!
     * @param testType DOCUMENT ME!
     * @param testValue DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public String getObj(String testId, String testType, String testValue) {
        String objText = "<obj id=\"" + testId + "\" type=\"" + testType
                + "\">" + testValue + "</obj>";

        return XML_HEAD + RESOURCE_START + objText + RESOURCE_END;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param testId DOCUMENT ME!
     * @param testType DOCUMENT ME!
     * @param testValue DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public String getTwoObj(String testId, String testType, String testValue) {
        String objText = "<obj id=\"" + testId + 1 + "\" type=\"" + testType
                + "\">" + testValue + 1 + "</obj>";
        String obj2Text = "<obj id=\"" + testId + 2 + "\" type=\"" + testType
                + "\">" + testValue + 2 + "</obj>";

        return XML_HEAD + RESOURCE_START + objText + obj2Text + RESOURCE_END;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param testId DOCUMENT ME!
     * @param testType DOCUMENT ME!
     * @param testValue DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public String getNObj(String[] testId, String[] testType, String[] testValue) {
        StringBuffer sb = new StringBuffer();

        if ((testId.length == testType.length)
                && (testType.length == testValue.length)) {
            sb.append(XML_HEAD).append(RESOURCE_START);

            for (int i = 0; i < testId.length; i++) {
                String objText = "<obj id=\"" + testId[i] + "\" type=\""
                        + testType[i] + "\">" + testValue[i] + "</obj>";

                sb.append(objText);
            }

            sb.append(RESOURCE_END);
        }

        return sb.toString();
    }
}