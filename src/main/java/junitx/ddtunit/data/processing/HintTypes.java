//$Id: HintTypes.java 206 2005-12-11 17:45:24Z jg_hamburg $
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
 * Typesafe enumeration class containing all supported hint values used in xml
 * data resource by xml attribute hint.
 * 
 * @author jg
 */
class HintTypes {
    /**
     * FIELDS hint specifies all classes that can be created by using
     * reflection.
     */
    public final static HintTypes FIELDS = new HintTypes("fields");

    /**
     * Collection hint specifies all classes that can are implementations of
     * <code>java.util.Collection</code> interface and have an appropriate
     * list or set character.
     */
    public final static HintTypes COLLECTION = new HintTypes("collection");

    /**
     * Collection hint specifies all classes that can are implementations of
     * <code>java.util.Collection</code> interface and have an appropriate
     * list or set character.
     */
    public final static HintTypes MAP = new HintTypes("map");

    /**
     * Signature hint specifies a <code>java.util.List</code> that will be
     * used for method invokation
     */
    public final static HintTypes ATTRLIST = new HintTypes("attrlist");

    /**
     * Constructor hint specifies the creation of objects.
     */
    public final static HintTypes CONSTRUCTOR = new HintTypes("constructor");

    /**
     * Constant hint specifies the use of a static field of specified class.
     */
    public static final HintTypes CONSTANT = new HintTypes("constant");

    /**
     * Date hint specifies the use of a static field of specified class.
     */
    public static final HintTypes DATE = new HintTypes("date");

    /**
     * Bean hint specifies the use of java bean based setter methods to populate
     * fields
     */
    public static final HintTypes BEAN = new HintTypes("bean");

    /**
     * Array hint specifies the use of Object[] arrays
     */
    public static final HintTypes ARRAY = new HintTypes("array");

    public static final HintTypes INTERNAL_MAPENTRY = new HintTypes("mapentry");

    public static final HintTypes CONTENT = new HintTypes("content");

    public static final HintTypes CALL = new HintTypes("call");

    private String hint;

    private HintTypes(String hint) {
        this.hint = hint;
    }

    /**
     * Returns <code>true</code> if this <code>HintTypes</code> is the same
     * as the oobj argument.
     * 
     * @return <code>true</code> if this <code>HintTypes</code> is the same
     *         as the obj argument.
     */
    public boolean equals(Object obj) {
        boolean checked = false;
        if (this == obj) {
            checked = true;
        } else if (obj == null) {
            checked = false;
        } else if (this.toString().equals(obj)) {
            checked = true;
        } else if (obj.getClass() != getClass()) {
            checked = false;
        } else {
            HintTypes castedObj = (HintTypes) obj;
            checked = ((this.hint == null ? castedObj.hint == null : this.hint
                .equals(castedObj.hint)));
        }
        return checked;
    }

    public String toString() {
        return this.hint;
    }

    /**
     * Override hashCode.
     * 
     * @return the Objects hashcode.
     */
    public int hashCode() {
        int hashCode = 1;
        hashCode = 31 * hashCode + (hint == null ? 0 : hint.hashCode());
        return hashCode;
    }
}