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

import java.util.Collection;
import java.util.Iterator;

import junit.framework.AssertionFailedError;
import junitx.ddtunit.DDTException;
import junitx.framework.ArrayAssert;
import junitx.framework.Assert;

/**
 * Class contains all information to execute an assert action between actual and
 * expected object.
 * 
 * @author jg
 */
public class ObjectAsserter extends AssertObject {

    /**
     * @param id of assert
     * @param type of expected object
     * @param action to process
     */
    public ObjectAsserter(String id, String type, String action) {
        super(id, type, action);
    }

    /**
     * defines equals action analogue to JUnit assertEquals
     */
    public static final String ASSERT_ACTION_ISEQUAL = "ISEQUAL";

    /**
     * defines equals action analogue to JUnit assertEquals
     */
    public static final String ASSERT_ACTION_ISNOTEQUAL = "ISNOTEQUAL";

    /**
     * defines equals action analogue to JUnit assertSame
     */
    public static final String ASSERT_ACTION_ISSAME = "ISSAME";

    /**
     * defines equals action analogue to JUnit assertEquals
     */
    public static final String ASSERT_ACTION_ISNOTSAME = "ISNOTSAME";

    /**
     * defines equals action analogue to JUnit assertEquals
     */
    public static final String ASSERT_ACTION_ISNULL = "ISNULL";

    /**
     * defines equals action analogue to JUnit assertEquals
     */
    public static final String ASSERT_ACTION_ISNOTNULL = "ISNOTNULL";

    private boolean validated = false;

    public static final String ASSERT_ACTION_ISTRUE = "ISTRUE";

    public static final String ASSERT_ACTION_ISFALSE = "ISFALSE";

    public static final String ASSERT_ACTION_ISGT = "ISGT";

    public static final String ASSERT_ACTION_ISNOTGT = "ISNOTGT";

    public static final String ASSERT_ACTION_ISLT = "ISLT";

    public static final String ASSERT_ACTION_ISNOTLT = "ISNOTLT";

    public static final String ASSERT_ACTION_ISCONTAINEDIN = "ISCONTAINEDIN";

    public static final String ASSERT_ACTION_ISNOTCONTAINEDIN = "ISNOTCONTAINEDIN";

    public static final String ASSERT_ACTION_ISINRANGE = "ISINRANGE";

    public static final String ASSERT_ACTION_ISNOTINRANGE = "ISNOTINRANGE";

