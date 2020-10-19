//$Id: ComparableRange.java 250 2006-08-25 21:30:26Z jg_hamburg $
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

public class ComparableRange<T> implements IRange<T> {
    private Comparable<T> start;

    private boolean startIncluded;

    private Comparable<T> end;

    private boolean endIncluded;

    /**
     * Instanciate range and set range delimiting objects as included in Range
     */
    public ComparableRange() {
        this.startIncluded = true;
        this.endIncluded = true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see junitx.ddtunit.data.IRange#isInRange(java.lang.Comparable)
     */
    public boolean isInRange(Comparable<T> actual) {

        if (this.start == null || this.end == null) {
            throw new IllegalArgumentException(
                    "Start and end object for range must be provided.");
        }
        boolean check = false;
        if ((this.startIncluded && this.start.compareTo((T) actual) <= 0)
                || this.start.compareTo((T) actual) < 0) {
            if ((this.endIncluded && this.end.compareTo((T) actual) >= 0)
                    || this.end.compareTo((T) actual) > 0) {
                check = true;
            }
        }
        return check;
    }

    /*
     * (non-Javadoc)
     * 
     * @see junitx.ddtunit.data.IRange#getEnd()
     */
    public Comparable<T> getEnd() {
        return this.end;
    }

    /*
     * (non-Javadoc)
     * 
     * @see junitx.ddtunit.data.IRange#setEnd(java.lang.Comparable)
     */
    public void setEnd(Comparable<T> end) {
        if (this.start != null) {
            if (this.start.compareTo((T) end) > 0) {
                throw new IllegalArgumentException(
                        "End element of range is lower than start");
            }
        }
        this.end = end;
    }

    /*
     * (non-Javadoc)
     * 
     * @see junitx.ddtunit.data.IRange#isEndIncluded()
     */
    public boolean isEndIncluded() {
        return this.endIncluded;
    }

    /*
     * (non-Javadoc)
     * 
     * @see junitx.ddtunit.data.IRange#setEndIncluded(boolean)
     */
    public void setEndIncluded(boolean endIncluded) {
        this.endIncluded = endIncluded;
    }

    /*
     * (non-Javadoc)
     * 
     * @see junitx.ddtunit.data.IRange#getStart()
     */
    public Comparable getStart() {
        return this.start;
    }

    /*
     * (non-Javadoc)
     * 
     * @see junitx.ddtunit.data.IRange#setStart(java.lang.Comparable)
     */
    public void setStart(Comparable<T> start) {
        if (this.end != null) {
            if (this.end.compareTo((T) start) < 0) {
                throw new IllegalArgumentException(
                        "Start element of range is higher than end");
            }
        }
        this.start = start;
    }

    /*
     * (non-Javadoc)
     * 
     * @see junitx.ddtunit.data.IRange#isStartIncluded()
     */
    public boolean isStartIncluded() {
        return this.startIncluded;
    }

    /*
     * (non-Javadoc)
     * 
     * @see junitx.ddtunit.data.IRange#setStartIncluded(boolean)
     */
    public void setStartIncluded(boolean startIncluded) {
        this.startIncluded = startIncluded;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("Range :");
        if (this.startIncluded)
            sb.append("[");
        else
            sb.append("]");
        sb.append(this.start).append(", ").append(end);
        if (this.endIncluded)
            sb.append("]");
        else
            sb.append("[");
        return sb.toString();
    }
}