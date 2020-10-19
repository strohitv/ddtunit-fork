//$Id: DataSet.java 256 2006-10-23 21:56:10Z jg_hamburg $
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Class containing all data objects used for executing a testclass. <br/ > The
 * structure is a direct mapping of the xml resource structure of <br/ >
 * <code>class - method - testcase</code>
 * 
 * @author jg
 */
abstract public class DataSet implements IDataSet {

    /**
     * id to identify the dataset in a cache or repository
     */
    private String dataSetId;

    protected TypedObjectMap objectMap;

    private Map subDataMap;

    private IDataSet parentSet;

    /**
     * @param setId specifies id for identification of dataset
     */
    public DataSet(String setId, IDataSet parent) {
        this.dataSetId = setId;
        this.objectMap = new TypedObjectMap();
        this.subDataMap = new HashMap();
        this.parentSet = parent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see junitx.ddtunit.data.IDataSet#putObject(java.lang.String,
     *      junitx.ddtunit.data.TypedObject)
     */
    public void putObject(String id, TypedObject entry) {
        this.objectMap.put(id, entry);
    }

    /*
     * (non-Javadoc)
     * 
     * @see junitx.ddtunit.data.IDataSet#getObject(java.lang.String,
     *      java.lang.String)
     */
    public TypedObject getObject(String id, String type) {
        return this.objectMap.get(id, type);
    }

    /*
     * (non-Javadoc)
     * 
     * @see junitx.ddtunit.data.IDataSet#getObject(java.lang.String)
     */
    public TypedObject getObject(String id) {
        return this.objectMap.get(id);
    }

    public TypedObject findObject(String id) {
        TypedObject local = getObject(id);
        if (local == null && parentSet != null) {
            local = parentSet.findObject(id);
        }
        return local;
    }

    public TypedObject findObject(String id, String type) {
        if (type == null || TypedObject.UNKNOWN_TYPE.equals(type)) {
            return findObject(id);
        }
        TypedObject local = getObject(id, type);
        if (local == null && parentSet != null) {
            local = parentSet.findObject(id, type);
        }
        return local;
    }

    /*
     * (non-Javadoc)
     * 
     * @see junitx.ddtunit.data.IDataSet#put(java.lang.String,
     *      junitx.ddtunit.data.IDataSet)
     */
    public void put(String id, IDataSet dataSet) {
        this.subDataMap.put(id, dataSet);
    }

    /*
     * (non-Javadoc)
     * 
     * @see junitx.ddtunit.data.IDataSet#get(java.lang.String)
     */
    public IDataSet get(String id) {
        IDataSet dataSet = null;
        if (containsKey(id)) {
            dataSet = (IDataSet) this.subDataMap.get(id);
        }
        return dataSet;
    }

    /*
     * (non-Javadoc)
     * 
     * @see junitx.ddtunit.data.IDataSet#containsKey(java.lang.String)
     */
    public boolean containsKey(String key) {
        return this.subDataMap.containsKey(key);
    }

    /*
     * (non-Javadoc)
     * 
     * @see junitx.ddtunit.data.IDataSet#getSubDataKeyIterator()
     */
    public Iterator getSubDataIterator() {
        return this.subDataMap.entrySet().iterator();
    }

    /*
     * (non-Javadoc)
     * 
     * @see junitx.ddtunit.data.IDataSet#getSubDataValues()
     */
    public Collection getSubDataValues() {
        return this.subDataMap.values();
    }

    /*
     * (non-Javadoc)
     * 
     * @see junitx.ddtunit.data.IDataSet#size()
     */
    public int size() {
        return this.subDataMap.size();
    }

    /**
     * Return human readable description of object.
     * 
     * @return Desciption of object.
     */
    public String toString() {
        StringBuffer info = new StringBuffer("DataSet(").append(this.dataSetId)
            .append("-type=").append(this.getClass().getName());

        return info.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see junitx.ddtunit.data.IDataSet#getId()
     */
    public String getId() {
        return dataSetId;
    }

}