//$Id: ExceptionAsserter.java 209 2005-12-18 17:45:49Z jg_hamburg $
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

import junit.framework.AssertionFailedError;
import junitx.ddtunit.DDTException;
import junitx.framework.ThrowableAssert;

/**
 * @author jg
 */
public class ExceptionAsserter extends AssertObject {

    /**
     * defines equals action analogue to JUnit assertEquals
     */
    public static final String EXCEPTION_ACTION_ISEQUAL = "ISEQUAL";

    /**
     * defines equals action analogue to JUnit assertEquals
     */
    public static final String EXCEPTION_ACTION_ISSIMILAR = "ISSIMILAR";

    /**
     * defines equals action analogue to JUnit assertEquals
     */
    public static final String EXCEPTION_ACTION_ISINSTANCEOF = "ISINSTANCEOF";

    /**
     * @param assertId
     * @param assertType
     * @param exceptionAction
     */
    public ExceptionAsserter(String exceptionId, String exceptionType,
            String exceptionAction) {
        super(exceptionId, exceptionType, exceptionAction);
    }

    /**
     * validate internal expression.
     * 
     * @param mark as processed if set to true, else multiple validation is
     *        possible.
     * 
     * @see junitx.ddtunit.data.AssertObject#validate()
     */
    public void validate(boolean mark) {
        if (!this.actualObjectSet) {
            throw new DDTException("Actual object for assertion not provided");
        } else if (EXCEPTION_ACTION_ISEQUAL.equals(this.action.toUpperCase())) {
            this.markAsProcessed = mark;
            ThrowableAssert.assertEquals("Unexpected exception",
                (Throwable) getValue(), (Throwable) getActualObject());
        } else if (EXCEPTION_ACTION_ISSIMILAR.equals(this.action.toUpperCase())) {
            this.markAsProcessed = mark;
            ThrowableAssert.assertSimilar("Unexpected exception",
                (Throwable) getValue(), (Throwable) getActualObject());
        } else if (EXCEPTION_ACTION_ISINSTANCEOF.equals(this.action
            .toUpperCase())) {
            this.markAsProcessed = mark;
            try {
                Class expectedClazz = Class.forName(this.getType());
                if (!expectedClazz.isInstance(this.getActualObject())) {
                    throw new AssertionFailedError(
                            "Unexpected exception: expected <"
                                    + this.getType() + "> got <"
                                    + this.getActualType() + ">");
                }
            } catch (ClassNotFoundException ex) {
                throw new DDTException(
                        "Error instanciating expected exception of type "
                                + this.getType(), ex);
            }
        } else {
            throw new DDTException("Unsupported action " + this.action);
        }
    }

    public Object clone() {
        ExceptionAsserter newObj = new ExceptionAsserter(this.getId(), this
            .getType(), this.getAction());
        newObj.setValue(this.getValue());
        return newObj;
    }

}