    /**
     * Validate expected object against actual object using assert action.
     * 
     * @param mark validation if set to true, else reprocessing is possible.
     */
    public void validate(boolean mark) {
        if (!this.actualObjectSet) {
            throw new DDTException("Actual object for assertion not provided");
        } else if (ASSERT_ACTION_ISEQUAL.equals(this.action.toUpperCase())) {
            this.markAsProcessed = mark;
            isSameType();
            if (this.getActualType() != null
                    && this.getActualType().startsWith("[L")
                    && this.getType().startsWith("[L")) {
                ArrayAssert.assertEquals("Wrong isEqual assert ("
                        + this.getId() + ") on arrays", (Object[]) this
                    .getValue(), (Object[]) this.getActualObject());
            } else {
                if ("java.lang.String".equals(this.getActualType())
                        && "java.lang.String".equals(this.getType())) {
                    String actualText = (String) getActualObject();
                    String expectedText = (String) getValue();
                    String[] actual = null;
                    String[] expected = null;
                    if (actualText != null && actualText.indexOf("\r\n") > -1) {
                        actual = actualText.split("\r\n");
                    } else {
                        actual = actualText.split("\n");
                    }
                    if (expectedText != null
                            && expectedText.indexOf("\r\n") > -1) {
                        expected = expectedText.split("\r\n");
                    } else {
                        expected = expectedText.split("\n");
                    }
                    ArrayAssert.assertEquals(
                        "Wrong isEqual assert on (multiline) string", expected,
                        actual);
                } else {
                    Assert.assertEquals("Wrong isEqual assert (" + this.getId()
                            + ")", this.getValue(), getActualObject());
                }
            }
        } else if (ASSERT_ACTION_ISNOTEQUAL.equals(this.action.toUpperCase())) {
            this.markAsProcessed = mark;
            Assert.assertNotEquals("Wrong isNotEqual assert (" + this.getId()
                    + ")", this.getValue(), getActualObject());
        } else if (ASSERT_ACTION_ISSAME.equals(this.action.toUpperCase())) {
            this.markAsProcessed = mark;
            isSameType();
            Assert.assertSame("Wrong isSame assert (" + this.getId() + ")",
                this.getValue(), getActualObject());
        } else if (ASSERT_ACTION_ISNOTSAME.equals(this.action.toUpperCase())) {
            this.markAsProcessed = mark;
            Assert.assertNotSame("Wrong isNotSame assert (" + this.getId()
                    + ")", this.getValue(), getActualObject());
        } else if (ASSERT_ACTION_ISNULL.equals(this.action.toUpperCase())) {
            this.markAsProcessed = mark;
            isSameType();
            Assert.assertNull("Object should be null on assert ("
                    + this.getId() + ")", getActualObject());
        } else if (ASSERT_ACTION_ISNOTNULL.equals(this.action.toUpperCase())) {
            this.markAsProcessed = mark;
            isSameType();
            Assert.assertNotNull("Object should not be null on assert ("
                    + this.getId() + ")", getActualObject());
        } else if (ASSERT_ACTION_ISTRUE.equals(this.action.toUpperCase())) {
            this.markAsProcessed = mark;
            if (Boolean.class.isInstance(getActualObject())) {
                isSameType();
                Assert.assertTrue("Object should be true on assert ("
                        + this.getId() + ")", ((Boolean) getActualObject())
                    .booleanValue());
            } else {
                throw new UnsupportedOperationException(
                        "Wrong type used under assert action 'ISTRUE':"
                                + getActualObject());
            }
        } else if (ASSERT_ACTION_ISFALSE.equals(this.action.toUpperCase())) {
            this.markAsProcessed = mark;
            if (Boolean.class.isInstance(getActualObject())) {
                isSameType();
                Assert.assertFalse("Object should be false on assert ("
                        + this.getId() + ")", ((Boolean) getActualObject())
                    .booleanValue());
            } else {
                throw new UnsupportedOperationException(
                        "Wrong type used under assert action 'ISFALSE':"
                                + getActualObject());
            }
        } else if (ASSERT_ACTION_ISGT.equals(this.action.toUpperCase())
                || ASSERT_ACTION_ISNOTGT.equals(this.action.toUpperCase())
                || ASSERT_ACTION_ISLT.equals(this.action.toUpperCase())
                || ASSERT_ACTION_ISNOTLT.equals(this.action.toUpperCase())
                || ASSERT_ACTION_ISINRANGE.equals(this.action.toUpperCase())
                || ASSERT_ACTION_ISNOTINRANGE.equals(this.action.toUpperCase())) {
            this.markAsProcessed = mark;
            // if doing range checks do not check expected and actual types
            if (!(this.getValue() instanceof IRange)) {
                isSameType();
            }
            if (Comparable.class.isInstance(this.actualObject)) {
                if (ASSERT_ACTION_ISGT.equals(this.action.toUpperCase())) {
                    if (((Comparable) this.getActualObject()).compareTo(this
                        .getValue()) <= 0) {
                        Assert.fail("Expected action: " + this.getAction()
                                + ". Got: " + this.getActualObject() + " <= "
                                + this.getValue() + " on assert ("
                                + this.getId() + ")");
                    }
                } else if (ASSERT_ACTION_ISNOTGT.equals(this.action
                    .toUpperCase())) {
                    if (((Comparable) this.getActualObject()).compareTo(this
                        .getValue()) > 0) {
                        Assert.fail("Expected action: " + this.getAction()
                                + ". Got: " + this.getActualObject() + " > "
                                + this.getValue() + "on assert ("
                                + this.getId() + ")");
                    }
                } else if (ASSERT_ACTION_ISLT.equals(this.action.toUpperCase())) {
                    if (((Comparable) this.getActualObject()).compareTo(this
                        .getValue()) >= 0) {
                        Assert.fail("Expected action: " + this.getAction()
                                + ". Got: " + this.getActualObject() + " >= "
                                + this.getValue() + " on assert ("
                                + this.getId() + ")");
                    }
                } else if (ASSERT_ACTION_ISNOTLT.equals(this.action
                    .toUpperCase())) {
                    if (((Comparable) this.getActualObject()).compareTo(this
                        .getValue()) >= 0) {
                        Assert.fail("Expected action: " + this.getAction()
                                + ". Got: " + this.getActualObject() + " > "
                                + this.getValue() + " on assert ("
                                + this.getId() + ")");
                    }
                } else if (ASSERT_ACTION_ISINRANGE.equals(this.action
                    .toUpperCase())) {
                    if (!((IRange) this.getValue()).isInRange((Comparable) this
                        .getActualObject())) {
                        Assert.fail("Expected action: " + this.getAction()
                                + ". Got: " + this.getActualObject()
                                + " not in " + this.getValue() + " on assert ("
                                + this.getId() + ")");
                    }
                } else if (ASSERT_ACTION_ISNOTINRANGE.equals(this.action
                    .toUpperCase())) {
                    if (((IRange) this.getValue()).isInRange((Comparable) this
                        .getActualObject())) {
                        Assert.fail("Expected action: " + this.getAction()
                                + ". Got: " + this.getActualObject() + " in "
                                + this.getValue() + " on assert ("
                                + this.getId() + ")");
                    }
                }
            } else {
                throw new DDTException(
                        "Asserted type does not implement Comparable interface: "
                                + this.getType());
            }
        } else if (ASSERT_ACTION_ISCONTAINEDIN
            .equals(this.action.toUpperCase())
                || ASSERT_ACTION_ISNOTCONTAINEDIN.equals(this.action
                    .toUpperCase())) {
            this.markAsProcessed = mark;
            boolean match = false;
            RuntimeException assertEx = null;
            if (ASSERT_ACTION_ISCONTAINEDIN.equals(this.action.toUpperCase())) {
                // actual type == array type do separate contain check
                if (this.getActualType().startsWith("[L")) {
                    match = isArrayContainedinList(assertEx);
                    if (!match) {
                        throw assertEx;
                    }
                } else {
                    Assert.assertTrue(
                        "Object should be member in List on assert ("
                                + this.getId() + ")", ((Collection) this
                            .getValue()).contains(getActualObject()));
                }
            } else {
                // actual type == array type do separate contain check
                if (this.getActualType().startsWith("[L")) {
                    match = isArrayContainedinList(assertEx);
                    if (match) {
                        throw new AssertionFailedError(
                                "Array not found in expected list on assert ("
                                        + this.getId() + ")");
                    }
                } else {
                    Assert.assertFalse(
                        "Object should not be contained in List on assert ("
                                + this.getId() + ")", ((Collection) this
                            .getValue()).contains(getActualObject()));
                }
            }
        } else {
            throw new DDTException("Unsupported assert action \"" + this.action
                    + "\"");
        }
    }

    /**
     * Check if actual object array is contained in expected list of arrays.
     * 
     * @param assertEx is set if no match is found during check
     * @return true, if match is found, false otherwise
     */
    private boolean isArrayContainedinList(RuntimeException assertEx) {
        boolean match = false;
        for (Iterator iter = ((Collection) this.getValue()).iterator(); iter
            .hasNext();) {
            Object array = iter.next();
            try {
                ArrayAssert.assertEquals((Object[]) array, (Object[]) this
                    .getActualObject());
                match = true;
                break;
            } catch (RuntimeException ex) {
                // ignore error, there might be another match
                assertEx = ex;
            }
        }
        return match;
    }

    /**
     * 
     */
    private void isSameType() {
        Assert.assertEquals("Class type differs between assert objects, ",
            getType(),
            (this.actualObject != null ? getActualType() : getType()));
    }

    /**
     * @return true if this assertion is allready processed
     */
    public boolean isValidated() {
        return validated;
    }

    public Object clone() {
        ObjectAsserter newObj = new ObjectAsserter(this.getId(),
                this.getType(), this.getAction());
        newObj.setValue(this.getValue());
        return newObj;
    }

}