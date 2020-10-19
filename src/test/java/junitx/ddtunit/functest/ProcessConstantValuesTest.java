//$Id: ProcessConstantValuesTest.java 366 2009-09-13 12:51:17Z jg_hamburg $
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
package junitx.ddtunit.functest;

import java.util.List;

import junitx.ddtunit.DDTTestCase;
import junitx.ddtunit.resources.MyEnumerator;
import junitx.ddtunit.resources.SimpleVOExtendByEnumerator;

/**
 * Test reading/instanciation of objects by defining a constructor call.
 * 
 * @author jg
 */
public class ProcessConstantValuesTest extends DDTTestCase {
	/**
	 * @param name
	 *            of testmethod to execute
	 */
	public ProcessConstantValuesTest(String name) {
		super(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junitx.ddtunit.DDTTestCase#initContext()
	 */
	protected void initContext() {
		initTestData("ProcessConstantValuesTest", "ProcessConstantValuesTest");
	}

	/**
	 * Test retrieval of objects with different types of constructors. Using
	 * generic and simple structures.
	 */
	public void testConstantsAsObjectsAndFields() {
		Object myObject = getObject("myValue");
		addObjectToAssert("result", myObject);
		addObjectToAssert("type", myObject.getClass().getName());
	}

	/**
	 * Test retrieval of objects with different types of constructors.
	 */
	public void testConstantsAsContainerEntries() {
		List myList = (List) getObject("myValue");
		assertNotNull("Collection should not be null", myList);
		addObjectToAssert("count", new Integer(myList.size()));
		addObjectToAssert("result", myList.get(0));
		addObjectToAssert("type", myList.getClass().getName());
	}

	/**
	 * Test creation of Java 5 Enumeration type
	 */
	public void testJDK5Enumerator() throws Exception {
		MyEnumerator enumer = (MyEnumerator) getObject("enumer");
		addObjectToAssert("result", enumer);
		addObjectToAssert("result1", enumer);
	}
	
	public void testEnumerationElement() throws Exception{
		SimpleVOExtendByEnumerator simpleVO = (SimpleVOExtendByEnumerator) getObject("myVO");
		addObjectToAssert("result1", simpleVO);
		addObjectToAssert("result2", simpleVO.getMyEnum());
	}

}