//$Id: TypedObjectMap.java 256 2006-10-23 21:56:10Z jg_hamburg $
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import junitx.ddtunit.DDTException;

/**
 * @author jg
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class TypedObjectMap implements Cloneable {
    private Map objectMap;

    public TypedObjectMap() {
        this.objectMap = new HashMap();
    }

    public void put(String id, TypedObject entry) {
        if (objectMap.containsKey(id)) {
            Object value = objectMap.get(id);
            if (MultiTypeMap.class.isInstance(value)) {
                ((MultiTypeMap) value).put(entry.getType(), entry);
            } else if (TypedObject.class.isInstance(value)) {
                TypedObject valueEntry = (TypedObject) value;
                MultiTypeMap typeMap = new MultiTypeMap();
                typeMap.put(valueEntry.getType(), valueEntry);
                typeMap.put(entry.getType(), entry);
                objectMap.put(id, typeMap);
            } else {
                throw new DDTException("Wrong data in internal Structure");
            }
        } else {
            objectMap.put(id, entry);
        }
    }

    public TypedObject get(String id, String type) {
        TypedObject obj = null;
        if (this.objectMap.containsKey(id)) {
            Object value = objectMap.get(id);
            if (MultiTypeMap.class.isInstance(value)) {
                obj = ((MultiTypeMap) value).get(type);
            } else if (TypedObject.class.isInstance(value)) {
                obj = (TypedObject) value;
            }
        }
        return obj;
    }

    public TypedObject get(String id) {
        TypedObject obj = null;
        if (this.objectMap.containsKey(id)) {
            Object value = objectMap.get(id);
            if (MultiTypeMap.class.isInstance(value)) {
                MultiTypeMap multiMap = (MultiTypeMap) value;
                if (multiMap.size() != 1) {
                    throw new DDTException(
                            "Try to retrieve object out of multiples without type info."
                                    + " Use signature (String id, String type) on call.");
                }
                obj = (TypedObject) ((Entry) multiMap.entrySet().iterator()
                    .next()).getValue();
            } else if (TypedObject.class.isInstance(value)) {
                obj = (TypedObject) value;
            }
        }

        return obj;
    }

    public Set entrySet() {
        return this.objectMap.entrySet();
    }

    public boolean isEmpty() {
        return this.objectMap.isEmpty();
    }

    public int size() {
        int size = 0;
        for (Iterator iter = this.objectMap.entrySet().iterator(); iter
            .hasNext();) {
            Entry objEntry = (Entry) iter.next();
            if (MultiTypeMap.class.isInstance(objEntry.getValue())) {
                size += ((MultiTypeMap) objEntry.getValue()).size();
            } else {
                size += 1;
            }
        }
        return size;
    }

    public Object clone() {
        TypedObjectMap newMap = new TypedObjectMap();
        String key;
        for (Iterator iter = this.entrySet().iterator(); iter.hasNext();) {
            TypedObject obj = null;
            Entry objEntry = (Entry) iter.next();
            key = (String) objEntry.getKey();
            Object value = objEntry.getValue();
            if (MultiTypeMap.class.isInstance(value)) {
                MultiTypeMap typeMap = (MultiTypeMap) value;
                for (Iterator typeIter = typeMap.entrySet().iterator(); typeIter
                    .hasNext();) {
                    Entry typeEntry = (Entry) typeIter.next();
                    obj = (TypedObject) ((TypedObject) typeEntry.getValue())
                        .clone();
                    newMap.put(key, obj);
                }
            } else if (TypedObject.class.isInstance(value)) {
                obj = (TypedObject) ((TypedObject) value).clone();
                newMap.put(key, obj);
            }
        }
        return newMap;
    }

    class MultiTypeMap {
        private Map multiTypeMap;

        public MultiTypeMap() {
            this.multiTypeMap = new HashMap();
        }

        public void put(String type, TypedObject obj) {
            this.multiTypeMap.put(type, obj);
        }

        public TypedObject get(String type) {
            return (TypedObject) this.multiTypeMap.get(type);
        }

        public int size() {
            return this.multiTypeMap.size();
        }

        public Set entrySet() {
            return this.multiTypeMap.entrySet();
        }
    }

}
