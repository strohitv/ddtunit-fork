//$Id: SimpleTypeSafeEnum.java 131 2005-04-06 22:33:47Z jg_hamburg $
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
 * Class represents a typesafe enumeration class example for test purposes.
 * 
 * @author jg
 */
public class SimpleTypeSafeEnum {
    private String text;

    public final static SimpleTypeSafeEnum FIRST_ENUM = new SimpleTypeSafeEnum(
            "first");

    public final static SimpleTypeSafeEnum SECOND_ENUM = new SimpleTypeSafeEnum(
            "second");

    public final static SimpleTypeSafeEnum THIRD_ENUM = new SimpleTypeSafeEnum(
            "third");

    private SimpleTypeSafeEnum() {
        // no special init
    }

    private SimpleTypeSafeEnum(String text) {
        this.text = text;
    }

    /**
     * Returns <code>true</code> if this <code>SimpleTypeSafeEnum</code> is
     * the same as the o argument.
     * 
     * @return <code>true</code> if this <code>SimpleTypeSafeEnum</code> is
     *         the same as the o argument.
     */
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o.getClass() != getClass()) {
            return false;
        }
        SimpleTypeSafeEnum castedObj = (SimpleTypeSafeEnum) o;
        return ((this.text == null ? castedObj.text == null : this.text
            .equals(castedObj.text)));
    }

    /**
     * Override hashCode.
     * 
     * @return the Objects hashcode.
     */
    public int hashCode() {
        int hashCode = 1;
        hashCode = 31 * hashCode + (text == null ? 0 : text.hashCode());
        return hashCode;
    }
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("SimpleTypeSafeEnum: ");
        buffer.append(text);
        return buffer.toString();
    }
}
