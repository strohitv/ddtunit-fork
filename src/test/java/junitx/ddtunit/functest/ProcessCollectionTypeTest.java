//$Id: ProcessCollectionTypeTest.java 342 2008-02-20 22:38:16Z jg_hamburg $
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

import java.util.Collection;
import java.util.List;
import java.util.Vector;

import junitx.ddtunit.DDTTestCase;

/**
 * Test class for checking read facilities of collection type xml resource
 * objects.
 * 
 * @author jg
 */
public class ProcessCollectionTypeTest extends DDTTestCase {

	/*
	 * (non-Javadoc)
	 * 
	 * @see junitx.ddtunit.DDTTestCase#initContext()()
	 */
	protected void initContext() {
		initTestData("ProcessCollectionTypeTest", "ProcessCollectionTypeTest");
	}

	/**
	 * Test reading simple <code>java.util.Vector</code> object from xml
	 * resource.
	 * 
	 */
	public void testReadVector() {
		Vector vector = (Vector) getObject("myVector");
		assertNotNull("Vector should not be null", vector);
		addObjectToAssert("count", new Integer(vector.size()));
		if (vector.size() > 0) {
			addObjectToAssert("expected", vector.get(1));
		} else {
			addObjectToAssert("expected", null);
		}
	}

	public void testReadNullVector() {
		Vector vector = (Vector) getObject("myVector");
		addObjectToAssert("result", vector);
	}

	/**
	 * Test reading of nested collections.
	 * 
	 */
	public void testReadNestedCollection() {
		Vector vector = (Vector) getObject("myVector");
		assertNotNull("Vector should not be null", vector);
		assertObject("count", new Integer(vector.size()), false);
		List first = (List) vector.get(0);
		List second = (List) vector.get(1);
		assertObject("count", new Integer(first.size()), false);
		assertObject("count", new Integer(second.size()));
	}

	/**
	 * Test reading of nested collections.
	 * 
	 */
	public void testReadNestedMixedType() {
		Vector vector = (Vector) getObject("myVector");
		assertNotNull("Vector should not be null", vector);
		assertObject("count", new Integer(vector.size()), false);
		List first = (List) vector.get(0);
		addObjectToAssert("count", new Integer(first.size()));
		addObjectToAssert("firstEntry", first.get(0));
		addObjectToAssert("secondEntry", vector.get(1));
	}

	public void testArrayFromCollection() {
		List list = (List) getObject("myList");
		addObjectToAssert("count", new Integer(list.size()));
		addObjectToAssert("result", list.get(1));
	}

	/**
	 * Test assert behavior with empty nested collection as reported by bug
	 * 1773974
	 */
	public void testDoubleNestedCollectionInAssert() throws Exception {
		Collection container = (Collection) getObject("object1");
		addObjectToAssert("result", container);
	}
	
	public void testIdentifyCollection() throws Exception {
		Object obj = getObject("myObj");
		addObjectToAssert("result", obj);
	}
	
	/**
	 * Test verifies parsing behavior of Bug 1898143 on sourceforge
	 * @throws Exception
	 */
	public void testNestedVoWithCollection() throws Exception {
		Object obj = getObject("myObj");
		addObjectToAssert("result", obj);
	}
}