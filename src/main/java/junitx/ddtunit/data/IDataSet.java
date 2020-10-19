//$Id: IDataSet.java 256 2006-10-23 21:56:10Z jg_hamburg $
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

public interface IDataSet {

    /**
     * add new entry to local object map
     * 
     * @param id
     * @param entry
     */
    public abstract void putObject(String id, TypedObject entry);

    /**
     * Retrieve object entry from local object map
     * 
     * @param id
     * @param type
     * @return selected object or null if not found
     */
    public abstract TypedObject getObject(String id, String type);

    public abstract TypedObject findObject(String id, String type);

    /**
     * Retrieve object entry from local object map
     * 
     * @param id
     * @return selected object
     * @throws junitx.ddtunit.DDTException if multiple entries for id exist
     */
    public abstract TypedObject getObject(String id);

    public abstract TypedObject findObject(String id);

    /**
     * 
     * @param id
     * @param dataSet
     */
    public abstract void put(String id, IDataSet dataSet);

    /**
     * Retrieve DataSet specified by id
     * 
     * @param id
     * @return dataset or null if nothing found
     */
    public abstract IDataSet get(String id);

    /**
     * Check if provided key is instained in sub object map
     * 
     * @param key
     * @return true if found else false
     */
    public abstract boolean containsKey(String key);

    /**
     * Retrieve iterator over all keys provided by sub datasets
     * 
     * @return Iterator to process all keys
     */
    abstract Iterator getSubDataIterator();

    /**
     * Retrieve iterator over all dataSets provided as subelements of this
     * dataSet.
     * 
     * @return Iterator to process all subDataSets
     */
    abstract Collection getSubDataValues();

    /**
     * Retrieve number of sub datasets
     * 
     * @return count of sub datasets
     */
    public abstract int size();

    /**
     * Retrieve id of DataSet
     * @return id of dataset
     */
    public abstract String getId();

}