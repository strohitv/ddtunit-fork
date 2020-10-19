//$Id: ParserConstants.java 218 2006-03-02 23:23:29Z jg_hamburg $
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

/**
 * Class containing all constants used in parsing process
 * 
 * @author jg
 */
public final class ParserConstants {
    public static final String XML_ELEM_RESOURCES = "resources";

    public static final String XML_ELEM_CLUSTER = "cluster";

    public static final String XML_ELEM_GROUP = "group";

    public static final String XML_ELEM_TEST = "test";

    public static final String XML_ELEM_OBJ = "obj";

    public static final String XML_ELEM_OBJS = "objs";

    public static final String XML_ELEM_EXCEPTION = "exception";

    public static final String XML_ELEM_ASSERT = "assert";

    public static final String XML_ELEM_ASSERTS = "asserts";

    public static final String XML_ATTR_ID = "id";

    public static final String XML_ATTR_TYPE = "type";

    public static final String XML_ATTR_ACTION = "action";

    public static final String XML_ATTR_CONTENT = "content";

    public static final String XML_ATTR_HINT = "hint";

    public static final String[] XML_IGNORE_ELEM = new String[] {
            XML_ELEM_OBJS, XML_ELEM_ASSERTS };

    public static final String XML_ATTR_PICDATA = "picdata";

    public static final String XML_ATTR_REFID = "refid";

    public final static String UNKNOWN = "!#!UNKNOWN!#!";

    public static final String XML_ATTR_VALUETYPE = "valuetype";

    public static final String XML_ELEM_ITEM = "item";

    public static final String XML_ATTR_KEYTYPE = "keytype";

    public static final String XML_ATTR_BASEID = "baseid";

    /**
     * Default constructor defined private. Only constants allowed in class
     */
    private ParserConstants() {
        // no special initialization
    }
}