//$Id: DDTTestDataExceptionTest.java 276 2007-06-18 22:07:19Z jg_hamburg $
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

import junit.framework.TestCase;
import junitx.ddtunit.util.ClassAnalyser;
import junitx.ddtunit.util.DDTConfiguration;
import junitx.framework.Assert;

/**
 * Test exceptional behavior of DDTDataRepository.
 * 
 * @author jg
 */
public class DDTTestDataExceptionTest extends TestCase {

	private static final String LF = "\n";

	/**
	 * Checks if a DDTTestDataException is raised if no dataSet is found.
	 */
	public void testRetrievalOfEmptyClusterDataSet() {
		String resource = "/resourceThatDoesNotExists.mypost";
		String clusterId = "myClusterIdThatDoesNotExists";
		try {
			DDTDataRepository.getInstance().get(resource, clusterId);
			fail("A DDTTestDataException should be thrown on empty lusterDataSet retrieval");
		} catch (DDTTestDataException ex) {
			String expectedMessage = "Error on behalf of xml test resource."
					+ LF
					+ "Caused by:java.io.FileNotFoundException - /resourceThatDoesNotExists.mypost (No such file or directory)";
			assertEquals("Wrong message of caught exception", expectedMessage,
					ex.getMessage());
		}
	}

	/**
	 * Check that a DDTTestDataException is raised if a parsing error occures.<br/>
	 * This test will allways be true on non-validating parsers.<br/> On use of
	 * validating parser this test throws an exception that actually will be
	 * caught by DDTUnit as defined in xml resource.
	 */
	public void testParsingErrorBehavior() {
		String resource = ResourceNameFactory.getInstance().getName(
				ClassAnalyser.classPackage(this), "DefectXMLTest");
		assertEquals("Wrong resource path",
				"/junitx/ddtunit/data/DDT-DefectXMLTest.xml", resource);
		String clusterId = "DbRepositoryAccessTest";
		try {
			DDTDataRepository.getInstance().get(resource, clusterId);
			DDTConfiguration config = DDTConfiguration.getInstance();
			if (config.isActiveParserValidation()
					&& config.isActiveXmlValidation()) {
				fail("A DDTTestDataException should be thrown on empty ClusterDataSet retrieval");
			}
		} catch (DDTTestDataException ex) {
			String expectedMessage = "No testdata provided for class id 'DbRepositoryAccessTest' in testresource '/junitx/ddtunit/data/DDT-DefectXMLTest.xml'"
					+ LF
					+ "Check if referred class id in xml resources matches definition of "
					+ LF
					+ " initTestData(resource, classId) inside of your testclass.";
			Assert.assertEquals("Wrong message of caught exception",
					expectedMessage, ex.getMessage());
		}
	}
}
