//$Id: ActionState.java 220 2006-03-17 00:22:16Z jg_hamburg $
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
 * Class for defining special processing states useful for object instanciation
 */
final class ActionState {
    /**
     * Object parsing type, identifying a standard object to parse
     */
    public final static ActionState OBJECT_CREATION = new ActionState(4,
            "Object Creation Processing");

    /**
     * Assert parsing type, identifying a standard assertion to parse
     */
    public final static ActionState ASSERT_CREATION = new ActionState(5,
            "Assert Creation Processing");

    /**
     * Exception parsing type, identifying a standard expected exception to
     * parse
     */
    public final static ActionState EXCEPTION_CREATION = new ActionState(6,
            "Exception Creation Processing");

    /**
     * XML Content parsing type, identifying xml content to parse
     */
    public final static ActionState CONTENT_CREATION = new ActionState(7,
            "Content Extraction Processing");

    /**
     * Subelement parsing type, identifying a substructure object to parse, e.g.
     * a field of a value object
     */
    public final static ActionState SUBELEMENT_CREATION = new ActionState(8,
            "Subelement Creation Processing");

    /**
     * Collection parsing type, identifying a collection object to parse, e.g. a
     * list of object values
     */
    public final static ActionState COLLECTION_CREATION = new ActionState(10,
            "Collection Creation Processing");

    /**
     * Map parsing type, identifying a map object to parse, e.g. a list of
     * object values
     */
    public final static ActionState MAP_CREATION = new ActionState(11,
            "Map Creation Processing");

    /**
     * Date parsing type, identifying a Date object to parse, e.g. a date time
     * object
     */
    public final static ActionState DATE_CREATION = new ActionState(12,
            "Date Creation Processing");

    public static final ActionState ATTRLIST_CREATION = new ActionState(13,
            "Internal Attribute Creation Processing");

    public static final ActionState ARRAY_CREATION = new ActionState(14,
            "Array Creation Processing");

    public static final ActionState BEAN_CREATION = new ActionState(15,
            "Bean Creation Processing");

    public static final ActionState CALL_CREATION = new ActionState(16,
            "Call Creation Processing");

    public static final ActionState CONSTANT_CREATION = new ActionState(17,
            "Constant Creation Process");

    private int id;

    private String name;

    private ActionState() {
        // no special initialization
    }

    private ActionState(int id, String name) {
        if (id < 0 || name == null || name.compareTo("") == 0) {
            throw new IllegalArgumentException(
                    "Wrong arguments: id >= 0, name not empty.");
        }
        this.id = id;
        this.name = name;
    }

    /**
     * Overwrite toString() method.
     * 
     * @return name of parse type
     */
    public String toString() {
        return this.name;
    }

    /**
     * Returns <code>true</code> if this <code>ActionState</code> is the
     * same as the o argument.
     * 
     * @return <code>true</code> if this <code>ActionState</code> is the
     *         same as the o argument.
     */
    public boolean equals(Object obj) {
        boolean check = false;
        if (obj != null && obj.getClass() == getClass()) {
            ActionState castedObj = (ActionState) obj;
            check = ((this.id == castedObj.id) && (this.name == null ? castedObj.name == null
                    : this.name.equals(castedObj.name)));
        }
        return check;
    }

    /**
     * Override hashCode.
     * 
     * @return the Objects hashcode.
     */
    public int hashCode() {
        int hashCode = 1;
        hashCode = 31 * hashCode + id;
        hashCode = 31 * hashCode + (name == null ? 0 : name.hashCode());
        return hashCode;
    }
}
