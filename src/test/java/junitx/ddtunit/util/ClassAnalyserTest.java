//$Id: ClassAnalyserTest.java 360 2009-08-09 18:20:30Z jg_hamburg $
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
package junitx.ddtunit.util;

import java.util.Arrays;
import java.util.Set;
import java.util.Vector;

import junit.framework.TestCase;

/**
 * @author jg
 */
public class ClassAnalyserTest extends TestCase {

    /**
     * Constructor for ClassAnalyserTest.
     * 
     * @param arg0
     */
    public ClassAnalyserTest(String arg0) {
        super(arg0);
    }

    public final void testGetSuperElements() {
        Set superList = ClassAnalyser.getSuperElements(Object.class);
        assertEquals("SuperElements of Object should be empty", 0, superList
            .size());
        superList = ClassAnalyser.getSuperElements(String.class);
        assertEquals("SuperElements of String should be empty", 4, superList
            .size());
        superList = ClassAnalyser.getSuperElements(Vector.class);
        int superCount = 9;
        if (System.getProperty("os.name").startsWith("Windows")
                && System.getProperty("java.version").startsWith("1.5")) {
            superCount += 1;
        }
        assertEquals("SuperElements of Object should be empty", superCount,
            superList.size());
    }

    public void testFindInheritedMethod() {
        Class[] args = new Class[0];
        Object method = ClassAnalyser.findMethodByParams(
            "junitx.ddtunit.resources.SimpleVOExtend", "getIntegerValue", args);
        assertNotNull("Method should not be null", method);
    }

    public void testGetPrimitiveClazz() throws Exception {
        Boolean[] booleanArray = { Boolean.TRUE, Boolean.FALSE };
        boolean[] bArray = { true, false };
        Byte[] byteArray = { Byte.valueOf((byte) '1'), Byte.valueOf((byte) '2'),
                Byte.valueOf((byte) '3') };
        byte[] byArray = { '1', '2', '3' };
        Character[] charArray = { Character.valueOf('a'),
                Character.valueOf('b'), Character.valueOf('c') };
        char[] chArray = { 'a', 'b', 'c' };
        Integer[] intArray = { Integer.valueOf(100), Integer.valueOf(200),
                Integer.valueOf(300) };
        int[] iArray = { 100, 200, 300 };
        Long[] longArray = { Long.valueOf(1000L), Long.valueOf(2000),
                Long.valueOf(3000) };
        long[] lArray = { 1000L, 2000L, 3000L };
        Short[] shortArray = { Short.valueOf((short) 1),
                Short.valueOf((short) 2), Short.valueOf((short) 3) };
        short[] sArray = { 1, 2, 3 };
        Double[] doubleArray = { Double.valueOf(1.2), Double.valueOf(2.3),
                Double.valueOf(3.4) };
        double[] dArray = { 1.2, 2.3, 3.4 };
        Float[] floatArray = { Float.valueOf(1.2F), Float.valueOf(2.3F),
                Float.valueOf(3.4F) };
        float[] fArray = { 1.2F, 2.3F, 3.4F };
        assertTrue(Arrays.equals(bArray, (boolean[]) ClassAnalyser
            .createPrimitiveArray(booleanArray)));
        assertTrue(Arrays.equals(byArray, (byte[]) ClassAnalyser
            .createPrimitiveArray(byteArray)));
        assertTrue(Arrays.equals(chArray, (char[]) ClassAnalyser
            .createPrimitiveArray(charArray)));
        assertTrue(Arrays.equals(iArray, (int[]) ClassAnalyser
            .createPrimitiveArray(intArray)));
        assertTrue(Arrays.equals(sArray, (short[]) ClassAnalyser
            .createPrimitiveArray(shortArray)));
        assertTrue(Arrays.equals(lArray, (long[]) ClassAnalyser
            .createPrimitiveArray(longArray)));
        assertTrue(Arrays.equals(dArray, (double[]) ClassAnalyser
            .createPrimitiveArray(doubleArray)));
        assertTrue(Arrays.equals(fArray, (float[]) ClassAnalyser
            .createPrimitiveArray(floatArray)));
    }
}