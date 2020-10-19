//$Id: ActionFactory.java 218 2006-03-02 23:23:29Z jg_hamburg $
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

import java.util.Map;

import junitx.ddtunit.DDTException;

/**
 * Factory class producing objects extending
 * {@link junitx.ddtunit.data.processing.ActionBase}
 * 
 * @author jg
 */
class ActionFactory {
    private ActionFactory() {
        // no special intiialization
    }

    public static IAction getAction(ActionState state, Map attrMap) {
        IAction action = null;
        if (ActionState.BEAN_CREATION.equals(state)) {
            action = new BeanCreatorAction(attrMap);
        } else if (ActionState.SUBELEMENT_CREATION.equals(state)) {
            action = new SubelementCreatorAction(attrMap);
        } else if (ActionState.CALL_CREATION.equals(state)) {
            action = new CallCreatorAction(attrMap);
        } else if (ActionState.COLLECTION_CREATION.equals(state)) {
            action = new CollectionCreatorAction(attrMap);
        } else if (ActionState.ARRAY_CREATION.equals(state)) {
            action = new ArrayCreatorAction(attrMap);
        } else if (ActionState.ATTRLIST_CREATION.equals(state)) {
            action = new AttributeListCreatorAction(attrMap);
        } else if (ActionState.MAP_CREATION.equals(state)) {
            action = new MapCreatorAction(attrMap);
        } else if (ActionState.CONTENT_CREATION.equals(state)) {
            action = new ContentCreatorAction(attrMap);
        } else if (ActionState.CONSTANT_CREATION.equals(state)) {
            action = new ConstantCreatorAction(attrMap);
        } else if (ActionState.DATE_CREATION.equals(state)) {
            action = new DateCreatorAction(attrMap);
        } else {
            throw new DDTException("Wrong ParseType provided (" + state + ")");

        }
        action.inject();
        return action;
    }
}