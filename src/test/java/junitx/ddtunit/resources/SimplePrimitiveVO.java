//$Id: SimplePrimitiveVO.java 178 2005-09-19 22:34:46Z jg_hamburg $
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
 * Example test resource representing a set of value objects usually used as
 * transport medium between different architectural system layers.
 * 
 * @author jg
 */
public class SimplePrimitiveVO {
    private static final String LF = System.getProperty("line.separator");

    private int integerValue;

    private String stringValue;

    private double doubleValue;

    private SimplePrimitiveVO simpleVO;

    /**
     * Default constructor.
     */
    public SimplePrimitiveVO() {
        // no special initialization neccessary
    }

    /**
     * 
     * @param text
     */
    public SimplePrimitiveVO(String text) {
        this.stringValue = text;
    }

    /**
     * 
     * @param text
     * @param intValue
     * @param doubleValue
     */
    public SimplePrimitiveVO(String text, int intValue, double doubleValue) {
        this.stringValue = text;
        this.integerValue = intValue;
        this.doubleValue = doubleValue;
    }

    /**
     * Overwrite toString() method with instance specific information
     */
    public String toString() {
        StringBuffer sb = new StringBuffer("SimpleVO:");

        sb.append(LF).append("  textValue=\"").append(stringValue).append("\"");
        sb.append(LF).append("  integerValue=\"").append(integerValue).append(
            "\"");
        sb.append(LF).append("  doubleValue=\"").append(doubleValue).append(
            "\"");

        return sb.toString();
    }

    /**
     * overwrite default equals() method
     */
    public boolean equals(Object object) {
        boolean check = false;

        if (SimplePrimitiveVO.class.isInstance(object)) {
            SimplePrimitiveVO vo = (SimplePrimitiveVO) object;

            if ((this.integerValue == vo.getIntegerValue())
                    && (this.doubleValue == vo.getDoubleValue())
                    && (((this.stringValue != null) && this.stringValue
                        .equals(vo.getStringValue())) || ((this.stringValue == null) && (vo
                        .getStringValue() == null)))) {
                check = true;
            }
        }

        return check;
    }

    /**
     * Overwrite default hashCode()
     */
    public int hashCode() {
        final int CONST_VAL = 42;
        int hashVal = CONST_VAL + this.stringValue.hashCode();

        hashVal = (CONST_VAL * hashVal) + this.integerValue;
        hashVal = (CONST_VAL * hashVal)
                + (new Double(this.doubleValue).intValue());

        return hashVal;
    }

    /**
     * @return Returns the doubleValue.
     */
    public double getDoubleValue() {
        return doubleValue;
    }

    /**
     * @param doubleValue The doubleValue to set.
     */
    public void setDoubleValue(double doubleValue) {
        this.doubleValue = doubleValue * 10.0;
    }

    /**
     * @return Returns the integerValue.
     */
    public int getIntegerValue() {
        return integerValue;
    }

    /**
     * @param integerValue The integerValue to set.
     */
    public void setIntegerValue(int integerValue) {
        this.integerValue = integerValue * 10;
    }

    /**
     * @return Returns the stringValue.
     */
    public String getStringValue() {
        return stringValue;
    }

    /**
     * @param stringValue The stringValue to set.
     */
    public void setStringValue(String stringValue) {
        this.stringValue = stringValue + "XXX";
    }
}