//$Id: IAction.java 218 2006-03-02 23:23:29Z jg_hamburg $
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
package junitx.ddtunit.data.processing;

import junitx.ddtunit.data.TypedObject;

public interface IAction {
    /**
     * Used to append {@link TypedObject}that should be processed on this
     * action.
     */
    IAction inject();

    /**
     * Create injected object to according type specification on this action.
     * Just do nothing if object allready instanciated.
     * 
     * @return RecordBase
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    TypedObject createObject();

    TypedObject getObject();

    void setObject(TypedObject tObj);

    void setValue(Object obj);

    Object getValue();

    String getId();

    /**
     * do process required action to transition from this state
     */
    IAction process();

    /**
     * Process successor in action stack.
     * 
     * @param successor action in stack
     */
    void processSuccessor(IAction successor);

    /**
     * Process action if no successor exists on action stack.
     */
    void processNoSuccessor();

    IAction getNext();

    void setNext(IAction next);

    IAction getPrevious();

    void setPrevious(IAction previous);

    String getTypeFromRoot();

    String getType();

    void setType(String type);

    /**
     * Extract field type information from object sitting on the stack. <br/>If
     * rootAction is a generated container action used for construction of
     * fields or constructor call of the underlying object then skip the
     * container action and retrieve requested type from underlying object.
     * 
     * @param fieldName to extract type from
     * @return full type of field
     */
    String extractFieldType(String fieldName);

    String getHint();

    /**
     * Insert a new Record entry after this.
     * 
     * @param action to insert
     */
    void insert(IAction action);

    void promoteLinkChangeListener(IAction action);

    void registerLinkChangeListener(ILinkChangeListener listener);

    void removeLinkChangeListener(ILinkChangeListener listener);

    void registerReferenceListener(IReferenceListener listener);

    void removeReferenceListener(IReferenceListener listener);

    /**
     * Retrieve attribute from Action object.
     * 
     * @param key
     * @return value of key
     */
    String getAttribute(String key);

}
