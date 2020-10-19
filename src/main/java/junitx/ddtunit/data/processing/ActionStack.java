//$Id: ActionStack.java 351 2008-08-14 20:20:56Z jg_hamburg $
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Stack containing all {@link junitx.ddtunit.data.processing.ActionBase}s
 * generated during parsing xml structure.
 * 
 * @author jg
 */
public class ActionStack implements ILinkChangeListener {
    private Logger log = LoggerFactory.getLogger(ActionStack.class);

    private static final String LF = System.getProperty("line.separator");

    protected IAction first;

    protected IAction last;

    public ActionStack() {
        // no special initialization
    }

    /**
     * Push new element on stack
     * 
     * @param action to add
     */
    public void push(IAction action) {
        if (action == null) {
            throw new IllegalArgumentException(
                    "Provided action should not be null");
        }
        if (this.last != null) {
            action.setPrevious(this.last);
            this.last.setNext(action);
            action.setNext(null);
            this.last = action;
        } else {
            this.first = action;
            this.last = action;
            action.setPrevious(null);
            action.setNext(null);
        }
        action.registerLinkChangeListener(this);
    }

    /**
     * Retrieve last element on stack and delete it on stack.
     * 
     * @return last element on stack
     */
    public IAction pop() {
        IAction action = this.last;
        this.last = action.getPrevious();
        if (this.last != null) {
            this.last.setNext(null);
        }
        if (action == this.first) {
            this.first = null;
        }
        action.setPrevious(null);
        action.setNext(null);
        return action;
    }

    /**
     * Retrieve last element on stack without deleting it from stack
     * 
     * @return last element of stack
     */
    public IAction peek() {
        return this.last;
    }

    /**
     * check if record stack is empty.
     * 
     * @return true if no elements on stack
     */
    public boolean isEmpty() {
        boolean check = false;
        if (this.last == null && this.first == null) {
            check = true;
        }
        return check;
    }

    String infoOf() {
        StringBuffer info = new StringBuffer("ActionStack Info:");
        if (this.first == null) {
            info.append(" is Empty");
        } else {
            IAction action = this.first;
            StringBuffer indent = new StringBuffer(" ");
            while (action != null) {
                info.append(LF);
                info.append(indent).append(action.getClass().getName());
                action = action.getNext();
                indent.append(" ");
            }
        }
        return info.toString();
    }

    public IAction process() {
        IAction lastAction = peek();
        log.debug("Processing " + lastAction + " ...");
        return lastAction.process();
    }
}
