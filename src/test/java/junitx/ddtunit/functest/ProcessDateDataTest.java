//$Id: ProcessDateDataTest.java 278 2007-07-01 19:58:40Z jg_hamburg $
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

import junit.framework.Test;
import junit.framework.TestSuite;
import junitx.ddtunit.DDTTestCase;

/**
 * Test simple data types like Integer, String, Double, Date.
 * 
 * @author jg
 */
public class ProcessDateDataTest extends DDTTestCase {

	/**
	 * @param name
	 */
	public ProcessDateDataTest(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(ProcessDateDataTest.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junitx.ddtunit.DDTTestCase#initContext()
	 */
	protected void initContext() {
		initTestData("ProcessDateDataTest", "ProcessDateDataTest");
	}

	/**
	 * Test reading and asserting String objects.
	 */
	public void testDateType() {
		addObjectToAssert("result", getObject("myDate"));
	}

	/**
	 * Test reading and asserting String objects.
	 */
	public void testSpecificDateType() {
		addObjectToAssert("result", getObject("myDate"));
	}
}
