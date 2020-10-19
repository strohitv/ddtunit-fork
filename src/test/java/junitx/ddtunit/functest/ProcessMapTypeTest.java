//$Id: ProcessMapTypeTest.java 264 2007-01-21 23:09:24Z jg_hamburg $
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junitx.ddtunit.DDTTestCase;
import junitx.ddtunit.resources.ComplexVO;

/**
 * Test class for checking read facilities of map type xml resource objects.
 * 
 * @author jg
 */
public class ProcessMapTypeTest extends DDTTestCase {

	/*
	 * (non-Javadoc)
	 * 
	 * @see junitx.ddtunit.DDTTestCase#initContext()()
	 */
	protected void initContext() {
		initTestData("ProcessMapTypeTest", "ProcessMapTypeTest");
	}

	/**
	 * Test reading simple <code>java.util.HashMap</code> object from xml
	 * resource as base object.
	 */
	public void testReadMap() {
		Map map = (Map) getObject("myMap");
		assertNotNull("Map should not be null", map);
		addObjectToAssert("count", new Integer(map.size()));
		Object key = getObject("key");
		addObjectToAssert("expected", map.get(key));
	}

	public void testReadSpecialMap() {
		Map map = (HashMap) getObject("myMap");
		addObjectToAssert("result", map);
	}

	/**
	 * Test reading of <code>java.util.Map</code> as field of complex data
	 * type.
	 */
	public void testReadMapAsField() {
		ComplexVO complexVO = (ComplexVO) getObject("complexVO");
		Map mapField = complexVO.getMap();
		addObjectToAssert("count", new Integer(mapField.size()));
		Object key = getObject("key");
		addObjectToAssert("expected", mapField.get(key));
	}

	/**
	 * Test reading of <code>java.util.Map</code> as member of
	 * <code>java.util.Collection</code> type.
	 */
	public void testReadMapAsCollectionMember() {
		List list = (List) getObject("list");
		assertNotNull("List should not be null", list);
		assertEquals("Wrong number of collection entries",
				getObject("listCount"), new Integer(list.size()));
		Map mapField = (Map) list.get(0);
		addObjectToAssert("count", new Integer(mapField.size()));
		Object key = getObject("key");
		addObjectToAssert("expected", mapField.get(key));
	}

	public void testArrayFromMap() {
		Map map = (Map) getObject("myMap");
		addObjectToAssert("count", new Integer(map.size()));
		addObjectToAssert("result", map.get(getObject("key")));
	}

}