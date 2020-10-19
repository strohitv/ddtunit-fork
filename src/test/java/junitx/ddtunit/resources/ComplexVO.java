//$Id: ComplexVO.java 149 2005-05-26 22:13:10Z jg_hamburg $
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
package junitx.ddtunit.resources;

import java.util.List;
import java.util.Map;

/**
 * Test resource representing nested complex field structures.
 * 
 * @author jg
 */
public class ComplexVO {
    private String text;

    private SimpleVO simpleVO;

    private List collect;
    
    private Map map;

    /**
     * Default constructor
     *  
     */
    public ComplexVO() {
        // no special initialization
    }

    public ComplexVO(SimpleVO simpleVO) {
        this.simpleVO = simpleVO;
    }

    public ComplexVO(String text, List collect) {
        this.text = text;
        this.collect = collect;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("ComplexVO: text=\"").append(
            this.text).append("\", simpleVO=<").append(this.simpleVO).append(
            ">");
        return sb.toString();
    }

    public int hashCode() {
        final int CONST_VAL = 42;
        int hashVal = CONST_VAL + this.text.hashCode();
        hashVal = (CONST_VAL * hashVal) + this.simpleVO.hashCode();
        return hashVal;
    }

    public SimpleVO getSimpleVO() {
        return simpleVO;
    }

    public void setBeanSimpleVO(SimpleVO simpleVO) {
        this.simpleVO = simpleVO;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    /**
     * Returns <code>true</code> if this <code>ComplexVO</code> is the same
     * as the o argument.
     * 
     * @return <code>true</code> if this <code>ComplexVO</code> is the same
     *         as the o argument.
     */
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o.getClass() != getClass()) {
            return false;
        }
        ComplexVO castedObj = (ComplexVO) o;
        return ((this.text == null ? castedObj.text == null : this.text
            .equals(castedObj.text))
                && (this.simpleVO == null ? castedObj.simpleVO == null
                        : this.simpleVO.equals(castedObj.simpleVO)) && (this.collect == null ? castedObj.collect == null
                : this.collect.equals(castedObj.collect)));
    }

    public List getCollect() {
        return collect;
    }

    public void setCollect(List collect) {
        this.collect = collect;
    }
    public Map getMap() {
        return this.map;
    }
}