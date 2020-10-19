//$Id: DDTDataRepository.java 351 2008-08-14 20:20:56Z jg_hamburg $
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

import java.util.Map;
import java.util.Set;

import junitx.ddtunit.data.db.DbDataRestorer;
import junitx.ddtunit.util.DDTConfiguration;
import junitx.ddtunit.util.SoftHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data repository containing test data per test class. <br/>Implementation as
 * singleton.
 * 
 * @author jg
 */
public class DDTDataRepository extends DataSet {

	private static DDTDataRepository repository;

	private Map clusterDataSets;

	private Logger log = LoggerFactory.getLogger(DDTDataRepository.class);

	/**
	 * Default constructor
	 * 
	 * @param hardCacheSize
	 *            defines number of elements that can not be deleted by garbadge
	 *            collection
	 */
	private DDTDataRepository(int hardCacheSize) {
		super("DDTUnitDataRepository", null);
		this.clusterDataSets = new SoftHashMap(hardCacheSize);
	}

	/**
	 * @return DDTDataRepository as singleton
	 */
	public static DDTDataRepository getInstance() {
		if (repository == null) {
			repository = new DDTDataRepository(DDTConfiguration.getInstance()
					.getHardCacheSize());
		}
		return repository;
	}

	/**
	 * Check existance of testdata concerning provided class data.
	 * 
	 * @param classId
	 *            of class data used for test
	 * @return true if data is found
	 */
	public boolean containsKey(String classId) {
		boolean contains = false;
		contains = this.clusterDataSets.containsKey(classId);
		log.debug("containsKey(" + classId + ")=" + contains);
		return contains;
	}

	/**
	 * Add {@link TestClusterDataSet}to repository.
	 * 
	 * @param classId
	 * @param dataSet
	 */
	public void put(String classId, IDataSet dataSet) {
		this.clusterDataSets.put(classId, dataSet);
		log.debug("put(" + classId + ") - size " + this.size());
	}

	/**
	 * Get TestClusterDataSet associated to the specified method of a class.
	 * 
	 * @param resource
	 *            name of resource of test class data
	 * @param clusterId
	 *            to identify dataSet used unter test
	 * @return DataSet to process
	 */
	public TestClusterDataSet get(String resource, String clusterId) {
		if (!containsKey(clusterId)) {
			restore(resource, clusterId);
		}
		return (TestClusterDataSet) this.clusterDataSets.get(clusterId);
	}

	/**
	 * Restore test object definition from provided xml resource
	 * 
	 * @param resource
	 *            name of xml file
	 * @param clusterId
	 *            specifiing the testdata cluster to process from within xml
	 *            resource
	 */
	public IDataSet restore(String resource, String clusterId) {
		try {
			// check for special key in resource - db:
			IDataSet dataSet = null;
			if (resource.startsWith("/db:")) {
				IDataSetSerializer dbRetriever = new DbDataRestorer();
				dataSet = dbRetriever.restore(resource, clusterId);
			} else {
				IDataSetSerializer xmlRetriever = new XmlDataRestorer(this);
				dataSet = xmlRetriever.restore(resource, clusterId);
			}
			put(clusterId, dataSet);
			log.debug("restore(" + resource + ", " + clusterId + ")");
			return dataSet;
		} catch (DDTTestDataException ex) {
			throw ex;
		} catch (Throwable ex) {
			throw new DDTTestDataException(ex.getMessage(), ex);
		}
	}

	/**
	 * @return number of entries in repository
	 */
	public int size() {
		log.debug("size = " + this.clusterDataSets.size());
		return this.clusterDataSets.size();
	}

	public Set entrySet() {
		return this.clusterDataSets.entrySet();
	}
}
