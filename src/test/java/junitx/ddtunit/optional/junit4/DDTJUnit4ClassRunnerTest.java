// $Id: SimpleDDTUnitTest.java 220 2006-03-17 00:22:16Z jg_hamburg $
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
package junitx.ddtunit.optional.junit4;

import junit.framework.Test;
import junit.framework.TestSuite;
import junitx.ddtunit.DDTTestCase;
import junitx.ddtunit.optional.junit4.DDTUnit4ClassRunner;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test class to verify simple execution of JUnit TestCase like asserts.
 * 
 * @author jg
 * 
 */
@RunWith(value = DDTUnit4ClassRunner.class)
public class DDTJUnit4ClassRunnerTest extends DDTTestCase {
	final static Logger logger = LoggerFactory.getLogger(DDTJUnit4ClassRunnerTest.class);
	
	@Before
	public void before(){
		logger.info("before");
	}
	
	@After
	public void after(){
		logger.info("after");		
	}
	
	@BeforeClass
	public static void beforeClass() {
		logger.info("beforeClass");		
	}

	@AfterClass
	public static void afterClass() {
		logger.info("afterClass");		
	}
	
	protected void setUp() throws Exception {
		logger.info("setUp()");
	}
	
	protected void tearDown() throws Exception {
		logger.info("tearDown()");
	}
	
	/**
     * @param name
     */
    public DDTJUnit4ClassRunnerTest(String name) {
        super(name);
    }
    
    
    public DDTJUnit4ClassRunnerTest() {
    	
    }

    /**
     * Define tests to run
     * 
     * @return
     */
    public static Test suite() {
        TestSuite ts = new TestSuite();

        ts.addTestSuite(DDTJUnit4ClassRunnerTest.class);

        return ts;
    }

    /**
     * Execute simple valid JUnit assert methods
     */
    @org.junit.Test
    public void simpleJUnitAssert() {
    	logger.info("simpleJUnitAssert");
        assertTrue("Should be true", true);
        assertNull("Should be null", null);
        assertEquals("Should be equal", "xxx", "xxx");
    }

    /**
     * @see junitx.ddtunit.DDTTestCase#initTestData()
     */
    public void initContext() {
        initTestData("DDTJUnit4ClassRunnerTest");
    }

    /**
     * Test retrieving object on a per method-test basis
     */
    @org.junit.Test   
    public void retrieveObjectData() {
    	logger.info("retrieveObjectData");
        String obj = (String) getObject("myObj");

        assertNotNull("Method-Test object should not be null", obj);
        assertTrue("Wrong value for retrieved object", obj.startsWith("My "));
    }

    /**
     * Test retrieving object on a per method-test basis
     */  
    public void retrieveAssertData() {
        String obj = (String) getObject("myObj");
        fail();
        assertNotNull("Method-Test object should not be null", obj);
        assertTrue("Wrong value for retrieved object", obj.startsWith("My "));
        addObjectToAssert("result", obj);
    }

}
