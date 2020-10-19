//$Id: TypedObject.java 204 2005-12-11 17:28:53Z jg_hamburg $
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
package junitx.ddtunit.data;

/**
 * Class represents objects that specify type and value separately. This is
 * especially usefull during operation with null object references.
 * 
 * @author jg
 */
public class TypedObject implements Cloneable {
    public final static String UNKNOWN_TYPE = "UnknownType";

    private String id;

    private String type;

    private Object value;

    public TypedObject(String id) {
        this(id, UNKNOWN_TYPE);
    }

    public TypedObject(String id, String type) {
        if (id == null || "".equals(id)) {
            // @TODO check if condition is correct || type == null ||
            // "".equals(type)) {
            throw new IllegalArgumentException(
                    "id and type must be specified on TypedObject");
        }
        this.id = id;
        if (type == null || "".equals(type)) {
            this.type = UNKNOWN_TYPE;
        } else {
            this.type = type;
        }
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    /**
     * Override hashCode.
     * 
     * @return the Objects hashcode.
     */
    public int hashCode() {
        int hashCode = 1;
        hashCode = 31 * hashCode + (id == null ? 0 : id.hashCode());
        hashCode = 31 * hashCode + (type == null ? 0 : type.hashCode());
        hashCode = 31 * hashCode + (value == null ? 0 : value.hashCode());
        return hashCode;
    }

    /**
     * Returns <code>true</code> if this <code>TypedObject</code> is the
     * same as the o argument.
     * 
     * @return <code>true</code> if this <code>TypedObject</code> is the
     *         same as the o argument.
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        TypedObject castedObj = (TypedObject) obj;
        return ((this.id == null ? castedObj.id == null : this.id
            .equals(castedObj.id))
                && (this.type == null ? castedObj.type == null : this.type
                    .equals(castedObj.type)) && (this.value == null ? castedObj.value == null
                : this.value.equals(castedObj.value)));
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setType(String type) {
        if (type == null) {
            throw new NullPointerException(
                    "TypedObject.type is not allowed to be null!");
        }
        this.type = type;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[TypedObject:");
        buffer.append(" id: ");
        buffer.append(id);
        buffer.append(" type: ");
        buffer.append(type);
        buffer.append("]");
        return buffer.toString();
    }

    public Object clone() {
        TypedObject newObj = new TypedObject(this.id, this.type);
        newObj.setValue(this.value);
        return newObj;
    }
}