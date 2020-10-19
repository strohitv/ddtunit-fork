//$Id: AssertObject.java 253 2006-09-11 21:15:25Z jg_hamburg $
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

import java.util.Set;

import junitx.ddtunit.util.ClassAnalyser;

/**
 * @author jg
 */
public abstract class AssertObject extends TypedObject {

    protected String action;

    protected Object actualObject;

    protected String actualType;

    protected boolean actualObjectSet = false;

    protected boolean markAsProcessed = false;

    /**
     * @param id
     * @param type
     */
    public AssertObject(String id, String type, String action) {
        super(id, type);
        if (action != null && !"".equals(action)) {
            this.action = action;
        } else {
            throw new IllegalArgumentException("An action must be specified");
        }
    }

    /**
     * Process assertion specified in this object. Should throw exception on
     * assert failure.
     */
    abstract public void validate(boolean mark);

    public String getAction() {
        return action;
    }

    public Object getActualObject() {
        return actualObject;
    }

    /**
     * Associate actual and expected object. <br/>If actual and expected object
     * are not of same class type an exception is thrown
     * 
     * @param actualObject
     */
    public void setActualObject(Object actualObject) {
        // check for identical types of expected and
        if (actualObject != null) {
            this.actualType = actualObject.getClass().getName();
            // if (!validateSubType(actualObject.getClass())) {
            // throw new DDTException("Class types of actual ("
            // + this.actualType + ") is no subtype of expected ("
            // + getType() + ") objects in assert '" + this.getId()
            // + "'.");
            // }
        }
        this.actualObject = actualObject;
        this.actualObjectSet = true;
    }

    private boolean validateSubType(Class actualClazz) {
        boolean check = false;
        try {
            Class expectedClazz = Class.forName(getType());
            Set superSet = ClassAnalyser.getSuperElements(actualClazz);
            if (this.actualType.equals(getType())
                    || superSet.contains(expectedClazz)) {
                check = true;
            }
        } catch (Exception e) {
            // ignore exception: validation is false
        }
        return check;
    }

    /**
     * @return
     */
    protected String getActualType() {
        return this.actualType;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("Assert(").append(this.getId())
            .append("):");
        return sb.toString();
    }
}
