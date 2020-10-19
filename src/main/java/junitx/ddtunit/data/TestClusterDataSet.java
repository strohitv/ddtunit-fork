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
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jg
 */
public class TestClusterDataSet extends DataSet {
	private Logger log = LoggerFactory.getLogger(TestClusterDataSet.class);

	/**
	 * @param setId
	 *            under which dataset can be retrieved
	 */
	public TestClusterDataSet(String setId, IDataSet parent) {
		super(setId, parent);
	}

	/**
	 * @return number of tests in all groups of this cluster
	 */
	public int size() {
		int count = 0;
		for (Iterator iter = getSubDataIterator(); iter.hasNext();) {
			count += ((TestGroupDataSet) ((Map.Entry) iter.next()).getValue())
					.size();
		}
		return count;
	}

	/**
	 * @return number of testgroup containing testdata
	 */
	public int size(String group) {
		int count = 0;
		if (containsKey(group)) {
			count = ((TestGroupDataSet) get(group)).size();
		}
		return count;
	}

	/**
	 * Check if test exists for specified group
	 * 
	 * @param groupId
	 *            name to check
	 * @param testId
	 *            name to check
	 * @return true if test data is found
	 */
	public boolean containsTest(String groupId, String testId) {
		boolean check = false;
		if (containsKey(groupId)
				&& ((TestGroupDataSet) get(groupId)).containsKey(testId)) {
			check = true;
		}
		return check;
	}

	/**
	 * Retrieve DataSet of specified group and test if exists. <br/>Otherwise
	 * null is returned.
	 * 
	 * @param groupId
	 *            of DataSet to select
	 * @param testId
	 *            of test under group of DataSet to select
	 * 
	 * @return DataSet of specified groupId/testId or null if no DataSet was
	 *         found.
	 */
	public TestDataSet getTestDataSet(String groupId, String testId) {
		if (groupId == null || testId == null) {
			throw new IllegalArgumentException(
					"Xml Ids of <group/> and <test/> must be not null.");
		}
		log.debug("getTestDataSet(" + groupId + ", " + testId + ") - START");
		TestDataSet dataSet = null;
		if ((groupId != null) && containsKey(groupId)) {
			TestGroupDataSet groupDataSet = (TestGroupDataSet) this
					.get(groupId);

			if (testId != null && groupDataSet.containsKey(testId)) {
				dataSet = (TestDataSet) groupDataSet.get(testId);
			}
		}

		log.debug("getTestDataSet(" + groupId + ", " + testId + ") - END");

		return dataSet;
	}

	/**
	 * Get iterator of all tests contained in specified test group
	 * 
	 * @param groupName
	 *            of test group to lookup
	 * @return Iterator or null if no entry is found
	 */
	public Iterator getTestEntries(String groupName) {
		Iterator iter = null;
		if (containsKey(groupName)) {
			iter = ((TestGroupDataSet) get(groupName)).getSubDataIterator();
		}
		return iter;
	}

	/**
	 * Get iterator on collection of all tests contained in specified test group
	 * 
	 * @param groupId
	 *            of test group to lookup
	 * @return Iterator of TestDatSets identified by groupId
	 */
	public Iterator getTestDataSets(String groupId) {
		Iterator dataIterator = null;
		if (containsKey(groupId)) {
			Collection collection = null;
			collection = ((TestGroupDataSet) get(groupId)).getSubDataValues();
			dataIterator = collection.iterator();
		}
		return dataIterator;
	}

	/**
	 * Retrieve object of specidied testdata under testgroup
	 * 
	 * @param groupId
	 *            to search
	 * @param testId
	 *            to search
	 * @param objectId
	 *            to retrieve
	 * @return TypedObject of requested object
	 */
	public TypedObject getObject(String groupId, String testId, String objectId) {
		TypedObject typedObject = this.getTestDataSet(groupId, testId)
				.findObject(objectId);
		return typedObject;
	}

	/**
	 * Retrieve object of requested testdata under testgroup
	 * 
	 * @param groupId
	 *            to search
	 * @param testId
	 *            to search
	 * @param objectId
	 *            to retrieve
	 * @param objectType
	 *            of object to retrieve
	 * @return TypedObject requested
	 */
	public TypedObject getObject(String groupId, String testId,
			String objectId, String objectType) {
		TypedObject typedObject = this.getTestDataSet(groupId, testId)
				.findObject(objectId, objectType);
		return typedObject;
	}

	/**
	 * Retrieve assert object of requested group and test
	 * 
	 * @param groupId
	 *            to search
	 * @param testId
	 *            to search
	 * @param assertId
	 *            to retrieve
	 * @return AssertObject
	 */
	public AssertObject getAssert(String groupId, String testId, String assertId) {
		AssertObject assertObject = this.getTestDataSet(groupId, testId)
				.getAssert(assertId);
		return assertObject;
	}

	/**
	 * Retrieve assert object of requested group and test
	 * 
	 * @param groupId
	 *            to search
	 * @param testId
	 *            to search
	 * @param assertId
	 *            to search
	 * @param assertType
	 *            to search
	 * @return AssertObject
	 */
	public AssertObject getAssert(String groupId, String testId,
			String assertId, String assertType) {
		AssertObject assertObject = this.getTestDataSet(groupId, testId)
				.getAssert(assertId, assertType);
		return assertObject;
	}

	public TypedObjectMap getAssertMap(String groupId, String testId) {
		TypedObjectMap assertMap = this.getTestDataSet(groupId, testId)
				.getAssertMap();
		return (TypedObjectMap) assertMap;
	}

	public List<String> getOrderedTestKeys(String groupName) {
		return ((TestGroupDataSet) get(groupName)).getOrderedSubKeys();
	}
}
