// $Id: DbRepositoryAccessTest.java 256 2006-10-23 21:56:10Z jg_hamburg $
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
package junitx.ddtunit.data.db;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import junitx.ddtunit.DDTException;
import junitx.ddtunit.DDTTestCase;

/**
 * Test class to verify simple execution of JUnit TestCase like asserts.
 * 
 * @author jg
 * 
 */
public class DbRepositoryAccessTest extends DDTTestCase {
    /**
     * @param name
     */
    public DbRepositoryAccessTest(String name) {
        super(name);
    }

    /**
     * initialize testmethod environment before every execution
     */
    public void setUp() {
    }

    /**
     * Define tests to run
     * 
     * @return
     */
    public static Test suite() {
        TestSuite ts = new TestSuite();

        ts.addTestSuite(DbRepositoryAccessTest.class);

        return ts;
    }

    /**
     * Execute simple valid JUnit assert methods
     */
    public void testSimpleJUnitAssert() {
        assertTrue("Should be true", true);
        assertNull("Should be null", null);
        assertEquals("Should be equal", "xxx", "xxx");
    }

    /**
     * @see junitx.ddtunit.DDTTestCase#initTestData()
     */
    protected void initContext() {
        initTestData("/db:myDataBaseAccess", "SimpleDDTUnitTest");
    }

    /**
     * Test retrieving object on a per method-test basis
     */
    public void testRetrieveObjectData() {
        Object obj = getObject("myObj");

        assertNotNull("Method-Test object should not be null", obj);
        addObjectToAssert("result", obj);
    }

    /**
     * Test retrieving object on a per method-test basis
     */
    public void testRetrieveAssertData(){
        Object obj = getObject("myObj");

        assertNotNull("Method-Test object should not be null", obj);
        if ("Hallo obj4".equals(obj)){
            throw new DDTException("Hallo Exception");
        }
        addObjectToAssert("result", obj);
    }

    public void testCreateXmlResource() throws Exception {
        DbDataRestorer restorer = new DbDataRestorer();
        System.err.println(restorer.createXml("SimpleDDTUnitTest"));
    }

    /**
     * Execute TestCase using a JUnit TestRunner.
     * 
     * @param args
     */
    public static void main(String[] args) {
        TestRunner.run(DbRepositoryAccessTest.suite());
    }
}
